package fr.isep.subscout.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isep.subscout.data.local.dao.SubscriptionDao
import fr.isep.subscout.data.local.entity.SubscriptionEntity
import fr.isep.subscout.data.model.Subscription
import fr.isep.subscout.data.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val subscriptionDao: SubscriptionDao
) {

    // Helper functions to map between Domain and Entity
    private fun SubscriptionEntity.toDomain() = Subscription(id, name, amount, renewalDate)
    private fun Subscription.toEntity(userId: String) = SubscriptionEntity(id, userId, name, amount, renewalDate)

    // Single Source of Truth: Room
    // Trigger Sync when observing
    val mySubscriptions: Flow<List<Subscription>> = auth.authStateChangesFlow().flatMapLatest { firebaseUser ->
        if (firebaseUser == null) {
            flowOf(emptyList())
        } else {
            // Trigger sync in background
            CoroutineScope(Dispatchers.IO).launch {
                try {
                     syncSubscriptions(firebaseUser.uid)
                } catch (e: Exception) {
                     e.printStackTrace()
                }
            }
            subscriptionDao.getSubscriptionsForUser(firebaseUser.uid).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    private fun FirebaseAuth.authStateChangesFlow(): Flow<com.google.firebase.auth.FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        addAuthStateListener(listener)
        awaitClose { removeAuthStateListener(listener) }
    }

    suspend fun syncSubscriptions(uid: String) {
        try {
            val snapshot = firestore.collection("users").document(uid).collection("subscriptions").get().await()
            val entities = snapshot.documents.mapNotNull { doc ->
                try {
                    // Manual safely mapping or permissive mapping
                    val name = doc.getString("name") ?: ""
                    val amount = doc.getDouble("amount") ?: 0.0
                    // Handle renewalDate gracefully (support Long or Timestamp)
                    val renewalDate = when (val rd = doc.get("renewalDate")) {
                         is Long -> rd
                         is com.google.firebase.Timestamp -> rd.seconds * 1000
                         else -> System.currentTimeMillis()
                    }
                    SubscriptionEntity(doc.id, uid, name, amount, renewalDate)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            subscriptionDao.insertSubscriptions(entities)
        } catch (e: Exception) {
            e.printStackTrace()
            // Ignore error, rely on local data
        }
    }

    suspend fun addSubscription(name: String, amount: Double, renewalDate: Long) {
        val uid = auth.currentUser?.uid ?: return
        val id = firestore.collection("users").document(uid).collection("subscriptions").document().id
        val subscription = Subscription(id, name, amount, renewalDate)
        
        // 1. Save to Room (Optimistic UI)
        subscriptionDao.insertSubscription(subscription.toEntity(uid))
        
        // 2. Save to Firestore
        try {
            firestore.collection("users").document(uid).collection("subscriptions").document(id).set(subscription).await()
        } catch (e: Exception) {
            e.printStackTrace()
            // In a real app, queue for retry
        }
    }

    suspend fun deleteSubscription(subId: String) {
        val uid = auth.currentUser?.uid ?: return
        
        // 1. Delete from Room
        subscriptionDao.deleteSubscription(subId)
        
        // 2. Delete from Firestore
        try {
            firestore.collection("users").document(uid).collection("subscriptions").document(subId).delete().await()
        } catch (e: Exception) {
             e.printStackTrace()
        }
    }

    suspend fun updateSubscription(subId: String, name: String, amount: Double, renewalDate: Long) {
        val uid = auth.currentUser?.uid ?: return
        
        // 1. Update Room
        subscriptionDao.updateSubscription(subId, name, amount, renewalDate)

        // 2. Update Firestore
        val updates = mapOf(
            "name" to name,
            "amount" to amount,
            "renewalDate" to renewalDate
        )
        try {
            firestore.collection("users").document(uid).collection("subscriptions").document(subId).update(updates).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- Admin Features (Keep as direct Firestore for simplicity, or implement caching if needed) ---
    // The requirement focuses on "subscriptions", which implies "my" subscriptions.

    suspend fun getAllUsers(): List<User> {
        return try {
            firestore.collection("users").get().await().toObjects(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // For Admin to see a specific user's subscriptions
    suspend fun getUserSubscriptions(uid: String): List<Subscription> {
         return try {
            firestore.collection("users").document(uid).collection("subscriptions").get().await().toObjects(Subscription::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

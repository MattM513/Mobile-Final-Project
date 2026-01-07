package fr.isep.subscout.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isep.subscout.data.model.Subscription
import fr.isep.subscout.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    // Get subscriptions for the CURRENT user
    fun getMySubscriptions(): Flow<List<Subscription>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            close(Exception("Not logged in"))
            return@callbackFlow
        }

        val listener = firestore.collection("users").document(uid).collection("subscriptions")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val subs = snapshot?.toObjects(Subscription::class.java) ?: emptyList()
                trySend(subs)
            }
        awaitClose { listener.remove() }
    }

    // Add subscription for CURRENT user (as per requirement: user modifies own subscriptions)
    suspend fun addSubscription(name: String, amount: Double, renewalDate: Long) {
        val uid = auth.currentUser?.uid ?: return
        val subDict = hashMapOf(
            "name" to name,
            "amount" to amount,
            "renewalDate" to renewalDate
        )
        // Auto-ID
        val ref = firestore.collection("users").document(uid).collection("subscriptions").document()
        val sub = Subscription(id = ref.id, name = name, amount = amount, renewalDate = renewalDate)
        ref.set(sub).await()
    }

    suspend fun deleteSubscription(subId: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).collection("subscriptions").document(subId).delete().await()
    }

    suspend fun updateSubscription(subId: String, name: String, amount: Double, renewalDate: Long) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "amount" to amount,
            "renewalDate" to renewalDate
        )
        firestore.collection("users").document(uid).collection("subscriptions").document(subId).update(updates).await()
    }

    // --- Admin Features ---

    suspend fun getAllUsers(): List<User> {
         // This might be heavy if many users, valid request for "Admin user that can modify all users"
         return firestore.collection("users").get().await().toObjects(User::class.java)
    }

    // For Admin to see a specific user's subscriptions
    fun getUserSubscriptions(uid: String): Flow<List<Subscription>> = callbackFlow {
        val listener = firestore.collection("users").document(uid).collection("subscriptions")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val subs = snapshot?.toObjects(Subscription::class.java) ?: emptyList()
                trySend(subs)
            }
        awaitClose { listener.remove() }
    }
}

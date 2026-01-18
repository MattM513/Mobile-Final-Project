package fr.isep.subscout.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey
    val id: String, // Matches Firestore Document ID
    val userId: String, // To separate users
    val name: String,
    val amount: Double,
    val renewalDate: Long
)

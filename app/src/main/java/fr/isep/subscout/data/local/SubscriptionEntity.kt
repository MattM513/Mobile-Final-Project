package fr.isep.subscout.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val amount: Double,
    val currency: String, // "USD", "EUR"
    val renewalDate: Long, // Timestamp
    val iconName: String? = null // For future icon support
)

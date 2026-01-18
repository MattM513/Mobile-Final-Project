package fr.isep.subscout.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.isep.subscout.data.local.dao.SubscriptionDao
import fr.isep.subscout.data.local.entity.SubscriptionEntity

@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}

package fr.isep.subscout.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.isep.subscout.data.local.AppDatabase
import fr.isep.subscout.data.local.dao.SubscriptionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "subscout_database"
        ).fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    fun provideSubscriptionDao(appDatabase: AppDatabase): SubscriptionDao {
        return appDatabase.subscriptionDao()
    }
}

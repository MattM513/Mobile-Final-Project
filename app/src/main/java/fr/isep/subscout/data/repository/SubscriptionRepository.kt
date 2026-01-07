package fr.isep.subscout.data.repository

import android.util.Log
import fr.isep.subscout.data.local.SubscriptionDao
import fr.isep.subscout.data.local.SubscriptionEntity
import fr.isep.subscout.data.remote.ExchangeRateApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    private val exchangeRateApi: ExchangeRateApi
) {

    val allSubscriptions: Flow<List<SubscriptionEntity>> = subscriptionDao.getAllSubscriptions()

    suspend fun addSubscription(name: String, amount: Double, currency: String, renewalDate: Long) {
        var finalAmount = amount
        var finalCurrency = currency

        // Convert to EUR if USD
        if (currency == "USD") {
            try {
                val response = exchangeRateApi.getExchangeRates("USD")
                val rate = response.rates["EUR"]
                if (rate != null) {
                    finalAmount = amount * rate
                    finalCurrency = "EUR" // Store as EUR equivalent or keep original? 
                    // Requirement says: "Fetch current rate and save the equivalent in EUR"
                    // So we save as EUR.
                }
            } catch (e: Exception) {
                Log.e("SubscriptionRepo", "Error converting currency", e)
                // If offline or fails, keep original currency/amount or maybe fail?
                // Requirement: "Ensure the app runs offline (display cached data if API fails)"
                // But for adding, if we cannot convert, maybe we just save as USD?
                // Let's save as is if conversion fails, effectively supporting multi-currency storage but maybe displaying differently.
                // However, tracking total monthly cost implies a single currency.
                // For now, let's assume we store what we have, but if we can't convert, 
                // we might have mixed currencies in DB. The UI should handle it or we should try to convert later.
                // For simplicity, I'll just save it as is if conversion fails.
            }
        }

        val subscription = SubscriptionEntity(
            name = name,
            amount = finalAmount,
            currency = finalCurrency,
            renewalDate = renewalDate
        )
        subscriptionDao.insertSubscription(subscription)
    }

    suspend fun deleteSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.deleteSubscription(subscription)
    }
}

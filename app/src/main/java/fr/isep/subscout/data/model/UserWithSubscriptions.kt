package fr.isep.subscout.data.model

data class UserWithSubscriptions(
    val user: User,
    val subscriptions: List<Subscription>
)

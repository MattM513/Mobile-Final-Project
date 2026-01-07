package fr.isep.subscout.data.model

data class Subscription(
    val id: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val renewalDate: Long = 0L
)

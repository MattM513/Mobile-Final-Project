package fr.isep.subscout.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val role: String = "user" // "admin" or "user"
)

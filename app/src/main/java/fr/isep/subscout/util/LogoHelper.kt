package fr.isep.subscout.util

import fr.isep.subscout.R

object LogoHelper {
    fun getLogoResId(subscriptionName: String): Int? {
        val name = subscriptionName.lowercase().trim()
        return when {
            name.contains("netflix") -> R.drawable.ic_netflix
            name.contains("spotify") -> R.drawable.ic_spotify
            name.contains("youtube") -> R.drawable.ic_youtube
            name.contains("amazon") || name.contains("prime") -> R.drawable.ic_prime_video
            name.contains("deezer") -> R.drawable.ic_deezer
            name.contains("uber") -> R.drawable.ic_uber
            name.contains("crunchyroll") -> R.drawable.ic_crunchyroll
            name.contains("disney") -> R.drawable.ic_disney_plus
            else -> null // No logo found
        }
    }
}

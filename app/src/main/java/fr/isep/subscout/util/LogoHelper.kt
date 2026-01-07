package fr.isep.subscout.util

object LogoHelper {
    fun getLogoUrl(subscriptionName: String): String {
        // Simple heuristic: Google Favicon API or Clearbit Logo API
        // Clearbit is often used for company logos: https://logo.clearbit.com/netflix.com
        // But for generic names like "Netflix", we need a domain.
        // Let's try a simple mapping for common ones, and a generic lookup for others (or just a placeholder)
        
        val name = subscriptionName.lowercase().trim()
        return when {
            name.contains("netflix") -> "https://logo.clearbit.com/netflix.com"
            name.contains("spotify") -> "https://logo.clearbit.com/spotify.com"
            name.contains("youtube") -> "https://logo.clearbit.com/youtube.com"
            name.contains("amazon") || name.contains("prime") -> "https://logo.clearbit.com/amazon.com"
            name.contains("hulu") -> "https://logo.clearbit.com/hulu.com"
            name.contains("disney") -> "https://logo.clearbit.com/disneyplus.com"
            name.contains("apple") -> "https://logo.clearbit.com/apple.com"
            name.contains("hbo") || name.contains("max") -> "https://logo.clearbit.com/hbomax.com"
            else -> "https://ui-avatars.com/api/?name=$subscriptionName&background=random&color=fff&size=128"
        }
    }
}

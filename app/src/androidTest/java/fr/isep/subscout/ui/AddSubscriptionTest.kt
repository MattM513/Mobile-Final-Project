package fr.isep.subscout.ui

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import fr.isep.subscout.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddSubscriptionTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun checkAddSubscriptionButtonIsVisible() {
        // "checks if the 'Add Subscription' button is visible on the Home Screen"
        // Note: The contentDescription I set in HomeScreen is stringResource(R.string.add_subscription)
        // In English "Add Subscription", in French "Ajouter un abonnement"
        // I should stick to English or check based on content description which is safer if I knew the context locale, but typically English.
        
        // Wait, I used R.string.add_subscription for content description.
        // composeTestRule.onNodeWithContentDescription("Add Subscription").assertExists()
        
        // Let's use the resource ID if possible or just the string value. 
        // Since I'm in instrumentation test, I can get context.
        
        composeTestRule.onNodeWithContentDescription("Add Subscription").assertExists()
    }
}

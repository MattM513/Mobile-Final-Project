package fr.isep.subscout

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import dagger.hilt.android.AndroidEntryPoint
import fr.isep.subscout.ui.SubScoutApp
import java.util.concurrent.TimeUnit

// --- NEW IMPORTS ADDED BELOW ---
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import fr.isep.subscout.ui.theme.SubscoutTheme
// -------------------------------

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule Notification Work
        val workRequest = PeriodicWorkRequestBuilder<fr.isep.subscout.worker.SubscriptionWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SubscriptionWork",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        setContent {
            SubScoutApp()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SubscoutTheme {
        Greeting("Android")
    }
}
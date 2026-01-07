package fr.isep.subscout.ui.edit

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import fr.isep.subscout.ui.MainViewModel
import fr.isep.subscout.util.LogoHelper
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    subscriptionId: String,
    onBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    // Collect subscriptions to find the one we are editing
    val subscriptions by viewModel.mySubscriptions.collectAsState()
    val subscription = subscriptions.find { it.id == subscriptionId }

    var name by remember(subscription) { mutableStateOf(subscription?.name ?: "") }
    var amount by remember(subscription) { mutableStateOf(subscription?.amount?.toString() ?: "") }
    var renewalDate by remember(subscription) { mutableLongStateOf(subscription?.renewalDate ?: System.currentTimeMillis()) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = renewalDate

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            renewalDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Subscription") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (subscription == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Subscription not found")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo Preview
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = LogoHelper.getLogoUrl(name),
                        contentDescription = "Logo",
                        modifier = Modifier.size(80.dp)
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (EUR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Text("Renewal: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(Date(renewalDate))}")
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (name.isNotBlank() && amount.isNotBlank()) {
                            viewModel.updateSubscription(
                                subId = subscriptionId,
                                name = name,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                renewalDate = renewalDate
                            )
                            onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}

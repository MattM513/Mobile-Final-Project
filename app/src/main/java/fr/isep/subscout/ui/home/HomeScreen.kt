package fr.isep.subscout.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.isep.subscout.R
import fr.isep.subscout.data.model.Subscription
import fr.isep.subscout.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onAdminClick: () -> Unit,
    onLogout: () -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val subscriptions by viewModel.mySubscriptions.collectAsState()
    val totalCost = subscriptions.sumOf { it.amount }
    
    val userRole by viewModel.userRole.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.app_logo), contentDescription = null, modifier = Modifier.size(32.dp))
                         Spacer(modifier = Modifier.width(8.dp))
                         Text(stringResource(R.string.home_title)) 
                    }
                },
                actions = {
                    if (userRole == "admin") {
                        TextButton(onClick = onAdminClick) {
                            Text("Admin")
                        }
                    }
                    IconButton(onClick = {
                        viewModel.signOut { onLogout() }
                    }) {
                         // Using a nicer icon if possible, or text
                        Text("Logout") 
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_subscription))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Total Cost Card with nice gradient or color
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Monthly Expenses",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format("%.2f EUR", totalCost),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (subscriptions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_subscriptions), style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subscriptions) { subscription ->
                        SubscriptionItem(
                            subscription = subscription,
                            onClick = { onEditClick(subscription.id) },
                            onDelete = { viewModel.deleteSubscription(subscription.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubscriptionItem(
    subscription: Subscription,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo
            coil.compose.AsyncImage(
                model = fr.isep.subscout.util.LogoHelper.getLogoUrl(subscription.name),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = subscription.name, style = MaterialTheme.typography.titleMedium)
                
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = "Next: ${dateFormat.format(Date(subscription.renewalDate))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.2f", subscription.amount)} €",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, 
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

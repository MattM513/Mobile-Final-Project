package fr.isep.subscout.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import fr.isep.subscout.data.model.Subscription
import fr.isep.subscout.data.model.User
import fr.isep.subscout.data.model.UserWithSubscriptions
import fr.isep.subscout.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val usersData by viewModel.adminUsersData.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(usersData) { data ->
                UserItem(data)
            }
        }
    }
}

@Composable
fun UserItem(data: UserWithSubscriptions) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("User: ${data.user.email}", style = MaterialTheme.typography.titleMedium)
            Text("Role: ${data.user.role}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            if (data.subscriptions.isEmpty()) {
                Text("No subscriptions", style = MaterialTheme.typography.bodySmall)
            } else {
                data.subscriptions.forEach { sub ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("- ${sub.name}", style = MaterialTheme.typography.bodyMedium)
                        Text("${sub.amount} €", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

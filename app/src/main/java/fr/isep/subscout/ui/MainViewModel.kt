package fr.isep.subscout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.isep.subscout.data.model.Subscription
import fr.isep.subscout.data.model.User
import fr.isep.subscout.data.model.UserWithSubscriptions
import fr.isep.subscout.data.repository.AuthRepository
import fr.isep.subscout.data.repository.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userRole = MutableStateFlow("user")
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    // Subscriptions for the *current* logged in user
    // Subscriptions for the *current* logged in user
    val mySubscriptions: StateFlow<List<Subscription>> = subscriptionRepository.mySubscriptions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin: List of all users
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers.asStateFlow()

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        val user = authRepository.currentUser
        _currentUser.value = user
        if (user != null) {
            viewModelScope.launch {
                try {
                    _userRole.value = authRepository.getUserRole(user.uid)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _userRole.value = "user" // Default to basic user on error
                }
            }
        }
    }

    fun signIn(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            authRepository.login(email, pass)
                .onSuccess {
                    checkAuthStatus()
                    onSuccess()
                }
                .onFailure { onError(it.message ?: "Login failed") }
        }
    }

    fun signUp(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            authRepository.signUp(email, pass)
                .onSuccess {
                    checkAuthStatus()
                    onSuccess()
                }
                .onFailure { onError(it.message ?: "Sign Up failed") }
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            onComplete()
        }
    }

    fun addSubscription(name: String, amount: Double, renewalDate: Long) {
        viewModelScope.launch {
            subscriptionRepository.addSubscription(name, amount, renewalDate)
        }
    }

    fun deleteSubscription(subId: String) {
        viewModelScope.launch {
            subscriptionRepository.deleteSubscription(subId)
        }
    }

    fun updateSubscription(subId: String, name: String, amount: Double, renewalDate: Long) {
        viewModelScope.launch {
            subscriptionRepository.updateSubscription(subId, name, amount, renewalDate)
        }
    }

    // Admin: List of all users with their subscriptions
    private val _adminUsersData = MutableStateFlow<List<UserWithSubscriptions>>(emptyList())
    val adminUsersData: StateFlow<List<UserWithSubscriptions>> = _adminUsersData.asStateFlow()

    // --- Admin Functions ---
    fun loadAllUsers() {
        viewModelScope.launch {
             val users = subscriptionRepository.getAllUsers()
             val fullData = users.map { user ->
                 val subs = subscriptionRepository.getUserSubscriptions(user.uid)
                 UserWithSubscriptions(user, subs)
             }
             _adminUsersData.value = fullData
        }
    }
}

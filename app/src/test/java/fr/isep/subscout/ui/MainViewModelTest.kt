package fr.isep.subscout.ui

import fr.isep.subscout.data.local.SubscriptionEntity
import fr.isep.subscout.data.repository.SubscriptionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MainViewModelTest {

    @Mock
    private lateinit var repository: SubscriptionRepository

    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        `when`(repository.allSubscriptions).thenReturn(flowOf(emptyList()))
        
        viewModel = MainViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        val subscriptions = viewModel.subscriptions.value
        assertEquals(emptyList<SubscriptionEntity>(), subscriptions)
    }

    // Add more tests as needed, e.g. verifying addSubscription calls repository
}

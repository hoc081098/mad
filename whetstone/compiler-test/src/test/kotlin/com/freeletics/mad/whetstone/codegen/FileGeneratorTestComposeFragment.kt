package com.freeletics.mad.whetstone.codegen

import com.freeletics.mad.whetstone.ComposableParameter
import com.freeletics.mad.whetstone.ComposeFragmentData
import com.freeletics.mad.whetstone.NavEntryData
import com.freeletics.mad.whetstone.Navigation
import com.squareup.kotlinpoet.ClassName
import org.junit.Test

internal class FileGeneratorTestComposeFragment {

    private val navigation = Navigation.Fragment(
        route = ClassName("com.test", "TestRoute"),
        destinationType = "NONE",
        destinationScope = ClassName("com.test.destination", "TestDestinationScope"),
    )

    private val data = ComposeFragmentData(
        baseName = "Test",
        packageName = "com.test",
        scope = ClassName("com.test", "TestScreen"),
        parentScope = ClassName("com.test.parent", "TestParentScope"),
        stateMachine = ClassName("com.test", "TestStateMachine"),
        fragmentBaseClass = ClassName("androidx.fragment.app", "Fragment"),
        navigation = null,
        navEntryData = null,
        composableParameter = emptyList()
    )

    private val navEntryData = NavEntryData(
        packageName = "com.test",
        scope = ClassName("com.test", "TestScreen"),
        parentScope = ClassName("com.test.parent", "TestParentScope"),
        navigation = navigation,
    )

    @Test
    fun `generates code for ComposeFragmentData`() {

        val expected = """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.compose.ui.platform.ViewCompositionStrategy
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.whetstone.ScopeTo
            import com.freeletics.mad.whetstone.`internal`.ComposeProviderValueModule
            import com.freeletics.mad.whetstone.`internal`.InternalWhetstoneApi
            import com.freeletics.mad.whetstone.`internal`.asComposeState
            import com.freeletics.mad.whetstone.fragment.`internal`.viewModel
            import com.squareup.anvil.annotations.ContributesSubcomponent
            import com.squareup.anvil.annotations.ContributesTo
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Module
            import dagger.multibindings.Multibinds
            import java.io.Closeable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlin.collections.Set
            import kotlinx.coroutines.launch

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
              modules = [ComposeProviderValueModule::class],
            )
            public interface WhetstoneTestComponent {
              public val testStateMachine: TestStateMachine

              public val closeables: Set<Closeable>

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance savedStateHandle: SavedStateHandle, @BindsInstance
                    arguments: Bundle): WhetstoneTestComponent
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTestComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTestModule {
              @Multibinds
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTestViewModel(
              parentComponent: WhetstoneTestComponent.ParentComponent,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle,
            ) : ViewModel() {
              public val component: WhetstoneTestComponent =
                  parentComponent.whetstoneTestComponentFactory().create(savedStateHandle, arguments)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }
            
            @OptIn(InternalWhetstoneApi::class)
            public class WhetstoneTestFragment : Fragment() {
              private lateinit var whetstoneTestComponent: WhetstoneTestComponent
            
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
              ): View {
                if (!::whetstoneTestComponent.isInitialized) {
                  val arguments = requireArguments()
                  val viewModel = viewModel(TestParentScope::class, arguments, ::WhetstoneTestViewModel)
                  whetstoneTestComponent = viewModel.component
                }

                return ComposeView(requireContext()).apply {
                  setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))

                  setContent {
                    WhetstoneTest(whetstoneTestComponent)
                  }
                }
              }
            }

            @Composable
            @OptIn(InternalWhetstoneApi::class)
            private fun WhetstoneTest(component: WhetstoneTestComponent): Unit {
              val stateMachine = component.testStateMachine
              val state = stateMachine.asComposeState()
              val currentState = state.value
              if (currentState != null) {
                val scope = rememberCoroutineScope()
                Test(
                  state = currentState,
                  sendAction = { scope.launch { stateMachine.dispatch(it) } },
                )
              }
            }
            
        """.trimIndent()

        test(data, expected)
    }

    @Test
    fun `generates code for ComposeFragmentData with navigation`() {
        val withNavigation = data.copy(navigation = navigation)

        val expected = """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.compose.ui.platform.ViewCompositionStrategy
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.navigator.NavEventNavigator
            import com.freeletics.mad.navigator.fragment.handleNavigation
            import com.freeletics.mad.navigator.fragment.requireRoute
            import com.freeletics.mad.whetstone.ScopeTo
            import com.freeletics.mad.whetstone.`internal`.ComposeProviderValueModule
            import com.freeletics.mad.whetstone.`internal`.InternalWhetstoneApi
            import com.freeletics.mad.whetstone.`internal`.asComposeState
            import com.freeletics.mad.whetstone.fragment.`internal`.viewModel
            import com.squareup.anvil.annotations.ContributesSubcomponent
            import com.squareup.anvil.annotations.ContributesTo
            import com.test.destination.TestDestinationScope
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Module
            import dagger.multibindings.Multibinds
            import java.io.Closeable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlin.collections.Set
            import kotlinx.coroutines.launch

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
              modules = [ComposeProviderValueModule::class],
            )
            public interface WhetstoneTestComponent {
              public val testStateMachine: TestStateMachine

              public val navEventNavigator: NavEventNavigator

              public val closeables: Set<Closeable>

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance savedStateHandle: SavedStateHandle, @BindsInstance
                    testRoute: TestRoute): WhetstoneTestComponent
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTestComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTestModule {
              @Multibinds
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTestViewModel(
              parentComponent: WhetstoneTestComponent.ParentComponent,
              savedStateHandle: SavedStateHandle,
              testRoute: TestRoute,
            ) : ViewModel() {
              public val component: WhetstoneTestComponent =
                  parentComponent.whetstoneTestComponentFactory().create(savedStateHandle, testRoute)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }
            
            @OptIn(InternalWhetstoneApi::class)
            public class WhetstoneTestFragment : Fragment() {
              private lateinit var whetstoneTestComponent: WhetstoneTestComponent
            
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
              ): View {
                if (!::whetstoneTestComponent.isInitialized) {
                  val testRoute = requireRoute<TestRoute>()
                  val viewModel = viewModel(TestParentScope::class, TestDestinationScope::class, testRoute,
                      ::WhetstoneTestViewModel)
                  whetstoneTestComponent = viewModel.component
            
                  handleNavigation(this, whetstoneTestComponent.navEventNavigator)
                }

                return ComposeView(requireContext()).apply {
                  setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))

                  setContent {
                    WhetstoneTest(whetstoneTestComponent)
                  }
                }
              }
            }

            @Composable
            @OptIn(InternalWhetstoneApi::class)
            private fun WhetstoneTest(component: WhetstoneTestComponent): Unit {
              val stateMachine = component.testStateMachine
              val state = stateMachine.asComposeState()
              val currentState = state.value
              if (currentState != null) {
                val scope = rememberCoroutineScope()
                Test(
                  state = currentState,
                  sendAction = { scope.launch { stateMachine.dispatch(it) } },
                )
              }
            }
            
        """.trimIndent()

        test(withNavigation, expected)
    }

    @Test
    fun `generates code for ComposeFragmentData with navigation and destination`() {
        val withDestination = data.copy(navigation = navigation.copy(destinationType = "SCREEN"))

        val expected = """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.compose.ui.platform.ViewCompositionStrategy
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.navigator.NavEventNavigator
            import com.freeletics.mad.navigator.fragment.NavDestination
            import com.freeletics.mad.navigator.fragment.ScreenDestination
            import com.freeletics.mad.navigator.fragment.handleNavigation
            import com.freeletics.mad.navigator.fragment.requireRoute
            import com.freeletics.mad.whetstone.ScopeTo
            import com.freeletics.mad.whetstone.`internal`.ComposeProviderValueModule
            import com.freeletics.mad.whetstone.`internal`.InternalWhetstoneApi
            import com.freeletics.mad.whetstone.`internal`.asComposeState
            import com.freeletics.mad.whetstone.fragment.`internal`.viewModel
            import com.squareup.anvil.annotations.ContributesSubcomponent
            import com.squareup.anvil.annotations.ContributesTo
            import com.test.destination.TestDestinationScope
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Module
            import dagger.Provides
            import dagger.multibindings.IntoSet
            import dagger.multibindings.Multibinds
            import java.io.Closeable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlin.collections.Set
            import kotlinx.coroutines.launch

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
              modules = [ComposeProviderValueModule::class],
            )
            public interface WhetstoneTestComponent {
              public val testStateMachine: TestStateMachine

              public val navEventNavigator: NavEventNavigator

              public val closeables: Set<Closeable>

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance savedStateHandle: SavedStateHandle, @BindsInstance
                    testRoute: TestRoute): WhetstoneTestComponent
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTestComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTestModule {
              @Multibinds
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTestViewModel(
              parentComponent: WhetstoneTestComponent.ParentComponent,
              savedStateHandle: SavedStateHandle,
              testRoute: TestRoute,
            ) : ViewModel() {
              public val component: WhetstoneTestComponent =
                  parentComponent.whetstoneTestComponentFactory().create(savedStateHandle, testRoute)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }
            
            @OptIn(InternalWhetstoneApi::class)
            public class WhetstoneTestFragment : Fragment() {
              private lateinit var whetstoneTestComponent: WhetstoneTestComponent
            
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
              ): View {
                if (!::whetstoneTestComponent.isInitialized) {
                  val testRoute = requireRoute<TestRoute>()
                  val viewModel = viewModel(TestParentScope::class, TestDestinationScope::class, testRoute,
                      ::WhetstoneTestViewModel)
                  whetstoneTestComponent = viewModel.component
            
                  handleNavigation(this, whetstoneTestComponent.navEventNavigator)
                }
            
                return ComposeView(requireContext()).apply {
                  setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))

                  setContent {
                    WhetstoneTest(whetstoneTestComponent)
                  }
                }
              }
            }

            @Composable
            @OptIn(InternalWhetstoneApi::class)
            private fun WhetstoneTest(component: WhetstoneTestComponent): Unit {
              val stateMachine = component.testStateMachine
              val state = stateMachine.asComposeState()
              val currentState = state.value
              if (currentState != null) {
                val scope = rememberCoroutineScope()
                Test(
                  state = currentState,
                  sendAction = { scope.launch { stateMachine.dispatch(it) } },
                )
              }
            }
            
            @Module
            @ContributesTo(TestDestinationScope::class)
            public object WhetstoneTestNavDestinationModule {
              @Provides
              @IntoSet
              public fun provideNavDestination(): NavDestination = ScreenDestination<TestRoute,
                  WhetstoneTestFragment>()
            }
            
        """.trimIndent()

        test(withDestination, expected)
    }

    @Test
    fun `generates code for ComposeFragmentData, with navigation, destination and navEntry`() {
        val withDestinationAndNavEntry = data.copy(
            navigation = navigation.copy(destinationType = "SCREEN"),
            navEntryData = navEntryData
        )

        val expected = """
            package com.test

            import android.content.Context
            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.compose.ui.platform.ViewCompositionStrategy
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import androidx.navigation.NavBackStackEntry
            import com.freeletics.mad.navigator.NavEventNavigator
            import com.freeletics.mad.navigator.`internal`.InternalNavigatorApi
            import com.freeletics.mad.navigator.`internal`.destinationId
            import com.freeletics.mad.navigator.fragment.NavDestination
            import com.freeletics.mad.navigator.fragment.ScreenDestination
            import com.freeletics.mad.navigator.fragment.handleNavigation
            import com.freeletics.mad.navigator.fragment.requireRoute
            import com.freeletics.mad.whetstone.NavEntry
            import com.freeletics.mad.whetstone.ScopeTo
            import com.freeletics.mad.whetstone.`internal`.ComposeProviderValueModule
            import com.freeletics.mad.whetstone.`internal`.DestinationComponent
            import com.freeletics.mad.whetstone.`internal`.InternalWhetstoneApi
            import com.freeletics.mad.whetstone.`internal`.NavEntryComponentGetter
            import com.freeletics.mad.whetstone.`internal`.NavEntryComponentGetterKey
            import com.freeletics.mad.whetstone.`internal`.asComposeState
            import com.freeletics.mad.whetstone.fragment.`internal`.viewModel
            import com.squareup.anvil.annotations.ContributesMultibinding
            import com.squareup.anvil.annotations.ContributesSubcomponent
            import com.squareup.anvil.annotations.ContributesTo
            import com.test.destination.TestDestinationScope
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Module
            import dagger.Provides
            import dagger.multibindings.IntoSet
            import dagger.multibindings.Multibinds
            import java.io.Closeable
            import javax.inject.Inject
            import kotlin.Any
            import kotlin.Int
            import kotlin.OptIn
            import kotlin.Unit
            import kotlin.collections.Set
            import kotlinx.coroutines.launch
            import com.freeletics.mad.navigator.`internal`.requireRoute as bundleRequireRoute

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
              modules = [ComposeProviderValueModule::class],
            )
            public interface WhetstoneTestComponent {
              public val testStateMachine: TestStateMachine

              public val navEventNavigator: NavEventNavigator

              public val closeables: Set<Closeable>

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance savedStateHandle: SavedStateHandle, @BindsInstance
                    testRoute: TestRoute): WhetstoneTestComponent
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTestComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTestModule {
              @Multibinds
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTestViewModel(
              parentComponent: WhetstoneTestComponent.ParentComponent,
              savedStateHandle: SavedStateHandle,
              testRoute: TestRoute,
            ) : ViewModel() {
              public val component: WhetstoneTestComponent =
                  parentComponent.whetstoneTestComponentFactory().create(savedStateHandle, testRoute)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }

            @OptIn(InternalWhetstoneApi::class)
            public class WhetstoneTestFragment : Fragment() {
              private lateinit var whetstoneTestComponent: WhetstoneTestComponent

              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
              ): View {
                if (!::whetstoneTestComponent.isInitialized) {
                  val testRoute = requireRoute<TestRoute>()
                  val viewModel = viewModel(TestParentScope::class, TestDestinationScope::class, testRoute,
                      ::WhetstoneTestViewModel)
                  whetstoneTestComponent = viewModel.component

                  handleNavigation(this, whetstoneTestComponent.navEventNavigator)
                }

                return ComposeView(requireContext()).apply {
                  setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))

                  setContent {
                    WhetstoneTest(whetstoneTestComponent)
                  }
                }
              }
            }

            @Composable
            @OptIn(InternalWhetstoneApi::class)
            private fun WhetstoneTest(component: WhetstoneTestComponent): Unit {
              val stateMachine = component.testStateMachine
              val state = stateMachine.asComposeState()
              val currentState = state.value
              if (currentState != null) {
                val scope = rememberCoroutineScope()
                Test(
                  state = currentState,
                  sendAction = { scope.launch { stateMachine.dispatch(it) } },
                )
              }
            }

            @Module
            @ContributesTo(TestDestinationScope::class)
            public object WhetstoneTestNavDestinationModule {
              @Provides
              @IntoSet
              public fun provideNavDestination(): NavDestination = ScreenDestination<TestRoute,
                  WhetstoneTestFragment>()
            }

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
            )
            public interface WhetstoneTestScreenNavEntryComponent {
              @get:NavEntry(TestScreen::class)
              public val closeables: Set<Closeable>

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance @NavEntry(TestScreen::class)
                    savedStateHandle: SavedStateHandle, @BindsInstance @NavEntry(TestScreen::class)
                    testRoute: TestRoute): WhetstoneTestScreenNavEntryComponent
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTestScreenNavEntryComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTestScreenNavEntryModule {
              @Multibinds
              @NavEntry(TestScreen::class)
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTestScreenNavEntryViewModel(
              parentComponent: WhetstoneTestScreenNavEntryComponent.ParentComponent,
              savedStateHandle: SavedStateHandle,
              testRoute: TestRoute,
            ) : ViewModel() {
              public val component: WhetstoneTestScreenNavEntryComponent =
                  parentComponent.whetstoneTestScreenNavEntryComponentFactory().create(savedStateHandle,
                  testRoute)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }

            @OptIn(InternalWhetstoneApi::class)
            @NavEntryComponentGetterKey(TestScreen::class)
            @ContributesMultibinding(
              TestDestinationScope::class,
              NavEntryComponentGetter::class,
            )
            public class TestScreenNavEntryComponentGetter @Inject constructor() : NavEntryComponentGetter {
              @OptIn(InternalWhetstoneApi::class, InternalNavigatorApi::class)
              public override fun retrieve(findEntry: (Int) -> NavBackStackEntry, context: Context): Any {
                val entry = findEntry(TestRoute::class.destinationId())
                val route: TestRoute = entry.arguments.bundleRequireRoute()
                val viewModel = com.freeletics.mad.whetstone.`internal`.viewModel(entry, context,
                    TestParentScope::class, TestDestinationScope::class, route, findEntry,
                    ::WhetstoneTestScreenNavEntryViewModel)
                return viewModel.component
              }
            }

            @ContributesTo(TestDestinationScope::class)
            @OptIn(InternalWhetstoneApi::class)
            public interface WhetstoneTestScreenNavEntryDestinationComponent : DestinationComponent

        """.trimIndent()

        test(withDestinationAndNavEntry, expected)
    }

    @Test
    fun `generates code for ComposeFragmentData, dialog fragment`() {
        val dialogFragment = data.copy(
            fragmentBaseClass = ClassName("androidx.fragment.app", "DialogFragment")
        )

        val expected = """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.compose.ui.platform.ViewCompositionStrategy
            import androidx.fragment.app.DialogFragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.whetstone.ScopeTo
            import com.freeletics.mad.whetstone.`internal`.ComposeProviderValueModule
            import com.freeletics.mad.whetstone.`internal`.InternalWhetstoneApi
            import com.freeletics.mad.whetstone.`internal`.asComposeState
            import com.freeletics.mad.whetstone.fragment.`internal`.viewModel
            import com.squareup.anvil.annotations.ContributesSubcomponent
            import com.squareup.anvil.annotations.ContributesTo
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Module
            import dagger.multibindings.Multibinds
            import java.io.Closeable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlin.collections.Set
            import kotlinx.coroutines.launch

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
              modules = [ComposeProviderValueModule::class],
            )
            public interface WhetstoneTestComponent {
              public val testStateMachine: TestStateMachine

              public val closeables: Set<Closeable>

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance savedStateHandle: SavedStateHandle, @BindsInstance
                    arguments: Bundle): WhetstoneTestComponent
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTestComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTestModule {
              @Multibinds
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTestViewModel(
              parentComponent: WhetstoneTestComponent.ParentComponent,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle,
            ) : ViewModel() {
              public val component: WhetstoneTestComponent =
                  parentComponent.whetstoneTestComponentFactory().create(savedStateHandle, arguments)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }
            
            @OptIn(InternalWhetstoneApi::class)
            public class WhetstoneTestFragment : DialogFragment() {
              private lateinit var whetstoneTestComponent: WhetstoneTestComponent
            
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
              ): View {
                if (!::whetstoneTestComponent.isInitialized) {
                  val arguments = requireArguments()
                  val viewModel = viewModel(TestParentScope::class, arguments, ::WhetstoneTestViewModel)
                  whetstoneTestComponent = viewModel.component
                }

                return ComposeView(requireContext()).apply {
                  setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))

                  setContent {
                    WhetstoneTest(whetstoneTestComponent)
                  }
                }
              }
            }

            @Composable
            @OptIn(InternalWhetstoneApi::class)
            private fun WhetstoneTest(component: WhetstoneTestComponent): Unit {
              val stateMachine = component.testStateMachine
              val state = stateMachine.asComposeState()
              val currentState = state.value
              if (currentState != null) {
                val scope = rememberCoroutineScope()
                Test(
                  state = currentState,
                  sendAction = { scope.launch { stateMachine.dispatch(it) } },
                )
              }
            }

        """.trimIndent()

        test(dialogFragment, expected)
    }

    @Test
    fun `generates code for ComposeFragmentData with Composable Depedencies`() {
        val withInjectedParameters = data.copy(
            baseName = "Test2",
            composableParameter = listOf(
                ComposableParameter(
                    name = "testClass",
                    className = ClassName("com.test", "TestClass"),
                ),
                ComposableParameter(
                    name = "test",
                    className = ClassName("com.test.other", "TestClass2"),
                )
            )
        )

        val expected = """
            package com.test

            import android.os.Bundle
            import android.view.LayoutInflater
            import android.view.View
            import android.view.ViewGroup
            import androidx.compose.runtime.Composable
            import androidx.compose.runtime.rememberCoroutineScope
            import androidx.compose.ui.platform.ComposeView
            import androidx.compose.ui.platform.ViewCompositionStrategy
            import androidx.fragment.app.Fragment
            import androidx.lifecycle.SavedStateHandle
            import androidx.lifecycle.ViewModel
            import com.freeletics.mad.whetstone.ScopeTo
            import com.freeletics.mad.whetstone.`internal`.ComposeProviderValueModule
            import com.freeletics.mad.whetstone.`internal`.InternalWhetstoneApi
            import com.freeletics.mad.whetstone.`internal`.asComposeState
            import com.freeletics.mad.whetstone.fragment.`internal`.viewModel
            import com.squareup.anvil.annotations.ContributesSubcomponent
            import com.squareup.anvil.annotations.ContributesTo
            import com.test.other.TestClass2
            import com.test.parent.TestParentScope
            import dagger.BindsInstance
            import dagger.Module
            import dagger.multibindings.Multibinds
            import java.io.Closeable
            import kotlin.OptIn
            import kotlin.Unit
            import kotlin.collections.Set
            import kotlinx.coroutines.launch

            @OptIn(InternalWhetstoneApi::class)
            @ScopeTo(TestScreen::class)
            @ContributesSubcomponent(
              scope = TestScreen::class,
              parentScope = TestParentScope::class,
              modules = [ComposeProviderValueModule::class],
            )
            public interface WhetstoneTest2Component {
              public val testStateMachine: TestStateMachine

              public val closeables: Set<Closeable>

              public val testClass: TestClass

              public val testClass2: TestClass2

              @ContributesSubcomponent.Factory
              public interface Factory {
                public fun create(@BindsInstance savedStateHandle: SavedStateHandle, @BindsInstance
                    arguments: Bundle): WhetstoneTest2Component
              }

              @ContributesTo(TestParentScope::class)
              public interface ParentComponent {
                public fun whetstoneTest2ComponentFactory(): Factory
              }
            }

            @Module
            @ContributesTo(TestScreen::class)
            public interface WhetstoneTest2Module {
              @Multibinds
              public fun bindCloseables(): Set<Closeable>
            }

            @InternalWhetstoneApi
            internal class WhetstoneTest2ViewModel(
              parentComponent: WhetstoneTest2Component.ParentComponent,
              savedStateHandle: SavedStateHandle,
              arguments: Bundle,
            ) : ViewModel() {
              public val component: WhetstoneTest2Component =
                  parentComponent.whetstoneTest2ComponentFactory().create(savedStateHandle, arguments)

              public override fun onCleared(): Unit {
                component.closeables.forEach {
                  it.close()
                }
              }
            }
            
            @OptIn(InternalWhetstoneApi::class)
            public class WhetstoneTest2Fragment : Fragment() {
              private lateinit var whetstoneTest2Component: WhetstoneTest2Component
            
              public override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?,
              ): View {
                if (!::whetstoneTest2Component.isInitialized) {
                  val arguments = requireArguments()
                  val viewModel = viewModel(TestParentScope::class, arguments, ::WhetstoneTest2ViewModel)
                  whetstoneTest2Component = viewModel.component
                }

                return ComposeView(requireContext()).apply {
                  setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner))

                  setContent {
                    WhetstoneTest2(whetstoneTest2Component)
                  }
                }
              }
            }

            @Composable
            @OptIn(InternalWhetstoneApi::class)
            private fun WhetstoneTest2(component: WhetstoneTest2Component): Unit {
              val testClass = component.testClass
              val testClass2 = component.testClass2
              val stateMachine = component.testStateMachine
              val state = stateMachine.asComposeState()
              val currentState = state.value
              if (currentState != null) {
                val scope = rememberCoroutineScope()
                Test2(
                  testClass = testClass,
                  test = testClass2,
                  state = currentState,
                  sendAction = { scope.launch { stateMachine.dispatch(it) } },
                )
              }
            }
            
        """.trimIndent()

        test(withInjectedParameters, expected)
    }
}
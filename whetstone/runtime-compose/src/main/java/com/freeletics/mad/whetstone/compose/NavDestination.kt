package com.freeletics.mad.whetstone.compose

import com.freeletics.mad.navigator.NavRoot
import com.freeletics.mad.navigator.NavRoute
import com.freeletics.mad.whetstone.internal.ObsoleteWhetstoneApi
import kotlin.reflect.KClass

/**
 * This annotation can be used in combination with [ComposeScreen] to
 * enable the integration of a `FragmentNavEventNavigator` into the generated code. The navigator
 * will automatically be set up so that it's ready to handle events.
 *
 * The `NavEventNavigator` subclass is expected to be bound to `NavEventNavigator` and available
 * in the [ComposeScreen.scope] scoped generated component. This can be achieved by adding
 * `@ContributesBinding(TheScope::class, NavEventNavigator::class)` to it.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class NavDestination(
    val route: KClass<out NavRoute>,
    val type: DestinationType = DestinationType.NONE,
    val destinationScope: KClass<*>,
)

/**
 * Describing the type of [NavDestination].
 */
public enum class DestinationType {
    @ObsoleteWhetstoneApi
    NONE,
    SCREEN,
    DIALOG,
    BOTTOM_SHEET,
}

/**
 * Like [NavDestination] but for a screen represented by a [NavRoot].
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
public annotation class RootNavDestination(
    val root: KClass<out NavRoot>,
    val destinationScope: KClass<*>,
)

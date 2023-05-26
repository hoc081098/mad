package com.freeletics.mad.sample.feature.root

import com.freeletics.mad.navigator.NavEventNavigator
import com.freeletics.mad.sample.feature.bottomsheet.nav.BottomSheetRoute
import com.freeletics.mad.sample.feature.dialog.nav.DialogRoute
import com.freeletics.mad.sample.feature.root.nav.RootRoute
import com.freeletics.mad.sample.feature.screen.nav.ScreenRoute
import com.freeletics.mad.whetstone.ScopeTo
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ScopeTo(RootRoute::class)
@ContributesBinding(RootRoute::class, NavEventNavigator::class)
class RootNavigator @Inject constructor() : NavEventNavigator() {
    fun navigateToScreen() {
        navigateTo(ScreenRoute(1))
    }

    fun navigateToDialog() {
        navigateTo(DialogRoute)
    }

    fun navigateToBottomSheet() {
        navigateTo(BottomSheetRoute)
    }
}
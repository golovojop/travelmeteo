package k.s.yarlykov.travelmeteo.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import k.s.yarlykov.travelmeteo.R
import k.s.yarlykov.travelmeteo.ui.MapActivity.Companion.logIt

class CustomBottomSheetBevavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<LinearLayout>(context, attrs) {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: LinearLayout, dependency: View): Boolean {
        logIt("layoutDependsOn: ${dependency.javaClass.simpleName}")
        return dependency is androidx.recyclerview.widget.RecyclerView || dependency.id == R.id.llForecastContent
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        logIt("onStartNestedScroll: axes == ViewCompat.SCROLL_AXIS_HORIZONTAL is ${axes == ViewCompat.SCROLL_AXIS_HORIZONTAL}")
        return axes == ViewCompat.SCROLL_AXIS_HORIZONTAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: LinearLayout,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        logIt("onNestedScroll:")
    }
}
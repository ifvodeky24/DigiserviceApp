package com.example.core_navigation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface ModuleNavigator {

    fun <T> T.navigateToAuthActivity(
        finnishCurrent: Boolean = false
    ) where T : Fragment, T : ModuleNavigator {
        startActivity(ActivityClassPath.Auth, finnishCurrent)
    }

    fun <T> T.navigateToAuthActivity(
        finnishCurrent: Boolean = false
    ) where T : AppCompatActivity, T : ModuleNavigator {
        startActivity(ActivityClassPath.Auth, finnishCurrent)
    }

    fun <T> T.navigateToHomeActivity(
        finnishCurrent: Boolean = false
    ) where T : Fragment, T : ModuleNavigator {
        startActivity(ActivityClassPath.Home, finnishCurrent)
    }

    fun <T> T.navigateToHomeActivity(
        finnishCurrent: Boolean = false
    ) where T : AppCompatActivity, T : ModuleNavigator {
        startActivity(ActivityClassPath.Home, finnishCurrent)
    }
}

private fun AppCompatActivity.startActivity(intent: Intent, finnishCurrent: Boolean) {
    startActivity(intent)
    if (finnishCurrent) finish()
}

private fun AppCompatActivity.startActivity(
    activityClassPath: ActivityClassPath,
    finnishCurrent: Boolean
) = startActivity(activityClassPath.getIntent(this), finnishCurrent)

private fun Fragment.startActivity(intent: Intent, finnishCurrent: Boolean) {
    startActivity(intent)
    if (finnishCurrent) activity?.finish()
}

private fun Fragment.startActivity(activityClassPath: ActivityClassPath, finnishCurrent: Boolean) =
    startActivity(activityClassPath.getIntent(requireContext()), finnishCurrent)

interface Auth :ModuleNavigator {

}

data class MapParameter(
    val lat: Double,
    val lng: Double,
)
data class ResultMapParameter(
    val lat: Double,
    val lng: Double,
    val address: String,
)

private class DataMapsResult : ActivityResultContract<MapParameter, ResultMapParameter>(){
    override fun createIntent(context: Context, input: MapParameter?): Intent {
//        ActivityClassPath.Map.getIntent(context).apply {
//            putExtra()
//        }
        TODO("Not yet implemented")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ResultMapParameter {
        TODO("Not yet implemented")
    }

}
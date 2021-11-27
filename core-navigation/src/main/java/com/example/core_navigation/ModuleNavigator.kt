package com.example.core_navigation

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.MainThread
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

    interface ProductNav : ModuleNavigator {

        companion object {
            const val JUAL_ID = "jualId"
        }

        @MainThread
        fun <T> T.jualIdParam(): Lazy<String> where T : AppCompatActivity, T : ProductNav =
            lazy(LazyThreadSafetyMode.NONE) {
                intent.getStringExtra(JUAL_ID).orEmpty()
            }
    }

    fun <T> T.navigateToProductActivity(
        jualId: String,
        finnishCurrent: Boolean = false
    ) where T : Fragment, T : ModuleNavigator {
        ActivityClassPath.Product.getIntent(requireContext())
            .apply {
                putExtra(ProductNav.JUAL_ID, jualId)
            }.let { startActivity(it, finnishCurrent) }
    }

    fun <T> T.navigateToProductActivity(
        jualId: String,
        finnishCurrent: Boolean = false
    ) where T : AppCompatActivity, T : ModuleNavigator {
        ActivityClassPath.Product.getIntent(this)
            .apply {
                putExtra(ProductNav.JUAL_ID, jualId)
            }.let { startActivity(it, finnishCurrent) }
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

//fun Fragment.navigateToGallery(flag: String){
//    val intent = Intent(Intent.ACTION_PICK).apply {
//        type = "image/*"
//    }
//    resultPhoto.launch(intent)
//}

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
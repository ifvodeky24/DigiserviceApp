@file:Suppress("TooManyFunctions")

package com.example.core_resource

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.core_data.api.ApiException

//region Fragment

fun Fragment.showApiFailedDialog(
    exception: ApiException = ApiException.Unknowns,
    onDismissListener: () -> Unit = {}
) {
    when (exception) {
        ApiException.Offline -> showErrorDialogOffline(onDismissListener)
        ApiException.Timeout,
        ApiException.Network -> showErrorDialogNetwork(onDismissListener)
        is ApiException.FailedResponse<*>,
        is ApiException.Http,
        ApiException.NullResponse,
        ApiException.EmptyResponse -> showErrorDialogServer(onDismissListener)
        ApiException.Unknowns -> showErrorDialogUnknown(onDismissListener)
    }
}

private fun Fragment.showErrorDialogUnknown(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.raw.dialog_api_failed_unknown,
        isAnimation = true,
        titleRes = R.string.dialog_api_failed_unknown_title,
        contentRes = R.string.dialog_api_failed_unknown_content,
        onDismissListener = onDismissListener
    )
}

private fun Fragment.showErrorDialogOffline(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.drawable.dialog_api_failed_network,
        isAnimation = false,
        titleRes = R.string.dialog_api_failed_offline_title,
        contentRes = R.string.dialog_api_failed_offline_content,
        onDismissListener = onDismissListener
    )
}

private fun Fragment.showErrorDialogNetwork(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.drawable.dialog_api_failed_network,
        isAnimation = false,
        titleRes = R.string.dialog_api_failed_network_title,
        contentRes = R.string.dialog_api_failed_network_content,
        onDismissListener = onDismissListener
    )
}

private fun Fragment.showErrorDialogServer(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.raw.dialog_api_failed_server,
        isAnimation = true,
        titleRes = R.string.dialog_api_failed_server_title,
        contentRes = R.string.dialog_api_failed_server_content,
        onDismissListener = onDismissListener
    )
}

//endregion
//region Activity

fun AppCompatActivity.showApiFailedDialog(
    exception: ApiException = ApiException.Unknowns,
    onDismissListener: () -> Unit = {}
) {
    when (exception) {
        ApiException.Offline -> showErrorDialogOffline(onDismissListener)
        ApiException.Timeout,
        ApiException.Network -> showErrorDialogNetwork(onDismissListener)
        is ApiException.FailedResponse<*>,
        is ApiException.Http,
        ApiException.NullResponse,
        ApiException.EmptyResponse -> showErrorDialogServer(onDismissListener)
        ApiException.Unknowns -> showErrorDialogUnknown(onDismissListener)
    }
}

private fun AppCompatActivity.showErrorDialogUnknown(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.raw.dialog_api_failed_unknown,
        isAnimation = true,
        titleRes = R.string.dialog_api_failed_unknown_title,
        contentRes = R.string.dialog_api_failed_unknown_content,
        onDismissListener = onDismissListener
    )
}

private fun AppCompatActivity.showErrorDialogOffline(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.drawable.dialog_api_failed_network,
        isAnimation = false,
        titleRes = R.string.dialog_api_failed_offline_title,
        contentRes = R.string.dialog_api_failed_offline_content,
        onDismissListener = onDismissListener
    )
}

private fun AppCompatActivity.showErrorDialogNetwork(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.drawable.dialog_api_failed_network,
        isAnimation = false,
        titleRes = R.string.dialog_api_failed_network_title,
        contentRes = R.string.dialog_api_failed_network_content,
        onDismissListener = onDismissListener
    )
}

private fun AppCompatActivity.showErrorDialogServer(onDismissListener: () -> Unit = {}) {
    showErrorDialog(
        assetRes = R.raw.dialog_api_failed_server,
        isAnimation = true,
        titleRes = R.string.dialog_api_failed_server_title,
        contentRes = R.string.dialog_api_failed_server_content,
        onDismissListener = onDismissListener
    )
}

//endregion

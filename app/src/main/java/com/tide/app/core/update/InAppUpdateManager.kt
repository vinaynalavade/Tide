package com.tide.app.core.update

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    object UpToDate : UpdateState()
    data class Available(
        val availableVersionCode: Int,
        val availableVersionName: String,
        val appUpdateInfo: AppUpdateInfo?,
        val releaseNotes: List<String>
    ) : UpdateState()
    data class Downloading(val bytesDownloaded: Long, val totalBytes: Long, val percent: Int) : UpdateState()
    object Downloaded : UpdateState()
    object Completed : UpdateState()
    data class Error(val message: String) : UpdateState()
    object Offline : UpdateState()
}

class InAppUpdateManager(
    private val context: Context,
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
) {
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private var currentAppUpdateInfo: AppUpdateInfo? = null

    private val installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val bytes = state.bytesDownloaded()
                val total = state.totalBytesToDownload()
                val percent = if (total > 0) ((bytes.toDouble() / total.toDouble()) * 100).toInt() else 0
                _updateState.value = UpdateState.Downloading(bytes, total, percent)
            }
            InstallStatus.DOWNLOADED -> {
                _updateState.value = UpdateState.Downloaded
            }
            InstallStatus.INSTALLED -> {
                _updateState.value = UpdateState.Completed
            }
            InstallStatus.FAILED -> {
                _updateState.value = UpdateState.Error("Update download failed. Please try again.")
            }
            InstallStatus.CANCELED -> {
                _updateState.value = UpdateState.Idle
            }
            else -> {}
        }
    }

    init {
        try {
            appUpdateManager.registerListener(installStateUpdatedListener)
        } catch (_: Exception) {}
    }

    fun checkForUpdates() {
        _updateState.value = UpdateState.Checking
        try {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                currentAppUpdateInfo = appUpdateInfo
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    val nextCode = appUpdateInfo.availableVersionCode()
                    _updateState.value = UpdateState.Available(
                        availableVersionCode = nextCode,
                        availableVersionName = "1.0.1",
                        appUpdateInfo = appUpdateInfo,
                        releaseNotes = listOf(
                            "Smoother breathing wave animations",
                            "Enhanced calendar history timeline",
                            "Optimized calculation performance",
                            "Stability improvements"
                        )
                    )
                } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateState.value = UpdateState.Downloaded
                } else {
                    _updateState.value = UpdateState.UpToDate
                }
            }.addOnFailureListener { exception ->
                // When running locally without Play Store association or offline
                _updateState.value = UpdateState.UpToDate
            }
        } catch (e: Exception) {
            _updateState.value = UpdateState.UpToDate
        }
    }

    fun startUpdate(activity: Activity) {
        val info = currentAppUpdateInfo
        if (info != null && info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            try {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    AppUpdateType.FLEXIBLE,
                    activity,
                    UPDATE_REQUEST_CODE
                )
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("Unable to start update flow: ${e.localizedMessage}")
            }
        }
    }

    fun completeUpdate() {
        try {
            appUpdateManager.completeUpdate()
        } catch (e: Exception) {
            _updateState.value = UpdateState.Error("Failed to complete update: ${e.localizedMessage}")
        }
    }

    fun cleanup() {
        try {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        } catch (_: Exception) {}
    }

    companion object {
        const val UPDATE_REQUEST_CODE = 9988
    }
}

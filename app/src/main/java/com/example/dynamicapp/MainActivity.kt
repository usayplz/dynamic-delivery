package com.example.dynamicapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*

/*
    REQUIRES_USER_CONFIRMATION	The download requires user confirmation. This is most likely due to the size of the download being larger than 150 MB.
    States: state.bytesDownloaded(), state.totalBytesToDownload(), state.errorCode()
    Download option: manager.deferredInstall(arrayListOf(DYNAMIC_MODULE_NAME))
*/

class MainActivity : AppCompatActivity() {
    private val manager: SplitInstallManager by lazy { SplitInstallManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindUseModuleButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                showToast("Install module cancelled") // action if canceled
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindUseModuleButton() {
        useModule.setOnClickListener {
            useDynamicModule()
        }
    }

    private fun useDynamicModule() {
        if (manager.installedModules.contains(DYNAMIC_MODULE_NAME)) {
            openModule()
        } else {
            downloadModule()
        }
    }

    private fun openModule() {
        val intent = Intent()
        intent.setClassName(BuildConfig.APPLICATION_ID, "com.example.ondemand.OnDemandActivity")
        startActivity(intent)
    }

    private fun downloadModule() {
        val request = SplitInstallRequest.newBuilder()
            .addModule(DYNAMIC_MODULE_NAME)
            .build()
        manager.registerListener(::getSessionStateListener)
        manager.startInstall(request)
    }

    private fun getSessionStateListener(state: SplitInstallSessionState) {
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> showToast("Downloading feature")
            SplitInstallSessionStatus.FAILED -> showToast("Install failed (${state.errorCode()})")
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION ->
                manager.startConfirmationDialogForResult(state, this, CONFIRMATION_REQUEST_CODE)
            SplitInstallSessionStatus.INSTALLED -> showToast("OnDemand module is ready to be used")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DYNAMIC_MODULE_NAME = "ondemand"
        private const val CONFIRMATION_REQUEST_CODE = 11
    }
}

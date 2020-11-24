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

class MainActivity : AppCompatActivity() {
    private val manager: SplitInstallManager by lazy { SplitInstallManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindDownloadButton()
        bindOpenButton()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONFIRMATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                showToast("Install module cancelled")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun bindDownloadButton() {
        download.setOnClickListener {
            downloadModule()
        }
    }

    private fun downloadModule() {
        val request = SplitInstallRequest.newBuilder()
            .addModule(DYNAMIC_MODULE_NAME)
            .build()

        manager.registerListener(::processSessionState)
        manager.startInstall(request)
    }

    private fun processSessionState(state: SplitInstallSessionState) {
        when (state.status()) {
            SplitInstallSessionStatus.DOWNLOADING -> showToast("Downloading feature")
            SplitInstallSessionStatus.FAILED -> showToast("Install failed (${state.errorCode()})")
            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION ->
                manager.startConfirmationDialogForResult(state, this, CONFIRMATION_REQUEST_CODE)
            SplitInstallSessionStatus.INSTALLED -> {
                showToast("Feature ready to be used")
                updateDynamicFeatureButtonState()
            }
        }
    }

    private fun bindOpenButton() {
        updateDynamicFeatureButtonState()
        open.setOnClickListener {
            val intent = Intent()
            intent.setClassName(BuildConfig.APPLICATION_ID, "com.example.ondemand.OnDemandActivity")
            startActivity(intent)
        }
    }

    private fun updateDynamicFeatureButtonState() {
        open.isEnabled = manager.installedModules.contains(DYNAMIC_MODULE_NAME)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val DYNAMIC_MODULE_NAME = "ondemand"
        private const val CONFIRMATION_REQUEST_CODE = 11
    }
}

package com.example.dynamicapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.dynamicfeatures.DynamicExtras
import androidx.navigation.dynamicfeatures.DynamicInstallMonitor
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUseModuleButton()
    }

    private fun bindUseModuleButton() {
        useModule.setOnClickListener {
            openDynamicModule()
        }
    }

    private fun openDynamicModule() {
        val installMonitor = DynamicInstallMonitor()
        findNavController().navigate(
            R.id.action_mainFragment_to_onDemandFragment,
            null,
            null,
            DynamicExtras(installMonitor)
        )

        if (installMonitor.isInstallRequired) {
            observeInstallDynamicModule(installMonitor)
        }
    }

    private fun observeInstallDynamicModule(installMonitor: DynamicInstallMonitor) {
        installMonitor.status.observe(
            viewLifecycleOwner,
            object : Observer<SplitInstallSessionState> {
                override fun onChanged(state: SplitInstallSessionState) {
                    getSessionStateListener(state)
                    if (state.hasTerminalStatus()) {
                        installMonitor.status.removeObserver(this)
                    }
                }
            })
    }

    private fun getSessionStateListener(state: SplitInstallSessionState) {
        when (state.status()) {
            DOWNLOADING -> showToast("Downloading feature")
            FAILED -> showToast("Install failed (${state.errorCode()})")
            INSTALLED -> showToast("OnDemand module is ready to be used")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
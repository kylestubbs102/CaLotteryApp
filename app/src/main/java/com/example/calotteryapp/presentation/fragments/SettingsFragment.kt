package com.example.calotteryapp.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.example.calotteryapp.R
import com.example.calotteryapp.databinding.FragmentSettingsBinding
import com.example.calotteryapp.presentation.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val viewModel: SettingsViewModel by viewModels()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        toast = Toast(context)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObserver()
        viewModel.fetchUserSettings()
    }

    private fun setupObserver() {
        viewModel.resultSuccess.observe(viewLifecycleOwner, Observer {
            if (viewLifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) {
                return@Observer
            }

            val message = when (it) {
                true -> getString(R.string.toast_save_success)
                false -> getString(R.string.toast_save_failure)
            }

            showToast(message)
        })
    }

    private fun showToast(message: String) {
        if (toast != null) {
            toast?.cancel()
        }
        toast = Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        )
        toast?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        toast = null
    }

}
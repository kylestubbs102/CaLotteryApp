package com.example.calotteryapp.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        clearEditTexts()
        setupTextWatcher()
        setupObserver()
    }

    private fun clearEditTexts() {
        binding.edittext1.text.clear()
        binding.edittext2.text.clear()
        binding.edittext3.text.clear()
        binding.edittext4.text.clear()
        binding.edittext5.text.clear()
        binding.edittext6.text.clear()
    }

    private fun setupTextWatcher() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length != 2) return

                if (view?.findFocus()?.id == R.id.edittext6) {
                    val imm =
                        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view?.windowToken, 0)

                    view?.findFocus()?.focusSearch(View.FOCUS_DOWN)
                } else {
                    view?.findFocus()?.focusSearch(View.FOCUS_RIGHT)?.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                viewModel.userNumbers.value?.let { userNums ->
                    binding.buttonSubmit.isEnabled = userNums.all { it.isNotEmpty() }
                }
            }

        }

        binding.edittext1.addTextChangedListener(textWatcher)
        binding.edittext2.addTextChangedListener(textWatcher)
        binding.edittext3.addTextChangedListener(textWatcher)
        binding.edittext4.addTextChangedListener(textWatcher)
        binding.edittext5.addTextChangedListener(textWatcher)
        binding.edittext6.addTextChangedListener(textWatcher)
    }

    private fun setupObserver() {
        viewModel.resultSuccess.observe(viewLifecycleOwner, Observer {
            if (viewLifecycleOwner.lifecycle.currentState != Lifecycle.State.RESUMED) return@Observer

            val text = when (it) {
                true -> getString(R.string.toast_save_success)
                false -> getString(R.string.toast_save_failure)
            }
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_LONG
            ).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
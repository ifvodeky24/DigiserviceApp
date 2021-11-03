package com.example.feature_auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_util.bindLifecycle
import com.example.core_util.dismissKeyboard
import com.example.core_util.hideProgress
import com.example.core_util.showProgress
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val textBtnNext by lazy {
        "LANJUTKAN DAFTAR"
    }
    private val textHintEmptyEmail by lazy {
        "Email harus diisi"
    }
    private val textHintEmptyName by lazy {
        "Nama harus diisi"
    }
    private val textHintEmptyPassword by lazy {
        "Password harus diisi"
    }
    private val textHintEmptyStoreName by lazy {
        "Nama toko harus diisi"
    }
    private val textHintEmptyStoreAddress by lazy {
        "Alamat toko harus diisi"
    }
    private val textHintEmptyStoreDescription by lazy {
        "Deskripsi harus diisi"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInput()
        observeLogin()
    }

    private fun observeLogin() {
        authViewModel.registerServiceResponse.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    hideProgress(true)
                    findNavController().navigate(R.id.chooseFragment)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    hideProgress(true)
                }
            }
        })
    }

    private fun setupInput() {
        with(binding){
            form {
                useRealTimeValidation(disableSubmit = true)
                inputLayout(R.id.edt_layout_email){
                    isNotEmpty().description(textHintEmptyEmail)
                }
                inputLayout(R.id.edt_layout_name){
                    isNotEmpty().description(textHintEmptyName)
                }
                inputLayout(R.id.edt_layout_store_name){
                    isNotEmpty().description(textHintEmptyStoreName)
                }
                inputLayout(R.id.edt_layout_store_address){
                    isNotEmpty().description(textHintEmptyStoreAddress)
                }
//                inputLayout(R.id.edt_layout_store_description){
//                    isNotEmpty().description(textHintEmptyStoreDescription)
//                }
                submitWith(R.id.btn_next) { registerService() }
            }
        }

        binding.btnNext.bindLifecycle(viewLifecycleOwner)
    }

    private fun registerService() {
        dismissKeyboard()
        with(binding) {
            authViewModel.registerService(
                edtInputEmail.text.toString(),
                edtInputName.text.toString(),
                "123456",
                edtInputStoreName.text.toString(),
                edtInputStoreAddress.text.toString(),
                0f,
                0f,
                edtInputStoreDescription.text.toString(),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showProgress() = with(binding) {
        listOf(
            btnNext, edtLayoutEmail, edtLayoutName,
            edtLayoutStoreAddress, edtLayoutStoreDescription,
        ).forEach { it.isEnabled = false }

        btnNext.showProgress()
    }

    private fun hideProgress(isEnable: Boolean) = with(binding) {
        btnNext.postDelayed(
            {
                listOf(
                    btnNext, edtLayoutEmail, edtLayoutName,
                    edtLayoutStoreAddress, edtLayoutStoreDescription,
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnNext.hideProgress(textBtnNext) {
            isEnable && with(binding) {
                "${edtInputName.text}".isNotBlank() && "${edtInputEmail.text}".isNotBlank()
            }
        }
    }

}
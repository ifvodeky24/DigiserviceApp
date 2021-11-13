package com.example.feature_auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.ApiException
import com.example.core_data.api.response.ErrorResponse
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.bindLifecycle
import com.example.core_util.dismissKeyboard
import com.example.core_util.hideProgress
import com.example.core_util.showProgress
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class LoginFragment : Fragment(), ModuleNavigator {

    private val textBtnLogin by lazy {
        getString(R.string.login)
    }

    private val textHintEmptyEmail by lazy {
        "Email harus diisi"
    }

    private val textHintEmptyPassword by lazy {
        "Password harus diisi"
    }

    private val viewModel by sharedViewModel<AuthViewModel>()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        setupInput()
        with(binding) {
            tvDaftar.setOnClickListener {
                findNavController().navigate(R.id.registerFragment)
            }
        }

        viewModel.loginRequest.observe(viewLifecycleOwner, { login ->
            when (login) {
                is ApiEvent.OnProgress -> {
                    showProgress()
                    Timber.d("progress ${login.currentResult}")
                }
                is ApiEvent.OnSuccess -> {
                    hideProgress(true)
                    Timber.d("sukses ${login.getData()}")
                    navigateToHomeActivity(finnishCurrent = true)
                }
                is ApiEvent.OnFailed -> {
                    hideProgress(true)
                    Timber.d("gagal ${login.getException()}")
                    Toast.makeText(requireActivity(), "${login.getException()}", Toast.LENGTH_SHORT)
                        .show()

                    when (val exception = login.getException()) {
//                        is ApiException.FailedResponse<*> -> when (val errorResponse =
//                            exception.error) {
//                            is ErrorResponse -> when {
//                                errorResponse contains ApiException.SERVER_ERROR -> {
//                                    showErrorFail(getString(R.string.oops_server_error))
//                                }
//                                else -> showErrorMessage()
//                            }
//                            else -> showErrorMessage()
//                        }
//                        is ApiException.Timeout -> showErrorFail(getString(R.string.request_timeout))
//                        is ApiException.Network -> showErrorFail(getString(R.string.oops_network))
//                        is ApiException.Offline -> showErrorFail(getString(R.string.oops_network))
//                        is ApiException.Unknown -> showErrorFail(getString(R.string.unknown_error))
//                        else -> showErrorMessage()
                    }
                }
            }
        })
    }



    private fun setupInput() {
        with(binding) {
            form {
                useRealTimeValidation(disableSubmit = true)
                inputLayout(R.id.til_email) {
                    isNotEmpty().description(textHintEmptyEmail)
                }
                inputLayout(R.id.til_password) {
                    isNotEmpty().description(textHintEmptyPassword)
                }
                submitWith(R.id.btn_login) {
                    dismissKeyboard()
                    val email = binding.edtEmail.text.toString()
                    val password = binding.edtPassword.text.toString()
//                    val email = "ryan@gmail.com"
//                    val password = "admins"

                    viewModel.email = email
                    viewModel.password = password

                    Timber.d("cekkkk $email dan ${password} dan ")

                    viewModel.login(email, password)
                }
            }
        }

        binding.btnLogin.bindLifecycle(viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showProgress() = with(binding) {
        listOf(
            btnLogin, tilEmail, tilPassword,
            edtEmail, edtPassword,
        ).forEach { it.isEnabled = false }

        btnLogin.showProgress()
    }

    private fun hideProgress(isEnable: Boolean) = with(binding) {
        btnLogin.postDelayed(
            {
                listOf(
                    btnLogin, tilEmail, tilPassword,
                    edtEmail, edtPassword,
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnLogin.hideProgress(textBtnLogin) {
            isEnable && with(binding) {
                "${edtEmail.text}".isNotBlank() && "${edtPassword.text}".isNotBlank()
            }
        }
    }

}
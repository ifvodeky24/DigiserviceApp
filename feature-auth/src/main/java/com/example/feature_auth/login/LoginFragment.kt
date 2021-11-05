package com.example.feature_auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.core_data.api.ApiEvent
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginFragment : Fragment() {
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

        with(binding) {
            btnLogin.setOnClickListener {
//                val email = binding.etEmail.text.toString()
//                val password = binding.etPassword.text.toString()
                val email = "ryan@gmail.com"
                val password = "admin"
                val level = "Teknisi"

//                viewModel.email = email
//                viewModel.password = password
//                viewModel.level = level

                Timber.d("cekkkk $email dan ${password} dan ${level}")

                viewModel.login(email, password, level)

                viewModel.loginRequest.observe(viewLifecycleOwner, { login ->
                    when(login){
                        is ApiEvent.OnProgress -> {
                            Timber.d("progress ${login.currentResult}")
                        }
                        is ApiEvent.OnSuccess ->  {
                            Timber.d("sukses ${login.getData()}")
                        }
                        is ApiEvent.OnFailed -> {
                            Timber.d("gagal ${login.getException()}")
                        }
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
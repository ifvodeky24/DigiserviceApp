package com.example.feature_auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.*
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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

    private lateinit var preferenceManager: PreferenceManager

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

        preferenceManager = PreferenceManager(requireActivity())

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        setupInput()
        with(binding) {
            tvTeknisi.setOnClickListener {
                findNavController().navigate(R.id.registerFragment)
            }
            tvPelanggan.setOnClickListener {
                findNavController().navigate(R.id.registerPelangganFragment)
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

                    val database = FirebaseFirestore.getInstance()
                    database.collection(Constants.KEY_COLLECTION_USERS)
                        .whereEqualTo("id", login.getData()!!.id)
                        .whereEqualTo(
                            "email",
                            login.getData()?.email
                        )
                        .get()
                        .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                            if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                                val documentSnapshot = task.result!!.documents[0]
                                preferenceManager.putString(
                                    Constants.KEY_SENDER_ID,
                                    documentSnapshot.id
                                )
                                preferenceManager.putString(
                                    Constants.ID,
                                    login.getData()!!.id.toString()
                                )
                                Timber.d("ada loh datanya ${documentSnapshot.id}")

                                Timber.d("sukses ${login.getData()}")
                                navigateToHomeActivity(finnishCurrent = true)
                            } else {
                                val database = FirebaseFirestore.getInstance()
                                val user = login.getData()!!
                                database.collection(Constants.KEY_COLLECTION_USERS)
                                    .add(user)
                                    .addOnSuccessListener { documentReference: DocumentReference ->
                                        Timber.d("belum ada loh datanya ${documentReference.id}")

                                        Timber.d("sukses ${login.getData()}")
                                        navigateToHomeActivity(finnishCurrent = true)
                                    }
                                    .addOnFailureListener { exception: Exception ->
                                        Timber.d("gagal dan belum ada loh datanya $exception")
                                    }
                            }
                        }
                }
                is ApiEvent.OnFailed -> {
                    hideProgress(true)
                    Timber.d(login.getException().toString())
                    Snackbar.make(
                        requireContext(), requireView(),
                        login.getException().toString(), Snackbar.LENGTH_SHORT
                    )
                        .show()

                }
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            }
        )
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
                    val email = edtEmail.text.toString()
                    val password = edtPassword.text.toString()

                    viewModel.email = email
                    viewModel.password = password

                    viewModel.login(email, password)
                }
            }
            btnLogin.bindLifecycle(viewLifecycleOwner)
        }

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
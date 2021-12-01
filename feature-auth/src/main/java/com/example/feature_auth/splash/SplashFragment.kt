package com.example.feature_auth.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.core_data.api.ApiEvent
import com.example.core_navigation.ModuleNavigator
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentSplashBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel by sharedViewModel<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            observeAuth()
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeAuth() {
        viewModel.auth.observe(viewLifecycleOwner, { data ->
            if (data != null) {
                if (data.isLogin){
                    data.let {
                        if (it.level == "teknisi") {
                            observeCurrentSkill(it.teknisiId)
                        } else {
                            navigateToHomeActivity(true)
                        }
                    }
                }
            } else {
                findNavController().navigate(R.id.loginFragment)
            }
        })
    }

    private fun observeCurrentSkill(teknisiId: Int) {
        viewModel.setCurrentSkill(teknisiId)

        viewModel.liveSkills.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    val result = event.getData()
                    if (result != null) {
                        if (result.jenisHp.isNullOrEmpty() && result.skils.isNullOrEmpty()) {
                            findNavController().navigate(R.id.chooseFragment)
                        } else {
                            navigateToHomeActivity(finnishCurrent = true)
                        }
                    }
                }
                is ApiEvent.OnFailed -> {}
            }
        }
    }
}
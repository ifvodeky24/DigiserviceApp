package com.example.feature_home.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.core_data.domain.auth.isTechnician
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentEditAccountBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAccountFragment : Fragment() {

    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDisplay()
        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            auth?.let {
                   if (auth.isTechnician) replaceFragmentEditAccountTechnician(binding.fragmentLayout.id)
                   else replaceFragmentEditAccountCustomer(binding.fragmentLayout.id)
                }
            })
        }

    private fun setupDisplay() {
        with(binding.toolbar.toolbar){
            title = getString(R.string.edit_account)
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }

    }
}
package com.example.feature_home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.example.core_data.domain.auth.isTechnician
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentStoreBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            auth?.let {
                if (auth.foto.isNotEmpty()) binding.imageProfilePicture.load(auth.foto) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
                binding.labelStoreName.text = if (auth.isTechnician) auth.namaToko else auth.name
                binding.labelPhone.text = auth.hp
            }
        })

        binding.seeProduct.setOnClickListener {
            findNavController().navigate(R.id.productFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
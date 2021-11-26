package com.example.feature_home.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.core_data.api.ApiEvent
import com.example.core_data.clearAppData
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.core_data.domain.auth.isTechnician
import com.example.core_data.removeAll
import com.example.core_navigation.ModuleNavigator
import com.example.feature_home.R
import com.example.feature_home.account.LogoutDialogFragment.Companion.TRUE
import com.example.feature_home.databinding.FragmentAccountBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.net.CookieHandler

class AccountFragment : Fragment(), ModuleNavigator{

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    private val cookieHandler: CookieHandler by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.editAccountFragment)
        }

        binding.buttonSignOut.setOnClickListener {
            val bs = LogoutDialogFragment()
            bs.show(childFragmentManager, "LogoutBottomSheet")
        }

        childFragmentManager.setFragmentResultListener(
            LogoutDialogFragment.KEY_RESULT_SUBMIT,
            this@AccountFragment
        ) { _, bundle ->
            val result = bundle.getString(LogoutDialogFragment.KEY_BUNDLE_SUBMIT)
            if (result == TRUE) {
                if (result == "true") {
                    viewLifecycleOwner.lifecycleScope.launch {
                        cookieHandler.removeAll()
                        getKoin().clearAppData()
                        navigateToAuthActivity(finnishCurrent = true)
                    }
                }
            }
        }

        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            auth?.let {
                with(binding){
                    if (auth.foto.isNotEmpty()) imageProfilePicture.load(auth.foto) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                    labelStoreName.text = if (auth.isTechnician) auth.namaToko else auth.name
                    labelPhone.text = auth.hp
                    labelEmail.text = auth.email
                    labelName.text = auth.name
                    labelPhone.text = auth.hp
                    labelDescription.isVisible = if (auth.isTechnician) true else false
                    rvJenisHp.isVisible = if (auth.isTechnician) true else false
                    tvHpType.isVisible = if (auth.isTechnician) true else false
                    tvSkillsType.isVisible = if (auth.isTechnician) true else false
                    labelDescription.text = auth.deskripsi
                    labelAddress.text = auth.alamat

                    if (auth.isTechnician) accountViewModel.setCurrentSkill(auth.teknisiId)
                }
            }
        })

        accountViewModel.liveSkils.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    setupRecyclerSkils(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) {
                    //hideProgress(true)
                }
            }
        })

    }

    private fun setupRecyclerSkils(listSkils: ResultSkils) {
        binding.rvSkills.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.skils))
            withItem<Skils, SkillsItemViewHolder>(R.layout.item_skils){
                onBind(::SkillsItemViewHolder){ _, item ->
                    titleSkils.text = "- ${item.namaKerusakan}"
                }
            }
        }
        binding.rvJenisHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.jenisHp))
            withItem<JenisHp, SkillsItemViewHolder>(R.layout.item_skils){
                onBind(::SkillsItemViewHolder){ _, item ->
                    titleSkils.text = "- ${item.jenisNama}"
                }
            }
        }
    }
}
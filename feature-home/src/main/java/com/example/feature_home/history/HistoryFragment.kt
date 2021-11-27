package com.example.feature_home.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentHistoryBinding
import com.example.feature_home.history.pelanggan.PelangganHistorySectionsPagerAdapter
import com.example.feature_home.history.teknisi.TeknisiHistorySectionsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding get() = _binding!!

    private lateinit var sectionsPagerAdapter: FragmentStateAdapter

    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountViewModel.authUser.observe(viewLifecycleOwner) { auth ->
            sectionsPagerAdapter = when(auth?.level) {
                "teknisi" -> TeknisiHistorySectionsPagerAdapter(childFragmentManager, lifecycle)
                else -> PelangganHistorySectionsPagerAdapter(childFragmentManager, lifecycle)
            }

            setupTablayout()

        }
    }

    private fun setupTablayout() {
        binding.viewPager.adapter = sectionsPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(TITLES[position])
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @StringRes
        private val TITLES = intArrayOf(
            R.string.history_service, R.string.history_produk
        )
    }
}
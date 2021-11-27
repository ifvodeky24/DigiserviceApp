package com.example.feature_home.history.teknisi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.feature_home.history.pelanggan.HistoryPelangganFragment

class TeknisiHistorySectionsPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int  = 2

    override fun createFragment(position: Int): Fragment {
//        return when(position) {
//            0 ->
//        }
        return HistoryTeknisiFragment()
    }

}
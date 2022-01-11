package com.example.feature_home.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.core_data.domain.RiwayatService
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentDetailPesananServiceBinding

class DetailPesananServiceFragment : Fragment() {

    private var _binding: FragmentDetailPesananServiceBinding? = null
    private val binding: FragmentDetailPesananServiceBinding
        get() = _binding as FragmentDetailPesananServiceBinding

//    private val args: DetailPesananServiceFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailPesananServiceBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val riwayatService = args.riwayatService

//        setupRiwayatSerice(riwayatService)
    }

    private fun setupRiwayatSerice(riwayatService: RiwayatService) {
        binding.apply {
            tvDetailPesananNama.text = riwayatService.nama
            tvDetailPesananMerek.text = riwayatService.merek
            tvDetailPesananKerusakan.text = riwayatService.kerusakan
            tvDetailPesananDeskripsi.text = riwayatService.deskripsi
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
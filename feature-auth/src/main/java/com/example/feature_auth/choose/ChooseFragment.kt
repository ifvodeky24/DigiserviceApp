package com.example.feature_auth.choose

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.JenisKerusakan
import com.example.core_data.domain.ListJenisKerusakan
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentChooseBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseFragment : Fragment() {

    private var _binding: FragmentChooseBinding? = null
    private val binding get() = _binding!!

    private val chooseViewModel: ChooseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        observeWhenDataSaved()
        setInput()
    }

    private fun setInput() {
        binding.edtInputDescription.addTextChangedListener { text ->
            chooseViewModel.putEditDataValue(
                inputType = TypeInput.ITEM_INPUT_TYPE_DESKRIPSI,
                indexId = -1,
                value = text.toString()
            )
        }
        binding.btnSubmit.setOnClickListener {
            chooseViewModel.saveForm(chooseViewModel.getFinalInput())
        }
    }

    private fun observeData() {
        chooseViewModel.jenisHp()
        chooseViewModel.liveJenisHp.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                // is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    chooseViewModel jenisHp it
                    setupRecycler(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
        chooseViewModel.jenisKerusakan()
        chooseViewModel.liveJenisKerusakan.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                //is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    chooseViewModel jenisKerusakan it
                    setupRecyclerJenisKeruskan(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
        chooseViewModel.isFormCompleted.observe(viewLifecycleOwner, {
            disableEnableButton(it)
        })
    }

    private fun disableEnableButton(isEnable: Boolean) {
        if (isEnable)
        {
            binding.btnSubmit.isEnabled = isEnable
            binding.btnSubmit.backgroundTintList =ContextCompat.getColorStateList(
                requireContext(),
                R.color.colorCobaltBlue
            )
        }
        else {
            binding.btnSubmit.isEnabled = isEnable
            binding.btnSubmit.backgroundTintList = ContextCompat.getColorStateList(
                requireContext(),
                R.color.colorLightBlueGrey
            )
        }
    }

    private fun setupRecyclerJenisKeruskan(listJenisKerusakan: ListJenisKerusakan) {
        binding.rvPhoneType.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listJenisKerusakan))
            withItem<JenisKerusakan, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.text = item.namaKerusakan
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        chooseViewModel.putEditDataValue(
                            itemId =item.idKerusakan,
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
    }

    private fun setupRecycler(listJenisHp: List<JenisHp>) {
        binding.rvSkills.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listJenisHp))
            withItem<JenisHp, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.text = item.jenisNama
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        chooseViewModel.putEditDataValue(
                            itemId =item.jenisId,
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_HP,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
    }

    private fun observeWhenDataSaved() {
        chooseViewModel.chooseServiceResponse.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    Snackbar.make(requireContext(), requireView(), "Berhasil menyimpan keahlian, mohon login untuk memasukin halaman utama!", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.loginFragment)
                }
                is ApiEvent.OnFailed -> {
                    Snackbar.make(requireContext(), requireView(), "Gagal menyimpan keahlian, mohon coba lagi!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

internal enum class TypeInput(val inputType: String){
    ITEM_INPUT_TYPE_JENIS_HP("JENIS_HP"),
    ITEM_INPUT_TYPE_JENIS_KERUSAKAN("JENIS_KERUSAKAN"),
    ITEM_INPUT_TYPE_DESKRIPSI("DESKRIPSI")
}
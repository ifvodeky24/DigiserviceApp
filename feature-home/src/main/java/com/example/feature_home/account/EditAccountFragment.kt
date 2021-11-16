package com.example.feature_home.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.ApiException
import com.example.core_data.api.request.TeknisiRequest
import com.example.core_data.api.request.UserRequest
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.core_resource.hideProgressDialog
import com.example.core_resource.showApiFailedDialog
import com.example.core_resource.showProgressDialog
import com.example.core_util.dismissKeyboard
import com.example.core_util.toEditable
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentEditAccountBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAccountFragment : Fragment() {

    private var _binding: FragmentEditAccountBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    private var teknisiId: Int? = null
    private var userId: Int? = null

    private val textBtnNext by lazy {
        "LANJUTKAN DAFTAR"
    }
    private val textHintEmptyEmail by lazy {
        "Email harus diisi"
    }
    private val textHintEmptyHp by lazy {
        "Nomor Handphone harus diisi"
    }
    private val textHintEmptyName by lazy {
        "Nama harus diisi"
    }
    private val textHintEmptyPassword by lazy {
        "Password harus diisi"
    }
    private val textHintEmptyStoreName by lazy {
        "Nama toko harus diisi"
    }
    private val textHintEmptyStoreAddress by lazy {
        "Alamat toko harus diisi"
    }
    private val textHintEmptyStoreDescription by lazy {
        "Deskripsi harus diisi"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setInput()
    }

    private fun setInput() {
        with(binding){
            form {
                useRealTimeValidation(disableSubmit = true)
                inputLayout(R.id.edt_layout_name){
                    isNotEmpty().description(textHintEmptyName)
                }
                inputLayout(R.id.edt_layout_email){
                    isNotEmpty().description(textHintEmptyEmail)
                }
                inputLayout(R.id.edt_layout_hp){
                    isNotEmpty().description(textHintEmptyHp)
                }
                inputLayout(R.id.edt_layout_store_name){
                    isNotEmpty().description(textHintEmptyStoreName)
                }
                inputLayout(R.id.edt_layout_store_address){
                    isNotEmpty().description(textHintEmptyStoreAddress)
                }
                inputLayout(R.id.edt_layout_store_desc){
                    isNotEmpty().description(textHintEmptyStoreDescription)
                }
                submitWith(R.id.btn_update) { updateEdit() }
            }
        }
    }

    private fun updateEdit() {
        dismissKeyboard()
        val  userRequest = UserRequest(userId ?: 0, nama = "${binding.edtInputName.text}", email = "${binding.edtInputEmail.text}", noHp = "${binding.edtInputHp.text}")
        val  teknisiRequest = TeknisiRequest(teknisiId ?: 0, namaToko = "${binding.edtInputStoreName.text}", teknisiAlamat = "${binding.edtInputStoreAddress.text}", deskripsi = "${binding.edtInputStoreDesc.text}")
        val jenisHpRequest = accountViewModel.getFinalJenisHpRequest()
        val jenisKerusakanHpRequest = accountViewModel.getFinalJenisKerusakanHpRequest()
        accountViewModel.updateTechinicial(teknisiId ?: 0, user = userRequest, teknisi = teknisiRequest, jenisHp = jenisHpRequest, jenisKerusakanHp = jenisKerusakanHpRequest )

        accountViewModel.isSaveForm.observe(viewLifecycleOwner){ event ->
            when (event) {
                is ApiEvent.OnProgress -> {
                    showProgressDialog()
                }
                is ApiEvent.OnSuccess -> if (event.hasBeenConsumed.not()) {
                    hideProgressDialog()
                    Toast.makeText(requireContext(), "Berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.accountFragment)
                    //requireActivity().finish()
                }
                is ApiEvent.OnFailed -> if (event.hasBeenConsumed.not()) {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }


    private fun observer() {

        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            binding.edtInputHp.isEnabled = false
            binding.edtInputEmail.isEnabled = false
            if (auth?.level == "teknisi"){
                userId = auth.id
                teknisiId = auth.teknisiId
                binding.edtInputEmail.text = auth.email.toEditable()
                binding.edtInputName.text = auth.name.toEditable()
                accountViewModel.setCurrentTechinicial(auth.email)

                accountViewModel.technicial.observe(viewLifecycleOwner, { tech ->
                    if (tech?.teknisiFoto?.isNotEmpty() == true) binding.imageProfile.load(tech.teknisiFoto){
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }

                    binding.edtInputStoreName.text = tech?.teknisiNamaToko?.toEditable()
                    binding.edtInputHp.text = tech?.teknisiHp?.toEditable()
                    binding.edtInputStoreDesc.text = tech?.teknisiDeskripsi?.toEditable()
                    binding.edtInputStoreAddress.text = tech?.teknisiAlamat?.toEditable()

                    accountViewModel.setCurrentSkill(tech?.teknisiId ?: 0)
                })

                accountViewModel.liveSkils.observe(viewLifecycleOwner, { event ->
                    when(event)
                    {
                        is ApiEvent.OnSuccess -> event.getData()?.let {
                            setupRecyclerSkils(it)
                        }
                        is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                        {
                            //hideProgress(true)
                        }
                    }
                })

            }
            else{
                binding.edtInputEmail.text = auth?.email?.toEditable()
                binding.edtInputName.text = auth?.name?.toEditable()
            }

        })
    }

    private fun setupRecyclerSkils(listSkils: ResultSkils) {
        accountViewModel jenisKerusakan listSkils.skils
        accountViewModel jenisHp listSkils.jenisHp
        binding.rvSkils.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.skils))
            withItem<Skils, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.isChecked = true
                    accountViewModel.putEditDataValue(
                        inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN,
                        itemId =item.id,
                        indexId = index,
                        default = true
                    )
                    titleCheckBox.text = item.namaKerusakan
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        accountViewModel.putEditDataValue(
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN,
                            itemId =item.id,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
        binding.rvHpType.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.jenisHp))
            withItem<JenisHp, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.isChecked = true
                    accountViewModel.putEditDataValue(
                        inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_HP,
                        itemId =item.id,
                        indexId = index,
                        default = true
                    )
                    titleCheckBox.text = item.jenisNama
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        accountViewModel.putEditDataValue(
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_HP,
                            itemId =item.id,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
    }

}

internal enum class TypeInput(val inputType: String){
    ITEM_INPUT_TYPE_JENIS_HP("JENIS_HP"),
    ITEM_INPUT_TYPE_JENIS_KERUSAKAN("JENIS_KERUSAKAN"),
}
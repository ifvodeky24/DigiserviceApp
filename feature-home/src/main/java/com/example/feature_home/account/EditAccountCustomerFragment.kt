package com.example.feature_home.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.commit
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import androidx.navigation.fragment.findNavController
import com.example.core_resource.hideProgressDialog
import com.example.core_resource.showApiFailedDialog
import com.example.core_resource.showProgressDialog
import com.example.core_util.bindLifecycle
import com.example.core_util.dismissKeyboard
import com.example.core_util.toEditable
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentEditAccountCustomerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditAccountCustomerFragment : Fragment() {

    private var _binding: FragmentEditAccountCustomerBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    private var pelangganId: Int? = null
    private var userId: Int? = null

    private val textHintEmptyEmail by lazy {
        "Email harus diisi"
    }
    private val textHintEmptyHp by lazy {
        "Nomor Handphone harus diisi"
    }
    private val textHintEmptyName by lazy {
        "Nama harus diisi"
    }
    private val textHintEmptyStoreAddress by lazy {
        "Alamat toko harus diisi"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditAccountCustomerBinding.inflate(inflater, container, false)
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
                inputLayout(R.id.edt_layout_store_address){
                    isNotEmpty().description(textHintEmptyStoreAddress)
                }
                submitWith(R.id.btn_update) { updateEdit() }
            }
            btnUpdate.bindLifecycle(viewLifecycleOwner)
        }
    }

    private fun updateEdit() {
        dismissKeyboard()
        accountViewModel.updateCustomer(
            pelangganId ?: 0,
            userId = userId ?: 0,
            pelangganNama = "${binding.edtInputName.text}",
            pelangganEmail = "${binding.edtInputEmail.text}",
            pelangganAlamat = "${binding.edtInputStoreAddress.text}",
            pelangganHp = "${binding.edtInputHp.text}"
        )
    }


    private fun observer() {
        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            binding.edtInputEmail.isEnabled = false
            with(binding){
                auth?.let {
                    userId = auth.id
                    pelangganId = auth.pelangganId
                    if (auth.foto.isNotEmpty()) imageProfile.load(auth.foto){
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                    edtInputName.text = auth.name.toEditable()
                    edtInputEmail.text = auth.email.toEditable()
                    edtInputHp.text = auth.hp.toEditable()
                    edtInputStoreAddress.text = auth.alamat.toEditable()
                }
            }
        })

        accountViewModel.isSaveForm.observe(viewLifecycleOwner){ event ->
            when (event) {
                is ApiEvent.OnProgress -> {
                    showProgressDialog()
                }
                is ApiEvent.OnSuccess -> if (event.hasBeenConsumed.not()) {
                    hideProgressDialog()
                    Toast.makeText(requireContext(), getString(R.string.success_updated), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.accountFragment)
                }
                is ApiEvent.OnFailed -> if (event.hasBeenConsumed.not()) {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }
}

fun Fragment.replaceFragmentEditAccountCustomer(resId: Int){
    val editAccountCustomerFragment = EditAccountCustomerFragment()
    childFragmentManager.commit {
        replace(resId,editAccountCustomerFragment)
    }
}
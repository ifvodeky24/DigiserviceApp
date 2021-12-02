package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.core_data.APP_PELANGGAN_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.core_data.domain.auth.isConsument
import com.example.core_data.domain.auth.isTechnician
import com.example.core_data.domain.servicehp.ServiceHandphoneByTechnicianGetAll
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.feature_home.HomeViewModel
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.account.ItemViewHolder
import com.example.feature_home.account.TypeInput
import com.example.feature_home.databinding.FragmentServiceBinding
import com.example.feature_home.viewHolder.ItemPopulerViewHolder
import com.example.feature_home.viewHolder.ItemServiceHandphoneTechnicianViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceFragment : Fragment() {

    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!
    private lateinit var drawer: DrawerLayout

    private val accountViewModel: AccountViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val serviceViewModel: ServiceViewModel by viewModel()
    private val serviceHandphoneViewModel: ServiceHandphoneViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        initComponent()
        setInput()
    }

    private fun setInput() {
        binding.btnFilter.setOnClickListener {
            val listJenisHp = serviceViewModel.getFinalJenisHpRequest()
            val listJenisKerusakan = serviceViewModel.getFinalJenisKerusakanHpRequest()
            serviceViewModel.filterTechnicianGetAll(listJenisHp, listJenisKerusakan)
            drawer.closeDrawer(GravityCompat.END)
        }
    }

    private fun observer() {

        accountViewModel.authUser.observe(viewLifecycleOwner) { auth ->
            auth?.let {
                serviceViewModel.isConsument = auth.isConsument
                if (auth.isConsument) {
                    binding.toolbar.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.filter -> {
                                drawer.openDrawer(GravityCompat.END)
                                true
                            }
                            else -> false
                        }
                    }
                    accountViewModel.getJenisKerusakanHpAll()
                    accountViewModel.liveSkils.observe(viewLifecycleOwner, { event ->
                        when(event) {
                            is ApiEvent.OnSuccess -> event.getData()?.let {
                                setupRecyclerSkils(it)
                            }
                            is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) { }
                        }
                    })

                    homeViewModel.technicianGetAll()
                    homeViewModel.technicianGetAllResponse.observe(viewLifecycleOwner, { technicianGetAll ->
                        when (technicianGetAll) {
                            is ApiEvent.OnProgress -> {}
                            is ApiEvent.OnSuccess -> technicianGetAll.getData()?.let {
                                onDataTechnicianGetAllLoaded(technicianGetAll.getData()!!)
                            }
                            is ApiEvent.OnFailed -> if (!technicianGetAll.hasNotBeenConsumed) {
                                Timber.d("Error.${technicianGetAll.getException()}")
                            }
                        }
                    })

                    serviceViewModel.filterTechnicianGetAllResponse.observe(viewLifecycleOwner){ event ->
                        when(event){
                            is ApiEvent.OnProgress -> { }
                            is ApiEvent.OnSuccess -> event.getData()?.let { listTechnicianAll ->
                                onDataTechnicianGetAllLoaded(listTechnicianAll)
                            }
                            is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) { }
                        }
                    }

                }
                else if (auth.isTechnician) {
                    serviceHandphoneViewModel.getServiceHandphoneByTechnicianId(technicianId = auth.teknisiId)
                    serviceHandphoneViewModel.serviceHandphoneByTechnician.observe(viewLifecycleOwner) { event ->
                        when(event) {
                            is ApiEvent.OnProgress -> {}
                            is ApiEvent.OnSuccess -> event.getData()?.let {
                                onDataServiceHandphoneGetAllLoaded(it)
                            }
                            is ApiEvent.OnFailed -> {}
                        }
                    }
                }
            }

        }

        serviceViewModel.filterTechnicianGetAllResponse.observe(viewLifecycleOwner){ event ->
            when(event){
                is ApiEvent.OnProgress -> {

                }
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    onDataTechnicianGetAllLoaded(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) {
                    // hideProgress(true)
                }
            }
        }

    }

    private fun onDataTechnicianGetAllLoaded(data: List<TechnicianGetAll>) {
            binding.recyclerView.setup{
                withLayoutManager(GridLayoutManager(requireContext(),2))
                withDataSource(dataSourceTypedOf(data))
                withItem<TechnicianGetAll, ItemPopulerViewHolder>(R.layout.item_teknisi_service){
                    onBind(::ItemPopulerViewHolder){ _, item ->
                        tvTeknisiName.text = item.teknisiNama
                        tvRating.text = String.format("%.1f", (item.teknisiTotalScore/item.teknisiTotalResponden)).toDouble().toString()
                        Glide
                            .with(requireActivity())
                            .load(APP_TEKNISI_IMAGES_URL+item.teknisiFoto)
                            .centerCrop()
                            .into(ivTeknisi)
                        layoutCard.setOnClickListener {
                            val directionTechnicianGetAll = ServiceFragmentDirections.actionServiceFragmentToServiceDetailFragment(item)
                            findNavController().navigate(directionTechnicianGetAll)
                        }
                    }
                }
            }
            binding.recyclerView.setPadding(0, 0, 16, 0)
        }

    private fun initComponent() {
        drawer = binding.drawerLayout
    }

    private fun setupRecyclerSkils(listSkils: ResultSkils){
        serviceViewModel jenisKerusakan listSkils.skils
        serviceViewModel jenisHp listSkils.jenisHp
        binding.rvJenisHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.skils))
            withItem<Skils, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.text = item.namaKerusakan
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        serviceViewModel.putEditDataValue(
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN,
                            itemId =item.idJenisKerusakan,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
        binding.rvJenisKerusakanHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.jenisHp))
            withItem<JenisHp, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.text = item.jenisNama
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        serviceViewModel.putEditDataValue(
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_HP,
                            itemId =item.jenisId,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
    }

    private fun onDataServiceHandphoneGetAllLoaded(data: List<ServiceHandphoneByTechnicianGetAll>) {
        if (data.isNotEmpty()) {
            binding.recyclerView.setup {
                withLayoutManager(LinearLayoutManager(context))
                withDataSource(dataSourceTypedOf(data))
                withItem<ServiceHandphoneByTechnicianGetAll, ItemServiceHandphoneTechnicianViewHolder>(R.layout.item_service_technician) {
                    onBind(::ItemServiceHandphoneTechnicianViewHolder) { _, item ->
                        val customerName = item.pelangganNama.run {
                            if (length >= 18) {
                                "${this.slice(0..16)}..."
                            } else {
                                this
                            }
                        }
                        tvServiceHpCustomerName.text = customerName
                        tvServiceHpStatus.text = item.statusService
                        tvServiceHpDate.text = item.createdAt
                        tvServiceHpType.text = item.jenisHp
                        tvServiceHpDamageType.text = item.jenisKerusakan

                        Glide.with(this@ServiceFragment)
                            .load(APP_PELANGGAN_IMAGES_URL+item.pelangganFoto)
                            .transform(CircleCrop())
                            .into(ivServiceHpUserPhoto)

                        layoutCard.setOnClickListener {
                            val toServiceHandphoneDetail = ServiceFragmentDirections.actionServiceFragmentToServiceHandphoneTechnicianFragment(item)
                            findNavController().navigate(toServiceHandphoneDetail)
                        }
                    }
                }
            }
        }
        binding.recyclerView.setPadding(0, 0, 16, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


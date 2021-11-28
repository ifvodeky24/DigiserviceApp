package com.example.feature_home.service

import androidx.lifecycle.*
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ListJenisHp
import com.example.core_data.domain.ListSkils
import com.example.core_data.domain.Skils
import com.example.core_data.domain.technician.ListTechnicianGetAll
import com.example.core_data.repository.TechnicianRepository
import com.example.feature_home.account.TypeInput
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ServiceViewModel(
    private val technicianRepository: TechnicianRepository,
) : ViewModel() {

    private val _filterTechnicianGetAllResponse = MutableLiveData<ApiEvent<ListTechnicianGetAll?>>()
    val filterTechnicianGetAllResponse: LiveData<ApiEvent<ListTechnicianGetAll?>> = _filterTechnicianGetAllResponse

    private val optionJenisHpForPost =HashMap<Int, JenisHp>()

    private val optionJenisKerusakanForPost =HashMap<Int, Skils>()

    fun filterTechnicianGetAll(jenisHp: List<Int>, jenisKerusakan: List<Int>) {
        viewModelScope.launch {
            technicianRepository.filterTechnician(jenisHp, jenisKerusakan)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _filterTechnicianGetAllResponse.value = it }
        }
    }

    private var listJenisHp: ListJenisHp? = null
    private var listJenisKerusakan: ListSkils? = null

    var isConsument = false

    infix fun jenisKerusakan(jenisKerusakanList: ListSkils){
        listJenisKerusakan = jenisKerusakanList
    }

    infix fun jenisHp(jenisHpList: ListJenisHp){
        listJenisHp = jenisHpList
    }

    fun getFinalJenisHpRequest() : List<Int> {
        val listJenisHp = mutableListOf<Int>()
        optionJenisHpForPost.values.filter{ it.value == "1" }.forEach {
            listJenisHp.add(it.jenisId)
        }
        return listJenisHp
    }

    fun getFinalJenisKerusakanHpRequest() : List<Int> {
        val listJenisKerusakanHp = mutableListOf<Int>()
        optionJenisKerusakanForPost.values.filter{ it.value.isNotEmpty() }.forEach {
            listJenisKerusakanHp.add(it.idJenisKerusakan)
        }
        return listJenisKerusakanHp
    }

    internal fun putEditDataValue(
        inputType: TypeInput,
        default: Boolean = false,
        itemId: Int = 0,
        indexId: Int,
        value: String = "0"
    )
    {
        //putFormDataValue(inputType, indexId, value)
        when (inputType){
            TypeInput.ITEM_INPUT_TYPE_JENIS_HP -> {
                val jenisHpChange = listJenisHp?.firstOrNull { it.jenisId == itemId }
                jenisHpChange?.let {
                    jenisHpChange.value = if (default){
                        "1"
                    }
                    else{
                        if (value.isNotEmpty()) value else ""
                    }
                    jenisHpChange.isChecked = true
                    optionJenisHpForPost[indexId] = jenisHpChange
                }
            }
            TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN -> {
                val jenisKerusakanChange = listJenisKerusakan?.firstOrNull { it.idJenisKerusakan == itemId }
                jenisKerusakanChange?.let {
                    jenisKerusakanChange.value = if (default){
                        "1"
                    }
                    else{
                        if (value.isNotEmpty()) value else ""
                    }
                    jenisKerusakanChange.isChecked = true
                    optionJenisKerusakanForPost[indexId] = jenisKerusakanChange
                }
            }
        }

    }

}
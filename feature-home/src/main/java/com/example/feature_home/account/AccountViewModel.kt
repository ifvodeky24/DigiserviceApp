package com.example.feature_home.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.*
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.*
import com.example.core_data.domain.auth.Auth
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

class AccountViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email: String = ""

    private val _authUser = MutableLiveData<Auth?>()
    val authUser: LiveData<Auth?> = _authUser

    private val _technicial = MutableLiveData<TechnicianGetAll?>()
    val technicial: LiveData<TechnicianGetAll?> = _technicial

    private val isUpdate = MutableLiveData<Boolean>()
    val isUpdateProfile: LiveData<Boolean> = isUpdate

    private val saveForm = MutableLiveData<ApiEvent<CommonResponse?>>()
    val isSaveForm: LiveData<ApiEvent<CommonResponse?>> = saveForm

    private val _liveSkils = MutableLiveData<ApiEvent<ResultSkils?>>()
    val liveSkils: LiveData<ApiEvent<ResultSkils?>> = _liveSkils

    private val optionJenisHpForPost =HashMap<Int, JenisHp>()

    private val optionJenisKerusakanForPost =HashMap<Int, Skils>()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUserAsFlow()
                .map { it.getData() }
                .collect {
                    _authUser.value = it
                }
        }
    }

    fun setCurrentTechinicial(email: String){
        viewModelScope.launch {
            authRepository.getCurrentTechnicianAsFlow(email)
                .map { it.getData() }
                .collect {
                    _technicial.value = it
                }
        }
    }

    fun setCurrentSkill(teknisiId: Int){
        viewModelScope.launch {
            authRepository.getCurrentSkilAll(teknisiId)
                .collect {
                    _liveSkils.value = it
                }
        }
    }

    private var listJenisHp: ListJenisHp? = null
    private var listJenisKerusakan: ListSkils? = null

    infix fun jenisKerusakan(jenisKerusakanList: ListSkils){
        listJenisKerusakan = jenisKerusakanList
    }

    fun getFinalJenisHpRequest() : ListJenisHpRequest {
        val listJenisHp = mutableListOf<JenisHpRequest>()
        optionJenisHpForPost.values.filter{ it.value == "1" }.forEach {
            listJenisHp.add(JenisHpRequest(it.teknisiJenisHpId, it.jenisId))
        }
        return listJenisHp
    }

    fun getFinalJenisKerusakanHpRequest() : ListJenisKerusakanRequest {
        val listJenisKerusakanHp = mutableListOf<JenisKerusakanRequest>()
        optionJenisKerusakanForPost.values.filter{ it.value.isNotEmpty() }.forEach {
            listJenisKerusakanHp.add(JenisKerusakanRequest(it.teknisiKerusakanJenisHpId, it.jenisKerusakanHpId))
        }
        return listJenisKerusakanHp
    }

    infix fun jenisHp(jenisHpList: ListJenisHp){
        listJenisHp = jenisHpList
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
                val jenisHpChange = listJenisHp?.firstOrNull { it.id == itemId }
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
                val jenisKerusakanChange = listJenisKerusakan?.firstOrNull { it.id == itemId }
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

    fun updateTechinicial(teknisiId: Int, user: UserRequest, teknisi: TeknisiRequest, jenisKerusakanHp: ListJenisKerusakanRequest, jenisHp: ListJenisHpRequest){
        viewModelScope.launch {
            authRepository.updateTeknisi(teknisiId, user = user, teknisi = teknisi, jenisKerusakanHp = jenisKerusakanHp, jenisHp = jenisHp)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    isUpdate.value = it is ApiEvent.OnSuccess<*>
                    saveForm.value = it
                }
        }
    }
}
package com.example.feature_auth.choose

import androidx.lifecycle.*
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.RequestChoose
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.*
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChooseViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _liveJenisHp = MutableLiveData<ApiEvent<ListJenisHp?>>()
    val liveJenisHp: LiveData<ApiEvent<ListJenisHp?>> = _liveJenisHp

    private val _liveJenisKerusakan = MutableLiveData<ApiEvent<ListJenisKerusakan?>>()
    val liveJenisKerusakan: LiveData<ApiEvent<ListJenisKerusakan?>> = _liveJenisKerusakan

    private val optionJenisHpForPost =HashMap<Int, JenisHp>()

    private val optionJenisKerusakanForPost =HashMap<Int, JenisKerusakan>()

    private val formInputDesc = HashMap<Int, String>()

    private val _chooseServiceResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val chooseServiceResponse: LiveData<ApiEvent<CommonResponse?>> = _chooseServiceResponse

    private val _isFormCompleted = MutableLiveData(false)
    internal val isFormCompleted = _isFormCompleted

    private val teknisiId = MutableLiveData<Int>()

    init {
        viewModelScope.launch {
            authRepository.getCurrentUserAsFlow()
                .map { it.getData() }
                .collect {
                    if (it != null) {
                        teknisiId.value = it.teknisiId
                    }
                }
        }
    }

    fun jenisHp() {
        viewModelScope.launch {
            authRepository.getJenisHpAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisHp.value = it
                }
        }
    }

    private var listJenisHp: ListJenisHp? = null

    private var listJenisKerusakan: ListJenisKerusakan? = null

    infix fun jenisHp(jenisHpList: ListJenisHp){
        listJenisHp = jenisHpList
    }

    infix fun jenisKerusakan(jenisKerusakanList: ListJenisKerusakan){
        listJenisKerusakan = jenisKerusakanList
    }

    fun jenisKerusakan() {
        viewModelScope.launch {
            authRepository.getJenisKerusakanAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisKerusakan.value = it
                }
        }
    }

    fun getFinalInput() : RequestChoose
    {
        val deskripsi = formInputDesc[-1]
        val jenisHp = optionJenisHpForPost.values.toList()
        val jenisKerusakan = optionJenisKerusakanForPost.values.toList()
        return RequestChoose(
            teknisiId = teknisiId.value as Int,
            deskripsi = "$deskripsi",
            jenisHp = jenisHp.toRequest(),
            jenisKerusakan = jenisKerusakan.toRequest()
        )
    }

    internal fun putEditDataValue(
        inputType: TypeInput,
        itemId: Int = 0,
        indexId: Int,
        value: String
    )
    {
        putFormDataValue(inputType, indexId, value)
        when (inputType){
            TypeInput.ITEM_INPUT_TYPE_JENIS_HP -> {
                val jenisHpChange = listJenisHp?.firstOrNull { it.jenisId == itemId }
                jenisHpChange?.let {
                    jenisHpChange.copy(
                        value = if (value.isNotEmpty()) value else "",
                        isChecked = true
                    )
                    optionJenisHpForPost[indexId] = jenisHpChange
                }
            }
            TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN -> {
                val jenisKerusakanChange = listJenisKerusakan?.firstOrNull { it.idKerusakan == itemId }
                jenisKerusakanChange?.let {
                    jenisKerusakanChange.copy(
                        value = if (value.isNotEmpty()) value else "",
                        isChecked = true
                    )
                    optionJenisKerusakanForPost[indexId] = jenisKerusakanChange
                }
            }
            TypeInput.ITEM_INPUT_TYPE_DESKRIPSI -> {
                formInputDesc[indexId] = value
            }
        }

    }

    private val formDataValueDeskripsi = HashMap<Int, String>()
    private val formDataValueJenisHp = HashMap<Int, String>()
    private val formDataValueJenisKerusakan = HashMap<Int, String>()

    private fun putFormDataValue(inputType: TypeInput, indexId: Int, value: String) {
        when(inputType){
            TypeInput.ITEM_INPUT_TYPE_DESKRIPSI -> {
                val mapDeskripsi = formDataValueDeskripsi
                mapDeskripsi[indexId] = value
                mapDeskripsi.values.any { it != "" }.also {
                    disableEnableButton(1, it)
                }
            }
            TypeInput.ITEM_INPUT_TYPE_JENIS_HP -> {
                val mapJenisHp = formDataValueJenisHp
                mapJenisHp[indexId] = value
                mapJenisHp.values.any { it != "0" }.also {
                    disableEnableButton(2, it)
                }
            }
            TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN -> {
                val mapJenisKerusakan = formDataValueJenisKerusakan
                mapJenisKerusakan[indexId] = value
                mapJenisKerusakan.values.any { it != "0" }.also {
                    disableEnableButton(3, it)
                }
            }
        }
    }

    private val formAllDataValue = HashMap<Int, Boolean>()
    private fun disableEnableButton(index: Int, value: Boolean) {
        val mapAllValue = formAllDataValue
        mapAllValue[index] = value
        mapAllValue.values.map { it }.apply {
            if (size == 3) _isFormCompleted.postValue(this[0] && this[1] && this[2])
        }
    }

    fun saveForm(chooseRequest: RequestChoose){
        viewModelScope.launch {
            authRepository.saveFormChoose(chooseRequest)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _chooseServiceResponse.value = it
                }
        }
    }
}
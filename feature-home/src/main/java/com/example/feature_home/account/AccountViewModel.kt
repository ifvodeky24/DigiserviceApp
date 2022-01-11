package com.example.feature_home.account

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
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

class AccountViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val auth = liveData<Auth?> {
        emit(authRepository.getAuth())
    }

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

    private val isPhotoTeknisiUpdate = MutableLiveData<ApiEvent<CommonResponse?>>()
    val photoTeknisiUpdate: LiveData<ApiEvent<CommonResponse?>> = isPhotoTeknisiUpdate


    // region pelanggan

    private val isPhotoPelangganUpdate = MutableLiveData<ApiEvent<CommonResponse?>>()
    val photoPelangganUpdate: LiveData<ApiEvent<CommonResponse?>> = isPhotoPelangganUpdate

    private val isIdentitasPelangganUpdate = MutableLiveData<ApiEvent<CommonResponse?>>()
    val identitasPelangganUpdate: LiveData<ApiEvent<CommonResponse?>> = isIdentitasPelangganUpdate

    // endregion

    private val isSertifikatTeknisiUpdate = MutableLiveData<ApiEvent<CommonResponse?>>()
    val sertifikatTeknisiUpdate: LiveData<ApiEvent<CommonResponse?>> = isSertifikatTeknisiUpdate

    private val isTempatUsahaTeknisiUpdate = MutableLiveData<ApiEvent<CommonResponse?>>()
    val tempatUsahaTeknisiUpdate: LiveData<ApiEvent<CommonResponse?>> = isTempatUsahaTeknisiUpdate

    private val isIdentitasTeknisiUpdate = MutableLiveData<ApiEvent<CommonResponse?>>()
    val identitasTeknisiUpdate: LiveData<ApiEvent<CommonResponse?>> = isIdentitasTeknisiUpdate

    private val optionJenisHpForPost = HashMap<Int, JenisHp>()

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

    fun updateCustomer(pelangganId: Int, userId: Int, pelangganNama: String, pelangganEmail: String, pelangganAlamat: String, pelangganHp: String){
        viewModelScope.launch {
            authRepository.updatePelanggan(
                pelangganId = pelangganId,
                userId = userId,
                pelangganNama = pelangganNama,
                pelangganEmail = pelangganEmail,
                pelangganAlamat = pelangganAlamat,
                pelangganHp = pelangganHp
            )
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    isUpdate.value = it is ApiEvent.OnSuccess<*>
                    saveForm.value = it
                }
        }
    }

    fun getJenisKerusakanHpAll(){
        viewModelScope.launch {
            authRepository.getJenisKerusakanHpAll()
                .collect {
                    _liveSkils.value = it
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updatePhotoTeknisi(id: Int, filePath: String, uri: Uri, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            authRepository.updatePhotoTeknisi(id, filePath, uri, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { isPhotoTeknisiUpdate.value = it }
        }
    }

    // foto tempat usaha
    @RequiresApi(Build.VERSION_CODES.Q)
    fun updatePhotoTempatUsaha(id: Int, filePath: String, uri: Uri, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            authRepository.updatePhotoTeknisiTempatUsaha(id, filePath, uri, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { isTempatUsahaTeknisiUpdate.value = it }
        }
    }

    // foto identitas
    @RequiresApi(Build.VERSION_CODES.Q)
    fun updatePhotoIdentitas(id: Int, filePath: String, uri: Uri, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            authRepository.updatePhotoTeknisiIdentitas(id, filePath, uri, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { isIdentitasTeknisiUpdate.value = it }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updatePhotoPelanggan(id: Int, filePath: String, uri: Uri, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            authRepository.updatePhotoPelanggan(id, filePath, uri, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { isPhotoPelangganUpdate.value = it }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateIdentitasPelanggan(id: Int, filePath: String, uri: Uri, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            authRepository.updatePhotoPelangganIdentitas(id, filePath, uri, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { isIdentitasPelangganUpdate.value = it }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateSertifikatTeknisi(id: Int, filePath: String, uri: Uri, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            authRepository.updateSertifikatTeknisi(id, filePath, uri, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { isSertifikatTeknisiUpdate.value = it }
        }
    }

    fun updateAuthPhotoLocally(authId: Int, foto: String) = viewModelScope.launch {
        authRepository.updateAuthPhotoLocally(authId, foto)
    }

    fun updateAuthTempatUsahaLocally(authId: Int, tempatUsaha: String) = viewModelScope.launch {
        authRepository.updateAuthTempatUsahaLocally(authId, tempatUsaha)
    }

    fun updateAuthIdentitasLocally(authId: Int, identitas: String) = viewModelScope.launch {
        authRepository.updateAuthIdentitasLocally(authId, identitas)
    }

    fun updateAuthSertifikatLocally(authId: Int, sertifikat: String) = viewModelScope.launch {
        authRepository.updateAuthSertifikatLocally(authId, sertifikat)
    }
}
package com.example.core_data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import com.example.core_data.UploadRequestBody
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.request.*
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.auth.toDomain
import com.example.core_data.api.response.toDomain
import com.example.core_data.api.service.AuthService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.*
import com.example.core_data.domain.auth.Auth
import com.example.core_data.persistence.dao.AuthDao
import com.example.core_data.persistence.dao.TechnicianDao
import com.example.core_data.persistence.entity.auth.AuthEntity
import com.example.core_data.persistence.entity.auth.toDomain
import com.example.core_data.persistence.entity.auth.toEntity
import com.example.core_data.persistence.entity.technician.toDomain
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AuthRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val authService: AuthService,
    private val dao: AuthDao,
    private val techDao: TechnicianDao,
    private val jsonParser: Moshi,
) {

    fun getCurrentUserAsFlow() = dao.selectAuthAsFlow().map { auth ->
        ApiEvent.OnSuccess.fromCache(auth?.toDomain())
    }

    fun getCurrentTechnicianAsFlow(email: String) = techDao.getCurrentTechnicianAsFlow(email).map {
        ApiEvent.OnSuccess.fromCache(it.toDomain())
    }

    suspend fun getAuth() = dao.selectAuth()
        ?.toDomain()

    fun login(
        email: String,
        password: String
    ): Flow<ApiEvent<Auth>> = flow {
        runCatching {
            val apiId = AuthService.Login

            val apiResult = apiExecutor.callApi(apiId) {
                authService.login(email, password)
            }

            val apiEvent: ApiEvent<Auth> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()

                is ApiResult.OnSuccess -> with(apiResult.response.result) {
                    dao.replace(this.toDomain().toEntity().copy(
                        isLogin = true
                    ))
                    ApiEvent.OnSuccess.fromServer(this.toDomain())
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<Auth>())
        }
    }

    fun registerService(
        email: String,
        teknisiNama: String,
        teknisiNoHp: String,
        password: String,
        teknisiNamaToko: String,
        teknisiAlamat: String,
        teknisiLat: Float,
        teknisiLng: Float,
        teknisiDeskripsi: String,
        fotoUri: Uri,
        fotoPath: String,
        sertifikatUri: Uri,
        sertifikatPath: String,
        identitasUri: Uri,
        identitasPath: String,
        tempatUsahaUri: Uri,
        tempatUsahaPath: String,
        contentResolver: ContentResolver,
        context: Context
    ) : Flow<ApiEvent<CommonResponse?>> = flow {

        // foto
        val fotoParcelFileDescriptor = contentResolver.openFileDescriptor(fotoUri, "r", null)
        val fotoInputStream = FileInputStream(fotoParcelFileDescriptor?.fileDescriptor)
        val fotoImageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val fotoFile = File(fotoImageDir, fotoPath)
        val fotoOutStream = FileOutputStream(fotoFile)
        fotoInputStream.copyTo(fotoOutStream)

        // sertifikat
        val sertifikatParcelFileDescriptor = contentResolver.openFileDescriptor(sertifikatUri, "r", null)
        val sertifikatInputStream = FileInputStream(sertifikatParcelFileDescriptor?.fileDescriptor)
        val sertifikatImageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val sertifikatFile = File(sertifikatImageDir, sertifikatPath)
        val sertifikatOutStream = FileOutputStream(sertifikatFile)
        sertifikatInputStream.copyTo(sertifikatOutStream)

        // identitas
        val identitasParcelFileDescriptor = contentResolver.openFileDescriptor(identitasUri, "r", null)
        val identitasInputStream = FileInputStream(identitasParcelFileDescriptor?.fileDescriptor)
        val identitasImageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val identitasFile = File(identitasImageDir, identitasPath)
        val identitasOutStream = FileOutputStream(identitasFile)
        identitasInputStream.copyTo(identitasOutStream)

        // tempat usaha
        val tempatUsahaParcelFileDescriptor = contentResolver.openFileDescriptor(tempatUsahaUri, "r", null)
        val tempatUsahaInputStream = FileInputStream(tempatUsahaParcelFileDescriptor?.fileDescriptor)
        val tempatUsahaImageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val tempatUsahaFile = File(tempatUsahaImageDir, tempatUsahaPath)
        val tempatUsahaOutStream = FileOutputStream(tempatUsahaFile)
        tempatUsahaInputStream.copyTo(tempatUsahaOutStream)

        val emailRB = email.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiNamaRB = teknisiNama.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiNoHpRB = teknisiNoHp.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiPasswordRB= password.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiNamaTokoRB= teknisiNamaToko.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiAlamatRB= teknisiAlamat.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiLatRB= "$teknisiLat".toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiLngRB= "$teknisiLng".toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiDeskripsiRB= teknisiDeskripsi.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiTotalScoreRB = "0".toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val teknisiTotalRespondentRB = "0".toRequestBody("multypart/form-data".toMediaTypeOrNull())

        val fotoBody = UploadRequestBody(fotoFile, "image")
        val sertifikatBody = UploadRequestBody(sertifikatFile, "image")
        val identitasBody = UploadRequestBody(identitasFile, "image")
        val tempatUsahaBody = UploadRequestBody(tempatUsahaFile, "image")

        runCatching {
            val apiId = AuthService.RegiterService

            val apiResult =apiExecutor.callApi(apiId) {
                authService.registerService(
                    teknisiNama = teknisiNamaRB,
                    teknisiNoHp = teknisiNoHpRB,
                    password = teknisiPasswordRB,
                    email = emailRB,
                    teknisiNamaToko = teknisiNamaTokoRB,
                    teknisiAlamat = teknisiAlamatRB,
                    teknisiLat = teknisiLatRB,
                    teknisiLng = teknisiLngRB,
                    teknisiDeskripsi = teknisiDeskripsiRB,
                    teknisiTotalScore = teknisiTotalScoreRB,
                    teknisiTotalRespondent = teknisiTotalRespondentRB,
                    foto = MultipartBody.Part.createFormData("teknisi_foto", fotoFile.name, fotoBody),
                    sertifikat = MultipartBody.Part.createFormData("teknisi_sertifikat", sertifikatFile.name, sertifikatBody),
                    identitas = MultipartBody.Part.createFormData("teknisi_identitas", identitasFile.name, identitasBody),
                    tempatUsaha = MultipartBody.Part.createFormData("teknisi_tempat_usaha", tempatUsahaFile.name, tempatUsahaBody)
                )
            }

            val apiEvent:ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        message.equals(ApiException.FailedResponse.MESSAGE_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)

        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    fun registerPelanggan(
        email: String,
        teknisiNama: String,
        teknisiNoHp: String,
        password: String,
        teknisiAlamat: String,
        teknisiLat: Float,
        teknisiLng: Float,
        fotoUri: Uri,
        fotoPath: String,
        identitasUri: Uri,
        identitasPath: String,
        contentResolver: ContentResolver,
        context: Context
    ) : Flow<ApiEvent<CommonResponse?>> = flow {

        // foto
        val fotoParcelFileDescriptor = contentResolver.openFileDescriptor(fotoUri, "r", null)
        val fotoInputStream = FileInputStream(fotoParcelFileDescriptor?.fileDescriptor)
        val fotoImageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val fotoFile = File(fotoImageDir, fotoPath)
        val fotoOutStream = FileOutputStream(fotoFile)
        fotoInputStream.copyTo(fotoOutStream)

        // identitas
        val identitasParcelFileDescriptor = contentResolver.openFileDescriptor(identitasUri, "r", null)
        val identitasInputStream = FileInputStream(identitasParcelFileDescriptor?.fileDescriptor)
        val identitasImageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val identitasFile = File(identitasImageDir, identitasPath)
        val identitasOutStream = FileOutputStream(identitasFile)
        identitasInputStream.copyTo(identitasOutStream)

        val fotoBody = UploadRequestBody(fotoFile, "image")
        val identitasBody = UploadRequestBody(identitasFile, "image")

        val emailRB = email.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val namaRB = teknisiNama.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val noHpRB = teknisiNoHp.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val passwordRB= password.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val alamatRB= teknisiAlamat.toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val latRB= "$teknisiLat".toRequestBody("multypart/form-data".toMediaTypeOrNull())
        val lngRB= "$teknisiLng".toRequestBody("multypart/form-data".toMediaTypeOrNull())

        runCatching {
            val apiId = AuthService.RegiterService

            val apiResult =apiExecutor.callApi(apiId) {
                authService.registerPelanggan(
                    pelangganNama = namaRB,
                    pelangganNoHp = noHpRB,
                    password = passwordRB,
                    email = emailRB,
                    pelangganAlamat = alamatRB,
                    pelangganLat = latRB,
                    pelangganLng = lngRB,
                    foto = MultipartBody.Part.createFormData("pelanggan_foto", fotoFile.name, fotoBody),
                    identitas = MultipartBody.Part.createFormData("pelanggan_identitas", identitasFile.name, identitasBody)
                )
            }

            val apiEvent:ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        message.equals(ApiException.FailedResponse.MESSAGE_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)

        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    fun getJenisHpAll() :Flow<ApiEvent<ListJenisHp>> = flow {
        runCatching {
            val apiId = AuthService.GetJenisHpAll
            val apiResult = apiExecutor.callApi(apiId) {
                authService.getJenisHpAll()
            }

            val apiEvent: ApiEvent<ListJenisHp> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        if (isEmpty())
                        {
                            ApiEvent.OnSuccess.fromServer(emptyList())
                        }
                        else
                        {
                            ApiEvent.OnSuccess.fromServer(this)
                        }
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ListJenisHp>())
        }
    }

    fun getCurrentSkilAll(teknisiId: Int) :Flow<ApiEvent<ResultSkils>> = flow {
        runCatching {
            val apiId = AuthService.SkilsBy
            val apiResult = apiExecutor.callApi(apiId) {
                authService.getCurrenSkils(teknisiId)
            }

            val apiEvent: ApiEvent<ResultSkils> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ResultSkils>())
        }
    }

    fun getJenisKerusakanAll() :Flow<ApiEvent<ListJenisKerusakan>> = flow {
        runCatching {
            val apiId = AuthService.GetJenisKerusakanAll
            val apiResult = apiExecutor.callApi(apiId) {
                authService.getJenisKerusakanAll()
            }

            val apiEvent: ApiEvent<ListJenisKerusakan> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        if (isEmpty())
                        {
                            ApiEvent.OnSuccess.fromServer(emptyList())
                        }
                        else
                        {
                            ApiEvent.OnSuccess.fromServer(this)
                        }
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ListJenisKerusakan>())
        }
    }

    fun saveFormChoose(
        choose: RequestChoose
    ) : Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = AuthService.RegiterService

            val apiResult =apiExecutor.callApi(apiId) {
                authService.saveChoose(choose)
            }

            val apiEvent:ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        message.equals(ApiException.FailedResponse.STATUS_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)

        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    fun updateTeknisi(
        teknisiId: Int,
        user: UserRequest,
        teknisi: TeknisiRequest,
        jenisKerusakanHp: ListJenisKerusakanRequest,
        jenisHp: ListJenisHpRequest,
    ) : Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = AuthService.UpdateTeknisi

            val apiResult = apiExecutor.callApi(apiId) {
                authService.updateTeknisi(
                    id = teknisiId,
                    request = RequestUpdateTeknisi(
                        user = user,
                        teknisi = teknisi,
                        jenisKerusakanHp = jenisKerusakanHp,
                        jenisHp = jenisHp,
                    )
                )
            }

            val apiEvent:ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        message.equals(ApiException.FailedResponse.STATUS_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)

        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    fun updatePelanggan(
        pelangganId: Int,
        userId: Int,
        pelangganNama: String,
        pelangganEmail: String,
        pelangganAlamat: String,
        pelangganHp: String,
    ) : Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = AuthService.UpdatePelanggan

            val apiResult = apiExecutor.callApi(apiId) {
                authService.updatePelanggan(
                    id = userId,
                    pelangganId = pelangganId,
                    pelangganNama = pelangganNama,
                    pelangganEmail = pelangganEmail,
                    pelangganAlamat = pelangganAlamat,
                    pelangganHp = pelangganHp,
                )
            }

            val apiEvent:ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        message.equals(ApiException.FailedResponse.STATUS_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)

        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }


    fun getJenisKerusakanHpAll() :Flow<ApiEvent<ResultSkils>> = flow {
        runCatching {
            val apiId = AuthService.JenisKerusakanHp
            val apiResult = apiExecutor.callApi(apiId) {
                authService.getJenisKerusakanHpAll()
            }

            val apiEvent: ApiEvent<ResultSkils> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ResultSkils>())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updatPhotoTeknisi(
        id: Int,
        filePath: String,
        imageUri: Uri,
        contentResolver: ContentResolver,
        context: Context
    ) : Flow<ApiEvent<CommonResponse?>> = flow{
        val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()

        val file = File(imageDir, filePath)
        val outStream = FileOutputStream(file)
        inputStream.copyTo(outStream)

        val body = UploadRequestBody(file, "image")

        runCatching {
            val apiId = AuthService.UpdatePhotoTeknisi
            val apiResult = apiExecutor.callApi(apiId){
                authService.updatePhotoTeknisi(
                    teknisiId = id,
                    foto = MultipartBody.Part.createFormData(
                        "teknisi_foto",
                        file.name,
                        body
                    )
                )
            }

            val apiEvent: ApiEvent<CommonResponse?> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        this!!.message.equals(ApiException.FailedResponse.MESSAGE_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }

            }

            emit(apiEvent)


        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updatePhotoPelanggan(
        id: Int,
        filePath: String,
        imageUri: Uri,
        contentResolver: ContentResolver,
        context: Context
    ) : Flow<ApiEvent<CommonResponse?>> = flow{
        val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val file = File(imageDir, filePath)
        val outStream = FileOutputStream(file)
        inputStream.copyTo(outStream)

//        val idRB: RequestBody = "$id".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = UploadRequestBody(file, "image")

        runCatching {
            val apiId = AuthService.UpdatePhotoPelanggan
            val apiResult = apiExecutor.callApi(apiId){
                authService.updatePhotoPelanggan(
                    pelangganId = id,
                    foto = MultipartBody.Part.createFormData(
                        "pelanggan_foto",
                        file.name,
                        body
                    )
                )
            }

            val apiEvent: ApiEvent<CommonResponse?> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        this!!.message.equals(ApiException.FailedResponse.MESSAGE_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }

            }

            emit(apiEvent)


        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun updateSertifikatTeknisi(
        id: Int,
        filePath: String,
        imageUri: Uri,
        contentResolver: ContentResolver,
        context: Context
    ) : Flow<ApiEvent<CommonResponse?>> = flow{
        val parcelFileDescriptor = contentResolver.openFileDescriptor(imageUri, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
        val file = File(imageDir, filePath)
        val outStream = FileOutputStream(file)
        inputStream.copyTo(outStream)

//        val idRB: RequestBody = "$id".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = UploadRequestBody(file, "image")

        runCatching {
            val apiId = AuthService.UpdateSertifikatTeknisi
            val apiResult = apiExecutor.callApi(apiId){
                authService.updateSertifikatTeknisi(
                    teknisiId = id,
                    foto = MultipartBody.Part.createFormData(
                        "teknisi_sertifikat",
                        file.name,
                        body
                    )
                )
            }

            val apiEvent: ApiEvent<CommonResponse?> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        this!!.message.equals(ApiException.FailedResponse.MESSAGE_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }

            }

            emit(apiEvent)


        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }


    suspend fun updateAuthPhotoLocally(authId: Int, foto: String) {
        dao.updateFoto(authId, foto)
    }

    suspend fun updateAuthSertifikatLocally(authId: Int, sertifikat: String) {
        dao.updateSertifikat(authId, sertifikat)
    }
}
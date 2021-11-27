package com.example.core_data.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.example.core_data.api.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.store.toDomain
import com.example.core_data.api.service.StoreService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.store.ListProductGetAll
import com.example.core_data.UploadRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*

class StoreRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val storeService: StoreService
) {
    fun productGetAll(): Flow<ApiEvent<ListProductGetAll>> = flow {
    runCatching {
            val apiId = StoreService.GetProductAll
            val apiResult = apiExecutor.callApi(apiId) {
                storeService.getProductAll()
            }

            val apiEvent: ApiEvent<ListProductGetAll> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result) {
                    toDomain().run {
                        if (isEmpty()) {
                            ApiEvent.OnSuccess.fromServer(emptyList())
                        } else {
                            ApiEvent.OnSuccess.fromServer(this)
                        }
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ListProductGetAll>())
        }
    }

    fun updateImageProduk(
        id: Int,
        filePath: String,
        uri: Uri,
        judul: String,
        deskripsi: String,
        harga: String,
        userId: String,
        jenisHpId: String,
        contentResolver: ContentResolver
    ) : Flow<ApiEvent<CommonResponse?>> = flow {

        //Init File
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r", null)

        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)

        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

        val file = File(imagesDir, filePath)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val body = UploadRequestBody(file, "image")

        // add another part within the multipart request
        val idRB: RequestBody = "$id".toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val judulRB: RequestBody = judul.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deskripsiRB: RequestBody = deskripsi.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val hargaRB: RequestBody = harga.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userIdRB: RequestBody = userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val jenisHpIdRB: RequestBody = jenisHpId.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val finalBody = body

        runCatching {
            val apiId = StoreService.UpdateProduct

            val apiResult = apiExecutor.callApi(apiId) {
                finalBody?.let {
                    storeService.updateImageProduk(
                        id = idRB,
                        jualJudul = judulRB,
                        jualDeskripsi = deskripsiRB,
                        jualHarga = hargaRB,
                        jualUserId = userIdRB,
                        jualJenisHp = jenisHpIdRB,
                        foto = MultipartBody.Part.createFormData(
                            "foto",
                            file.name,
                            body
                        ),
                    )
                }
            }

            val apiEvent: ApiEvent<CommonResponse?> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response) {
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

    fun uploadProduk(
        filePath: String,
        uri: Uri,
        judul: String,
        deskripsi: String,
        harga: String,
        userId: String,
        jenisHpId: String,
        contentResolver: ContentResolver
    ) : Flow<ApiEvent<CommonResponse?>> = flow {

        //Init File
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r", null)

        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)

        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()

        val file = File(imagesDir, filePath)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        val body = UploadRequestBody(file, "image")

        // add another part within the multipart request
        val judulRB: RequestBody = judul.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val deskripsiRB: RequestBody = deskripsi.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val hargaRB: RequestBody = harga.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val userIdRB: RequestBody = userId.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val jenisHpIdRB: RequestBody = jenisHpId.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val finalBody = body

        runCatching {
            val apiId = StoreService.UploadProduct

            val apiResult = apiExecutor.callApi(apiId) {
                finalBody?.let {
                    storeService.uploadProduk(
                        jualJudul = judulRB,
                        jualDeskripsi = deskripsiRB,
                        jualHarga = hargaRB,
                        jualUserId = userIdRB,
                        jualJenisHp = jenisHpIdRB,
                        foto = MultipartBody.Part.createFormData(
                            "foto",
                            file.name,
                            body
                        ),
                    )
                }
            }

            val apiEvent: ApiEvent<CommonResponse?> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response) {
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

    fun updateProduk(
        id: Int,
        judul: String,
        deskripsi: String,
        harga: Int,
        userId: Int,
        jenisHpId: Int,
    ) : Flow<ApiEvent<CommonResponse?>> = flow {

        runCatching {
            val apiId = StoreService.UploadProduct

            val apiResult = apiExecutor.callApi(apiId) {
                    storeService.updateProduk(
                        id = id,
                        jualJudul = judul,
                        jualDeskripsi = deskripsi,
                        jualHarga = harga,
                        jualUserId = userId,
                        jualJenisHp = jenisHpId,
                    )
            }

            val apiEvent: ApiEvent<CommonResponse?> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response) {
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

    //endregion
    // region upload image
    //item image upload
    private fun compressImage(file: File, uri: Uri, contentResolver: ContentResolver): ByteArray? {
        //    private File compressImage(File file) {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

        //      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        val filePath = file.path

        //        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        var bmp: Bitmap? = null
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            parcelFileDescriptor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

        val maxHeight = 1024f
        val maxWidth = 1024f

        //      max Height and width values of the compressed image is taken as 816x612
        if (actualHeight > maxHeight) {
            val tmpWidth = actualWidth
            val diff: Float = maxHeight / actualHeight.toFloat()
            actualWidth = (diff * tmpWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (actualWidth > maxWidth) {
            val tmpHeight = actualHeight
            val diff: Float = maxWidth / actualWidth.toFloat()
            actualHeight = (diff * tmpHeight).toInt()
            actualWidth = maxWidth.toInt()
        }

        //      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

        //      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

        //      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            //          load the bitmap from its path
            //            bmp = BitmapFactory.decodeFile(filePath, options);
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            parcelFileDescriptor.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp!!,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

        //      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val tmpPath = file.path.toLowerCase()
            return if (tmpPath.contains("png")) {
                scaledBitmap?.let { saveImage(contentResolver, it, "png") }
            } else if (tmpPath.contains("jpg") || tmpPath.contains("jpeg")) {
                scaledBitmap?.let { saveImage(contentResolver, it, "jpg") }
            } else if (tmpPath.contains("webp")) {
                scaledBitmap?.let { saveImage(contentResolver, it, "webp") }
            } else {
                scaledBitmap?.let { saveImage(contentResolver, it, "jpg") }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun saveImage(
        contentResolver: ContentResolver,
        bitmap: Bitmap,
        type: String
    ): ByteArray? {
        //        private File saveImage(ContentResolver contentResolver, Bitmap bitmap, @NonNull String name) throws IOException {
        val fos: OutputStream?
        val image: File
        var imageUri: Uri? = null
        val inputData: ByteArray
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "tmp_image.png")
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = Objects.requireNonNull(imageUri)?.let { contentResolver.openOutputStream(it) }
            image = File(imageUri.toString())
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
            image = File(imagesDir, "tmp_image.png.$type")
            fos = FileOutputStream(image)
            image.toURI()
        }
        when (type) {
            "png" -> bitmap.compress(Bitmap.CompressFormat.PNG, 80, fos)
            "webp" -> bitmap.compress(Bitmap.CompressFormat.WEBP, 80, fos)
            else -> bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        }
        Objects.requireNonNull(fos)?.close()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val iStream = Objects.requireNonNull(imageUri)?.let { contentResolver.openInputStream(it) }
            inputData = iStream?.let { getBytes(it) }!!
            contentResolver.delete(imageUri!!, "", null)
        } else {
            inputData = readBytesFromFile(image.path)!!
        }
        return inputData
    }

    private fun readBytesFromFile(filePath: String): ByteArray? {
        var fileInputStream: FileInputStream? = null
        var bytesArray: ByteArray? = null
        try {
            val file = File(filePath)
            bytesArray = ByteArray(file.length().toInt())

            //read file into bytes[]
            fileInputStream = FileInputStream(file)
            fileInputStream.read(bytesArray)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bytesArray
    }

    @Throws(IOException::class)
    fun getBytes(inputStream: InputStream): ByteArray? {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }


    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }


    fun deleteProduk(
        id: Int
    ) : Flow<ApiEvent<CommonResponse?>> = flow {

        runCatching {
            val apiId = StoreService.DeleteProduct

            val apiResult = apiExecutor.callApi(apiId) {
                storeService.deleteProduct(
                    id = id
                )
            }

            val apiEvent: ApiEvent<CommonResponse?> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response) {
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

}
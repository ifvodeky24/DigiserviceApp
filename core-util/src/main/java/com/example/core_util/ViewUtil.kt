package com.example.core_util

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.widget.ImageView
import androidx.fragment.app.Fragment
import coil.load
import java.io.InputStream
import java.lang.Exception

private var selected = false
private var imagePath = ""

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun ImageView.loadImage(uri: Uri?){
    if (this != null && uri != null){
        this.load(uri)
    }
    else{
        if (this != null){
            this.setImageResource(R.drawable.bg_image)
        }
    }
}

@SuppressLint( "WrongConstant", "Range")
fun Fragment.convertImagePath(data: Intent, selectedImage: Uri, filePathColumn: Array<String>): String{
    if (this != null && selectedImage != null) {
        var cursor: Cursor? = requireActivity().contentResolver.query(
            selectedImage,
            null, null, null, null
        )
        selected = true
        if (cursor != null) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            imagePath = cursor.getString(nameIndex)
            cursor?.close()
        }
    }
    return imagePath
}

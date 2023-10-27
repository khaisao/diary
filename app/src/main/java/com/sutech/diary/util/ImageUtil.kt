package com.sutech.diary.util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sutech.diary.database.DataStore
import com.sutech.diary.model.ImageObj
import com.sutech.journal.diary.diarywriting.lockdiary.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


object ImageUtil {

    fun setImage(image: ImageView, drawable_image: Int) {
        Glide.with(image.context).load(drawable_image).into(image)
    }

    fun setThemeForImageView(image: ImageView, context: Context) {
        DataStore.init(context)
        when (DataStore.getTheme()) {
            -1 -> {
                Glide.with(context).load(R.drawable.default_background).into(image)
            }

            0 -> {
                Glide.with(context).load(R.drawable.default_background).into(image)
            }

            1 -> {
                Glide.with(context).load(R.drawable.theme_1_background).into(image)

            }

            2 -> {
                Glide.with(context).load(R.drawable.theme_2_background).into(image)

            }

            3 -> {
                Glide.with(context).load(R.drawable.theme_3_background).into(image)

            }

            4 -> {
                Glide.with(context).load(R.drawable.theme_4_background).into(image)

            }

            5 -> {
                Glide.with(context).load(R.drawable.theme_5_background).into(image)

            }

            6 -> {
                Glide.with(context).load(R.drawable.theme_6_background).into(image)

            }

            7 -> {
                Glide.with(context).load(R.drawable.theme_7_background).into(image)

            }
            else -> {
                Glide.with(context).load(R.drawable.default_background).into(image)
            }
        }
    }

    fun setImage(image: ImageView, drawable_image: Uri) {
        Glide.with(image.context).load(drawable_image).into(image)
    }

    fun setImage(image: ImageView, url_image: Uri?, width: Int, height: Int) {
        Glide.with(image.context).load(url_image).thumbnail(0.01f).override(width, height)
            .placeholder(R.drawable.no_image)
            .error(R.color.black).into(image)
    }

    fun setImage(image: ImageView, url_image: String?) {
        Glide.with(image.context).load(url_image).placeholder(R.drawable.no_image)
            .error(R.color.black).into(image)
    }

    fun setImage(image: ImageView, url_image: Bitmap?) {
        Glide.with(image.context).load(url_image).placeholder(R.drawable.no_image)
            .error(R.color.black).into(image)
    }

    fun setImageByte(image: ImageView, url_image: ByteArray?) {
        Glide.with(image.context).load(url_image).placeholder(R.drawable.no_image)
            .error(R.color.black).load(url_image).into(image)
    }

    fun convertBitmapFromDrawable(res: Resources?, resId: Int): Bitmap {
        return BitmapFactory.decodeResource(res, resId)
    }

    fun imageToBitmap(res: Resources?, resId: Int): ByteArray {
        val bitmap = convertBitmapFromDrawable(res, resId)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)

        return stream.toByteArray()
    }

    fun convertBitmaptoFile(
        context: Context,
        bitmap: Bitmap,
        filename: String?
    ): File { //create a file to write bitmap data
        val f = File(context.cacheDir, filename)
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)
        val bitmapdata = bos.toByteArray()

//write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(f)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos!!.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return f
    }


    fun saveToInternalStorage(applicationContext: Context, image: ImageObj): String? {
        // path to /data/data/yourapp/app_data/imageDir
        val directory = getInternalPackage(applicationContext)

        // Create imageDir
        val mypath = File(directory, "${image.id}.png")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            image.bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mypath.absolutePath
    }

    private fun getInternalPackage(applicationContext: Context): File? {
        val cw = ContextWrapper(applicationContext)
        return cw.getDir("imageDir", Context.MODE_PRIVATE)
    }

    //    fun copyFileToInternal(context: Context, inputPath: String, id: String): String? {
//        val directory = getInternalPackage(context)
////        val src = Uri.parse(inputPath).toFile()
//        val src = File(Uri.parse(inputPath).path)
//
//        val mypath = File(directory, "${id}.png")
//        val inChannel: FileChannel? = FileInputStream(src).channel
//        val outChannel: FileChannel? = FileOutputStream(mypath).channel
//        try {
//            inChannel!!.transferTo(0, inChannel.size(), outChannel)
//        } catch (e: java.lang.Exception) {
//            Log.e("TAG", "copyFile error: ", e)
//        } finally {
//            inChannel?.close()
//            outChannel?.close()
//        }
//        return mypath.absolutePath
//    }
    fun getFilePath(context: Context, _uri: Uri): File {
        var filePath: String? = ""
        Log.d("", "URI = $_uri")
        if (_uri != null && "content" == _uri.scheme) {
            val cursor: Cursor? = context.getContentResolver()
                .query(_uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
            cursor?.moveToFirst()
            filePath = cursor?.getString(0)
            cursor?.close()
        } else {
            filePath = _uri!!.path
        }
        return File(filePath)
    }

    fun copyFileToInternal(context: Context, inputPath: String, id: String): String? {
        try {
            Log.d("hhhh", "copyFileToInternal: 1 ${id}")
            val outPath = context.filesDir.path + "/file"
            if (!File(outPath).exists()) {
                File(outPath).mkdirs()
            }
            val input = getFilePath(context, Uri.parse(inputPath))
            val output = File(
                outPath, "${id}.png"
            )
            input.copyTo(output)
            Log.d("hhhh", "copyFileToInternal: 2 ${inputPath}")

            Log.d("addMedia", "e.message.toString()")
            val oldPath = inputPath

//            media.path = output.path
//            Log.d("hhhh", "copyFileToInternal: 3  ${media.path}")
//            media.uri = output.toUri().toString()

            return output.path
        } catch (e: Exception) {
            Log.e("TAG", "copyFileToInternal: ${e.message}")
            return inputPath
        }
    }
}
package com.sutech.diary.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.sutech.diary.model.FolderObj
import com.sutech.diary.model.ImageObj
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.set

object DeviceUtil {
    var arrImage: ArrayList<ImageObj> = ArrayList()
    var arrVe: ArrayList<ImageObj> = ArrayList()


    fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }


    private fun getCount(
        context: Context,
        contentUri: Uri,
        bucketId: String
    ): Int {
        context.contentResolver.query(
            contentUri,
            null,
            " media_type = 3 AND " + MediaStore.Images.Media.BUCKET_ID + "=?",
            arrayOf(bucketId),
            null
        )
            .use { cursor -> return if (cursor == null || !cursor.moveToFirst()) 0 else cursor.count }
    }

    fun getFolderImage(context: Context): List<FolderObj> {

        val selection = " media_type = 3 "
        val arrFolder = ArrayList<FolderObj>()
        val folders = HashMap<Long, String>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.BUCKET_ID,
            MediaStore.Files.FileColumns.PARENT,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns._ID
        )
//        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = MediaStore.Files.getContentUri("external")
        val cursor =
            context.contentResolver.query(
                uri,
                projection,
                selection,
                null,
                null
            )
        if (cursor != null && cursor.count > 0) {
            val folderIdIndex: Int =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)
            val folderNameIndex: Int =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
            val trackIdIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val dateAddedIndex: Int =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT)
            while (cursor.moveToNext()) {
                try {
                    val folderId: Long = cursor.getLong(folderIdIndex)
                    if (!folders.containsKey(folderId)) {
                        val folderName: String = cursor.getString(folderNameIndex)
                        val folderPath: String = cursor.getString(trackIdIndex)
                        val folderParentId: Int = cursor.getInt(dateAddedIndex)
                        val folder = FolderObj(folderId, folderName)
                        folders[folderId] = folderName
                        arrFolder.add(folder)
                    }
                } catch (ex: NullPointerException) {
                }
            }
            // Close cursor
            cursor.close()
            folders.clear() //clear the hashmap becuase it's no more useful

        }
        return arrFolder
    }

    fun getAllImage(context: Context): ArrayList<ImageObj>? {
        val arrImage = ArrayList<ImageObj>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DISPLAY_NAME
        )
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val orderBy = MediaStore.Images.Media.DATE_ADDED
        val cursor = context.contentResolver.query(uri, projection, null, null, " $orderBy DESC ")
        if (cursor != null && cursor.count > 0) {
            val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val columnIndexName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)


            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getString(columnIndexId)
                    val pathFileUri =
                        Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
                    val image = ImageObj(id = id, path = pathFileUri.toString())

                    arrImage.add(image)


//                    }
                } catch (ex: NullPointerException) {
                    //ex.printStackTrace();
                }
            }
            // Close cursor
            cursor.close()
        }
        return arrImage
    }

    fun getImageFromFolder(context: Context, idFolder: Long): ArrayList<ImageObj>? {
        val selection = " media_type = 1 AND " + MediaStore.Images.Media.BUCKET_ID + "=?"
        val arrImage = ArrayList<ImageObj>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DISPLAY_NAME

        )
        val uri = MediaStore.Files.getContentUri("external")
        val cursor =
            context.contentResolver.query(
                uri,
                projection,
                selection,
                arrayOf(idFolder.toString()),
                null
            )
        if (cursor != null && cursor.count > 0) {
            val columnIndexId = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val columnIndexName = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            while (cursor.moveToNext()) {
                try {
                    val id = cursor.getString(columnIndexId)
                    val name = cursor.getString(columnIndexName)

                    val pathFileUri =
                        Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
                    val image = ImageObj(id = id, path = pathFileUri.toString())
                    arrImage.add(image)

                } catch (ex: NullPointerException) {
                    //ex.printStackTrace();
                }
            }
            // Close cursor
            cursor.close()
        }
        return arrImage
    }

    fun getImage(context: Context): ArrayList<String>? {
//        val selection =    MediaStore.Images.Media.MIME_TYPE + " LIKE Image"
        val rings = ArrayList<String>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
//            MediaStore.Audio.Media.ARTIST,
//            MediaStore.Audio.Media.TITLE,
            MediaStore.Images.Media.DATA
//            MediaStore.Audio.Media.DISPLAY_NAME,
//            MediaStore.Audio.Media.DURATION
        )
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor =
            context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.count > 0) {
//            val columnIndexId = cursor.getColumnIndexOrThrow( MediaStore.Audio.Media._ID)
            val columnIndexPath = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
//            val columnIndexTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
//            val columnIndexName = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
//            val columnIndexDuration = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//            val columnIndexArtist = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)


            while (cursor.moveToNext()) {
                try {
//                    val id = cursor.getString(columnIndexId)
                    val pathFile = cursor.getString(columnIndexPath)
//                    val name = cursor.getString(columnIndexName)
//                    val duration = cursor.getInt(columnIndexDuration) // Milliseconds
                    // String title = cursor.getString(columnIndexTitle);
//                    val artist = cursor.getString(columnIndexArtist)
                    val file = File(pathFile)
                    if (file.exists()) {
//                            Audio audio = new Audio(name, pathFile, file.getPath(), strDuration, artist);
//                        val GSC = Ring()
//                        GSC.musicId = id
//                        GSC.name = name
////                        GSC.songArtist = artist
//                        GSC.music_url = pathFile
//                        GSC.type = 0

                        rings.add(pathFile)
//                            int seconds = duration / 1000;
//                            if (seconds >= 5 && !name.endsWith(".wav")) {
//                                audio.setSeconds(duration / 1000);
//                                mListDataAudio.add(audio);
//                                Lo.d(TAG, "AUDIO ADDED: " + pathFile);
//                            }
                    }
                } catch (ex: NullPointerException) {
                    //ex.printStackTrace();
                }
            }
            // Close cursor
            cursor.close()
        }
        return rings
    }

}
package com.jhteck.cameratest

import android.os.Build
import android.os.Environment
import android.util.Log
import com.jhteck.icebox.utils.DateUtils
import com.jhteck.icebox.utils.FileUtil
import com.jhteck.icebox.utils.ToastUtils
import java.io.File
import java.io.IOException
import java.util.*

/**
 *@Description:(用一句话描述)
 *@author wade
 *@date 2023/8/8 23:33
 */
object FileUtils {
    /**
     * 获取视频文件路径
     */
    private val imagePath = Environment.getExternalStorageDirectory().toString() + "/images_km/register"
    fun getVideoName(): String {
        val videoPath = Environment.getExternalStorageDirectory().toString() + "/CameraX"
        val dir = File(videoPath)
        if (!dir.exists() && !dir.mkdirs()) {
            ToastUtils.shortToast("Trip")
        }
        return videoPath
    }

    /**
     * 获取图片文件路径
     */
    fun getImageFileName(): String {
        val dir = File(imagePath)
        if (!dir.exists() && !dir.mkdirs()) {
            ToastUtils.shortToast("Trip")
        }
        return imagePath
    }

    /**
     * 按时间删除文件，
     * 传入天数
     */
    fun deleteImageByDate(dayNum: Int) {
        val calendar = Calendar.getInstance()
//            calendar.clear()
        calendar.time = Date()
        calendar.add(Calendar.DATE, dayNum)
        val deleteDay = DateUtils.formatDateToString(
            calendar.time,
            DateUtils.format_yyyyMMddhhmmssNew
        )
        //文件夹路径,不包含文件的路径
        val listFiles = FileUtil.listFilesInDir(imagePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listFiles?.stream()?.forEach {
                println(it)
                println(it.name)

                var temp = it.name.split(".")
                println(temp[0])
                val pictureDate =
                    DateUtils.formatStringToDate(temp[0], DateUtils.format_yyyyMMddhhmmssNew)
                println("result=" + pictureDate)
                if (pictureDate.before(calendar.time)) {
                    Log.d("CameraXBasic", "deleteDay=${deleteDay}")
                    deleteFile(it.absolutePath)
                }

//                delete(it.toString())
//                FileUtil.getFileByPath()
            }
        }

        Log.d("CameraXBasic", deleteDay)
    }

    fun deleteFile(path: String) {
        try {
            Log.i("FileDel", "path==${path}")
            FileUtil.deleteFile(path)
        } catch (e: IOException) {
            Log.i("FileDel", "File deleted failed.")
            e.printStackTrace()
        }

    }


}
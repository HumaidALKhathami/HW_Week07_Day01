package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R
import java.lang.IllegalArgumentException

private const val TAG = "BlurWorker"

class BlurWorker(context : Context , workParams: WorkerParameters) :Worker(context,workParams) {
    override fun doWork(): Result {

        val appContext = applicationContext

        val resourceUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("the image is blurred" , appContext)

        sleep()

        return try{

            if (TextUtils.isEmpty(resourceUri)){
                Log.e(TAG,"invalid input uri")
                throw IllegalArgumentException("invalid input uri")
            }

            val resolver = appContext.contentResolver

            val picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))

            val output = blurBitmap(picture, appContext)

            val outputUri = writeBitmapToFile(appContext, output)

//            makeStatusNotification("output is $outputUri", appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        }catch (t : Throwable){
            Log.e(TAG , "Error applying blur")
            Result.failure()
        }
    }
}
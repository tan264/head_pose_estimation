package com.example.head_pose_estimation

import android.graphics.BitmapFactory
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    companion object {
        const val CHANNEL = "processImage"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            CHANNEL
        ).setMethodCallHandler { call, result ->
            if (call.method == "processImage") {
                val imageData: ByteArray? = call.argument("imageData")
                imageData?.let {
                    Log.d("tan264", it.size.toString())
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                }
            } else {
                result.notImplemented()
            }
        }
    }
}

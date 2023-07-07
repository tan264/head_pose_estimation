package com.example.head_pose_estimation

import android.content.Context
import android.view.View
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class CameraViewFactory(view: View) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {

    private var cameraView: View? = null

    init {
        cameraView = view
    }

    override fun create(context: Context?, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return CameraView(context!!, viewId, creationParams, cameraView!!)
    }
}
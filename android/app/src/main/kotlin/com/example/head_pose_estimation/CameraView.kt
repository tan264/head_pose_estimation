package com.example.head_pose_estimation

import android.content.Context
import android.view.View
import io.flutter.plugin.platform.PlatformView

class CameraView(context: Context, id: Int, creationParams: Map<String?, Any?>?, view: View) :
    PlatformView {

    override fun dispose() {

    }

    private var view: View? = null

    override fun getView(): View? {
        return view!!
    }

    init {
        this.view = view
    }
}
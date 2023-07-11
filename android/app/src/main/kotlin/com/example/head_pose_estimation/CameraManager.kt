package com.example.head_pose_estimation

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class CameraManager {

    fun handle(call: MethodCall, result: MethodChannel.Result, controller: CameraViewController) {
        when {
            call.method.equals("startCamera") -> {
                controller.startCamera { initialized ->
                    result.success(initialized)
                }
            }

//            call.method.equals("stopCamera") -> {
//                controller.stopCamera { stopped ->
//                    result.success(stopped)
//                }
//            }
        }
    }
}

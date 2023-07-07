package com.example.head_pose_estimation

import android.graphics.RectF
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.camera.view.PreviewView
import com.example.head_pose_estimation.utils.FaceLandmarkerHelper
import com.example.head_pose_estimation.utils.OvalOverlayView
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import java.util.logging.StreamHandler
import kotlin.math.max

class MainActivity : FlutterActivity(), FaceLandmarkerHelper.LandmarkerListener, EventChannel.StreamHandler {

    private var eventSink: EventChannel.EventSink? = null
    private var stream: EventChannel? = null

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        this.eventSink = events
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
        stream = null
    }

    private lateinit var textView: TextView
    private lateinit var ovalView: OvalOverlayView

    private var hasFrontAngle = false
    private var hasLeftAngle = false
    private var hasRightAngle = false
    private var hasUpAngle = false
    private var hasDownAngle = false

    override fun onError(error: String, errorCode: Int) {
        Log.d("tan264", error)
    }

    override fun onResults(resultBundle: FaceLandmarkerHelper.ResultBundle) {
        if (isInsideTheBox(
                resultBundle.results.faceLandmarks()[0],
                imageWidth = resultBundle.inputImageWidth,
                imageHeight = resultBundle.inputImageHeight,
                ovalView.getOvalRect(),
                ovalView.width,
                viewHeight = ovalView.height
            )
        ) {
            if (hasDownAngle && hasFrontAngle && hasUpAngle && hasRightAngle && hasLeftAngle) {
                runOnUiThread {
                    eventSink?.success(true)
                }
            }
            val yaw = resultBundle.yaw
            val pitch = resultBundle.pitch
            if(!hasFrontAngle) {
                setText("Nhin Thang")
            } else if (!hasRightAngle) {
                setText("Nhin phai")
            } else if (!hasLeftAngle) {
                setText("Nhin trai")
            } else if (!hasUpAngle) {
                setText("Nhin len tren")
            } else if (!hasDownAngle) {
                setText("Nhin xuong duoi")
            }
            if (yaw < -15 && hasRightAngle && !hasLeftAngle) {
                hasLeftAngle = true
            } else if (pitch > 15 && hasLeftAngle && !hasUpAngle) {
                hasUpAngle = true
            } else if (pitch < -10 && hasUpAngle && !hasDownAngle) {
                hasDownAngle = true
            } else if (yaw > 15 && hasFrontAngle && !hasRightAngle) {
                hasRightAngle = true
            } else if (yaw in -8.0..8.0 && pitch in -8.0..8.0 && !hasFrontAngle) {
                hasFrontAngle = true
            }

        } else {
            setText("Khong co mat")
            resetStatus()
        }
    }

    private fun resetStatus() {
        hasFrontAngle = false
        hasRightAngle = false
        hasLeftAngle = false
        hasUpAngle = false
        hasDownAngle = false
    }

    private fun setText(text: String) {
        runOnUiThread {
            textView.text = text
        }
    }

    override fun onEmpty() {
        runOnUiThread {
            textView.setText("Dua mat vao khung")
        }
    }

    fun isInsideTheBox(
        faceLandmarks: List<NormalizedLandmark>,
        imageWidth: Int,
        imageHeight: Int,
        box: RectF,
        viewWidth: Int,
        viewHeight: Int
    ): Boolean {
        val scaleFactor = max(viewWidth * 1f / imageWidth, viewHeight * 1f / imageHeight)

        return box.contains(
            faceLandmarks[234].x() * imageWidth * scaleFactor,
            faceLandmarks[10].y() * imageHeight * scaleFactor,
            faceLandmarks[454].x() * imageWidth * scaleFactor,
            faceLandmarks[200].y() * imageHeight * scaleFactor
        )
    }

    companion object {
        const val CHANNEL = "cameraX"
        const val STREAM = "isDone"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val view: View = View.inflate(context, R.layout.fragment_camera, null)

        flutterEngine.platformViewsController.registry.registerViewFactory(
            "<cameraX>",
            CameraViewFactory(view)
        )

        val cameraChannel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        stream = EventChannel(flutterEngine.dartExecutor.binaryMessenger, STREAM).apply {
            setStreamHandler(this@MainActivity)
        }

        val cameraPreview = view.findViewById<PreviewView>(R.id.viewFinder)
        textView = view.findViewById(R.id.textView)
        ovalView = view.findViewById(R.id.oval_view)

        // https://stackoverflow.com/questions/72358118/unable-to-position-androidview-with-video
        cameraPreview.implementationMode = PreviewView.ImplementationMode.COMPATIBLE

        val cameraController = CameraViewController(context, this, cameraPreview, this)
        val cameraManager = CameraManager()

        cameraChannel.setMethodCallHandler { call, result ->
            cameraManager.handle(
                call,
                result,
                cameraController
            )
        }
    }
}

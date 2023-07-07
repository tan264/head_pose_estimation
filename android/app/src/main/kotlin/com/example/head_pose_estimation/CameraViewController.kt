package com.example.head_pose_estimation

import android.content.Context
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.head_pose_estimation.utils.FaceLandmarkerHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CameraViewController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val cameraPreview: PreviewView,
    private val listener: FaceLandmarkerHelper.LandmarkerListener
) {

    private lateinit var faceLandmarkerHelper: FaceLandmarkerHelper
    private lateinit var cameraExecutor: ExecutorService

    fun startCamera(onInitialize: (result: Boolean?) -> Unit) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraExecutor.execute {
            faceLandmarkerHelper = FaceLandmarkerHelper(context, listener)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(cameraPreview.surfaceProvider)
                }

                val cameraSelector =
                    CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build()

                val imageAnalyzer =
                    ImageAnalysis.Builder().setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setTargetRotation(cameraPreview.display.rotation)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
                        // The analyzer can then be assigned to the instance
                        .also {
                            it.setAnalyzer(
                                cameraExecutor, faceLandmarkerHelper::detectLivestreamFrame
                            )
                        }

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )

                    onInitialize.invoke(true)
                } catch (e: Exception) {
                    Log.e("tan264", "Use case binding failed", e)
                    onInitialize.invoke(false)
                }
            }, ContextCompat.getMainExecutor(context))
            faceLandmarkerHelper.setupFaceLandmarker()
        }


    }

    fun stopCamera(onStop: (result: Boolean?) -> Unit) {
        cameraExecutor.shutdown()
        cameraExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }
}
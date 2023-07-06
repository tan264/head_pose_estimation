import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logger/logger.dart';

const platform = MethodChannel('processImage');
late List<CameraDescription> _cameras;


Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  _cameras = await availableCameras();
  CameraDescription selectedCamera = _cameras.firstWhere(
      (element) => element.lensDirection == CameraLensDirection.back);

  runApp(CameraApp(
    cameraDescription: selectedCamera,
  ));
}

class CameraApp extends StatefulWidget {
  const CameraApp({super.key, required this.cameraDescription});

  final CameraDescription cameraDescription;

  @override
  State<CameraApp> createState() => _CameraAppState();
}

class _CameraAppState extends State<CameraApp> {
  CameraController? _controller;

  get logger => Logger();

  @override
  void initState() {
    super.initState();
    _initializeCameraController(widget.cameraDescription);
  }

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }

  Future<void> _initializeCameraController(
      CameraDescription cameraDescription) async {
    _controller = CameraController(
      cameraDescription,
      ResolutionPreset.medium,
      enableAudio: false,
      imageFormatGroup: ImageFormatGroup.nv21,
    );

    try {
      await _controller?.initialize().then((value) {
        _controller?.startImageStream(_processCameraImage);
      });
    } on CameraException catch (e) {
      logger.d(e.code);
    }

    if (mounted) {
      setState(() {});
    }
  }

  Widget _cameraPreviewWidget() {
    if (_controller == null || !_controller!.value.isInitialized) {
      return Container();
    } else {
      return CameraPreview(_controller!);
    }
  }

  void _processCameraImage(CameraImage image) {
    final imageData = image.planes[0].bytes;
    _sendImageToNative(imageData);
  }

  Future<void> _sendImageToNative(Uint8List imageData) async {
    try {
      final int result = await platform.invokeMethod('processImage', {'imageData': imageData});
      logger.d(result);
    } on PlatformException catch (e) {
      logger.d(e.message);
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: _cameraPreviewWidget(),
    );
  }
}

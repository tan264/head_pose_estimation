import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:logger/logger.dart';
import 'package:permission_handler/permission_handler.dart';

import 'complete_screen.dart';

final _logger = Logger();

class CameraPage extends StatefulWidget {
  const CameraPage({super.key});

  @override
  State createState() => _CameraPageState();
}

class _CameraPageState extends State<CameraPage> {
  static const cameraChannel = MethodChannel('cameraX');
  static const stream = EventChannel('isDone');

  late StreamSubscription _streamSubscription;

  void _startListener() {
    _streamSubscription = stream.receiveBroadcastStream().listen(_listenStream);
  }

  void _cancelListener() {
    _streamSubscription.cancel();
  }

  void _listenStream(value) async {
    if (value is bool && value) {
      _logger.d("done");
      _cancelListener();
      navigateCompleteScreen();
    }
  }

  void navigateCompleteScreen() {
    Navigator.push(
        context, MaterialPageRoute(builder: (_) => const CompleteScreen()));
  }

  Future<void> startCamera() async {
    var status = await Permission.camera.status;
    if (status.isGranted) {
      try {
        bool success = await cameraChannel.invokeMethod("startCamera");
        _startListener();
        if (success && mounted) {
          setState(() {});
        }
      } catch (e) {
        _logger.d(e.toString());
      }
    } else {
      var status = await Permission.camera.request();
      if (status.isGranted) {
        startCamera();
      }
    }
  }

  Future<void> stopCamera() async {
    bool success = await cameraChannel.invokeMethod("stopCamera");
    if (success) {
      _logger.d("stop camera success");
    } else {
      _logger.d("stop camera failed");
    }
  }

  @override
  void initState() {
    startCamera();
    super.initState();
  }

  @override
  void dispose() {
    stopCamera();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    const String viewType = '<cameraX>';
    final Map<String, dynamic> creationParams = <String, dynamic>{};

    return SafeArea(
      child: AndroidView(
        viewType: viewType,
        creationParams: creationParams,
        layoutDirection: TextDirection.ltr,
        creationParamsCodec: const StandardMessageCodec(),
      ),
    );
  }
}

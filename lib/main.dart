import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:head_pose_estimation/complete_screen.dart';
import 'package:logger/logger.dart';
import 'package:permission_handler/permission_handler.dart';

final logger = Logger();

void main() {
  runApp(const MaterialApp(
    debugShowCheckedModeBanner: false,
    home: Scaffold(
      body: SafeArea(child: CameraPage()),
    ),
  ));
}

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

  void _listenStream(value) {
    if (value) {
      Navigator.push(
          context, MaterialPageRoute(builder: (_) => const CompleteScreen()));
    }
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
        logger.d(e.toString());
      }
    } else {
      var status = await Permission.camera.request();
      if (status.isGranted) {
        startCamera();
      }
    }
  }

  Future<void> stopCamera() async {
    try {
      _cancelListener();
      bool success = await cameraChannel.invokeMethod("stopCamera");
      logger.d(success);
      if (success && mounted) {
        setState(() {});
      }
    } catch (e) {
      logger.d(e.toString());
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

    return AndroidView(
      viewType: viewType,
      creationParams: creationParams,
      layoutDirection: TextDirection.ltr,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}

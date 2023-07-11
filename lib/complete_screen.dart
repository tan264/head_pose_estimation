import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class CompleteScreen extends StatelessWidget {
  const CompleteScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        body: Center(
            child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ElevatedButton(
              onPressed: () =>
                  {SystemChannels.platform.invokeMethod('SystemNavigator.pop')},
              child: const Text(
                "Done!",
                style: TextStyle(fontSize: 26),
              ),
            ),
          ],
        )),
      ),
    );
  }
}

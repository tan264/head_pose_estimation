import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/svg.dart';

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
            SvgPicture.asset(
              "done_ring_round_svgrepo_com.svg",
              semanticsLabel: "Face ID",
            ),
            const SizedBox(
              height: 5,
            ),
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

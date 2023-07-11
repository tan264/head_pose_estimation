import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_svg/svg.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

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
              "assets/done_ring_round_svgrepo_com.svg",
              semanticsLabel: "Face ID",
              width: 140,
              height: 140,
            ),
            const SizedBox(
              height: 5,
            ),
            ElevatedButton(
              onPressed: () =>
                  {SystemChannels.platform.invokeMethod('SystemNavigator.pop')},
              child: Text(
                AppLocalizations.of(context)!.done,
                style: const TextStyle(fontSize: 26),
              ),
            ),
          ],
        )),
      ),
    );
  }
}

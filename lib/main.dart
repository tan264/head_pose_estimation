import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'camera_page.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

void main() {
  runApp(const MaterialApp(
    localizationsDelegates: AppLocalizations.localizationsDelegates,
    supportedLocales: AppLocalizations.supportedLocales,
    debugShowCheckedModeBanner: false,
    home: HomePage(),
  ));
}

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              SvgPicture.asset(
                "assets/face_id_seeklogo_com.svg",
                semanticsLabel: "Face ID",
              ),
              const SizedBox(
                height: 5,
              ),
              ElevatedButton(
                  onPressed: () => {
                        Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (_) => const CameraPage()))
                      },
                  child: Text(AppLocalizations.of(context)!.check)),
            ],
          ),
        ),
      ),
    );
  }
}

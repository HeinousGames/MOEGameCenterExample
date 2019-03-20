# MOEGameCenterExample
Barebones example of how to get GameCenter running in your LibGDX game with Intel's Multi OS Engine (MOE).

# How?
Refactored the old RoboVM bindings for GameCenter from here (https://github.com/BlueRiverInteractive/robovm-ios-bindings/tree/master/gamecenter/src/org/robovm/bindings/gamecenter).

# Things to Note
- Removed deprecated GameCenter implementation code as LibGDX MOE games run on iOS 9.3+.
- Utilizes lambdas, so you may have to update to newer versions of Java if your IDE doesn't recognize them.
- Not sure if the method buildUnauthenticatedPlayerError is updated correctly, as my account logged in correctly. Marked as     untested.
- Removed getIOSVersion method as it was outdated for iOS 10+.

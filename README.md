# ClearLineage

ClearLineage is a system theme bringing the absolute finest of transparency effects to your system.

To get an impression of the theme,
click yourself through the
[interactive image gallery](https://programminghoch10.github.io/ClearLineage).

### Prerequisites

This mod currently supports LineageOS 17 - 19.

This mod uses a combo of Magisk-injected system overlays and LSPosed hooks.

### Installation

1. Install LSPosed, reboot and confirm that it's working
1. Install the module ZIP in Magisk
1. Reboot

### Blur

This module sets the system prop 
`ro.surface_flinger.supports_background_blur=1`, 
which enables blur effects on your system.

Blur is enabled by default, but it might cause lags.
You can disable blur by going into the device developer settings 
and disabling the option `Allow window-level blurs`.

### Bugs

With Android 12 changing all the time currently, expect there to be inconsistencies.

Do not complain about lags while [Blur](#blur) is enabled.

You can report bugs, but I can't guarantuee I can fix them, especially if I can't replicate them.

You can report bugs in GitHub issues,
or via Telegram
[`@clearlineage`](https://t.me/clearlineage).

### Development

Development on Linux is very easy:
- Compile and deploy to device:  
  `./compile.sh && ./install-to-device.sh && adb reboot`
- Recover device if crashing on boot:  
  `./recover-device.sh`
- Monitor overlay `android`:  
  `watch adb shell cmd overlay dump com.programminghoch10.clearlineage.android`
- Monitor Logcat of TAG `ClearLineage`:  
  `while (true); do adb logcat -s ClearLineage; done`

### Contributions

You are welcome to contribute, 
screenshots and descriptions of what you changed are necessary, 
and I won't merge stuff I can't check myself.

### Thank me

I really take **a lot** of time optimizing ClearLineage.

I currently do not take donations,
I rather do this for you to be happy.

And for me to be happy,
I'd like to see you happy.

So please do
[message me on Telegram](https://t.me/programminghoch10)
and tell me what you think.

And tell your friends about it!

Also more screenshots of the theme are appreciated,
gotta fill up the gallery somehow.

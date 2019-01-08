# Android Makefile v0.1 (c) 2011 - Daniel Hiepler <daniel@niftylight.de>

APPNAME = Birthdroid
ACTIVITY = BirthdroidActivity

all:
	ant release

debug:
	ant debug

install: sign
	adb install -r bin/$(APPNAME).apk
        
install_debug: debug
	adb install -r bin/$(APPNAME)-debug.apk

clean:
	ant clean

update:
	android update project --name $(APPNAME) --path .

keygen:
	keytool -genkey -v -keystore my.keystore -alias $(APPNAME)_key -keyalg RSA -keysize 4096 -validity 100000

sign: all
	jarsigner -keystore my.keystore bin/$(APPNAME)-release-unsigned.apk $(APPNAME)_key
	@rm -f bin/$(APPNAME).apk 2>/dev/null
	zipalign -v 4 bin/$(APPNAME)-release-unsigned.apk bin/$(APPNAME).apk

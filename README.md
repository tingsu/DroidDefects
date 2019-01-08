# defects4android

App | Package | Version | LOC | Category | Exception Category | Root Cause | Exception Type | Issue | Buggy | Fixed | Comments 
--- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ---
*[Bites](https://github.com/karimhamdanali/bites-android)* | `caldwell.ben.bites` | 1.3 |  | Recipes cookbook | Framework | Parameter Error | NumberFormatException | - | - | - | -
*[Birthdroid](https://github.com/rigid/Birthdroid)* | `com.rigid.birthdroid` | 0.6.3 |  | birthday app | Framework | Parameter Error | NumberFormatException | [issue](https://github.com/rigid/Birthdroid/issues/12) | [buggy](https://github.com/rigid/Birthdroid/commit/6484f28b2fdd70a3f2d9b97c83f19eab6c1fbefd) | [fix](https://github.com/rigid/Birthdroid/commit/d623684b5012ca3787ffd38781ca5f2874d35942) | -
*[Bankdroid](https://github.com/rmack/TwistedHomeManager)* | `com.liato.bankdroid` | 1.9.10.6 |  | Swedish Banking App | Framework | Parameter Error | IllegalArgumentException (Illegal character in query/path) | [issue](https://github.com/liato/android-bankdroid/issues/687) | - | - | -
*[Olam](https://github.com/vishnus/Olam)* | `com.olam` | 1.0 |  | Olam Malayalam Dictionary  | Framework | Parameter Error | SQLiteException (syntax error) | [issue](https://github.com/vishnus/Olam/issues/2) | - | - | -
*[Scribbler](https://github.com/taky/effy)* | `com.gmail.altakey.effy` | 0.1.8 |  | Graphics | Framework | XML Error | FormatFlagsConversionMismatchException | - | - | - | -
*[Nextcloud](https://github.com/nextcloud/android)* | `com.nextcloud.android.beta` | 20160613 |  | Nextcloud Android app | Framework | XML Error | Resources$NotFoundException | [issue](https://github.com/nextcloud/android/issues/31) | - | [fixed](https://github.com/nextcloud/android/commit/aedb03c45d14784d955c2187610ffb4f5a8733bb) | source code compile failed
*[transistor](https://github.com/y20k/transistor)* | `org.y20k.transistor` | 1.1.5 |  | Radio App | Framework | Lifecycle Error | IllegalStateException (Fragment XX not attached to Activity) | [issue](https://github.com/y20k/transistor/issues/21) | [buggy](https://github.com/y20k/transistor/commit/23f44ba40e4e78a6ef777c7e0a7c85bdeaea63c1) | [fix](https://github.com/y20k/transistor/commit/ec0b9237f732277754a3bba96e68831525e9e264) | -
*[android-obd-reader](https://github.com/pires/android-obd-reader)* | `com.github.pires.obd.reader` | 2.0-rc1 |  | OBD-II Reader App | Framework | Lifecycle Error | IllegalArgumentException (Service not registered) | [issue](https://github.com/pires/android-obd-reader/issues/22) | [buggy](https://github.com/pires/android-obd-reader/commit/deb7bd56136ab114443199f203859dab93d20a84) | [fix](https://github.com/pires/android-obd-reader/commit/415e3d8e4a743aa0b7ef48eee5025a00d1e80e87) | source code compile failed
*[opentasks](https://github.com/dmfs/opentasks)* | `org.dmfs.tasks` | 1.1.7 |  | Task App | Framework | Lifecycle Error | IllegalStateException (Can not perform this action after onSaveInstanceState) | [issue](https://github.com/dmfs/opentasks/issues/340) | [buggy]() | [fix]() | [discussion](https://github.com/dmfs/opentasks/pull/255)
*[budget-envelopes](https://github.com/notriddle/budget-envelopes)* | `com.notriddle.budget` | 3.4 |  | Budget Management App | Framework | Lifecycle Error | IllegalStateException (Can not perform this action after onSaveInstanceState) | - | - | - | -
*[NewPipe](https://github.com/TeamNewPipe/NewPipe)* | `org.schabi.newpipe` | 0.7.8 |  | Youtube frontend for Android | Framework | Lifecycle Error | IllegalStateException (Content view not yet created) | [issue](https://github.com/TeamNewPipe/NewPipe/issues/269) | - | - | -
*[WordPress-Android](https://github.com/wordpress-mobile/WordPress-Android)* | `org.wordpress.android` | 3.6 |  | WordPress for Android | Framework | Lifecycle Error | IllegalStateException (Fragment already added) | [issue](https://github.com/wordpress-mobile/WordPress-Android/issues/2265) | - | - | -
*[adsdroid](https://github.com/dnet/adsdroid)* | `hu.vsza.adsdroid` | 1.6 |  | App for alldatasheet.com | Framework | Lifecycle Error | IllegalArgumentException (View not attached to window manager) | - | - | - | -
*[TwistedHomeManager](https://github.com/rmack/TwistedHomeManager)* | `com.twsitedapps.homemanager` | 1.0.1.9 |  | Twisted Home Manager | Framework | UI Update Error | IllegalStateException (ListView & Adapter Update Issue) | [issue](https://github.com/rmack/TwistedHomeManager/issues/1) | - | - | Not easy to reproduce manually
*[Cowsay](https://github.com/rorist/Cowsay-android)* | `com.fixme.cowsay` | 1.3 |  | Development | Framework | UI Update Error | CalledFromWrongThreadException | - | - | - | -
*[Tickmate](https://github.com/lordi/tickmate)* | `de.smasi.tickmate` | 1.2.0 |  | One bit journal | Framework | Index Error | CursorIndexOutOfBoundsException | [issue](https://github.com/lordi/tickmate/issues/38) | [buggy](https://github.com/lordi/tickmate/commit/00486161d89dca9a66164b5705f37853fb66ffa9) | [fixed](https://github.com/lordi/tickmate/commit/ed127c37bf70590374ce3053cd0728120439a723) | -
*[Mitzuli](https://github.com/artetxem/mitzuli)* | `com.mitzuli` | 1.0.7 |  | Reading | Framework | Lifecycle Error | WindowManager$BadToken (unable to add window) | - | - | - | -
*[Anki-Android](https://github.com/ankidroid/Anki-Android)* | `com.ichi2.anki` | 2.8.2beta2 |  | Anki on Android | Framework | Constraint Error | IllegalStateException(Fragment null must be a public static class to be  properly recreated from instance state) | [issue](https://github.com/ankidroid/Anki-Android/issues/4589) | - | [fixed](https://github.com/ankidroid/Anki-Android/pull/4591/commits/5c8a30999eba23661d3e3a64072c64438ebf91a8) | -
*[tripmobile](https://github.com/TripSit/tripmobile)* | `me.tripsit.mobile` | 1.0 |  | Tripsit mobile app | Framework | Constraint Error | RuntimeException (Can't create handler inside thread that has not called Looper.prepare()) | [issue](https://github.com/TripSit/tripmobile/issues/13) | [buggy](https://github.com/TripSit/tripmobile/commit/793893cfc3a61be734283c8ff5505a45d6c6ad39) | [fixed](https://github.com/TripSit/tripmobile/commit/da488e4211b33887985a2339cc3026bb96393207) | -
*[Notepad](https://code.google.com/archive/p/banderlabs/)* | `bander.notepad` | 1.06 |  | Note App | Framework | Database Management Error | IllegalArgumentException (column XX does not exist) | [issue](https://code.google.com/archive/p/banderlabs/issues/23) | - | - | -
*[AppBak](https://github.com/moparisthebest/AppBak)* | `org.moparisthebest.appbak` | 1|  | System | Framework | Hardware | OutOfMempryError | - | - | - | -
*[Addi](https://code.google.com/archive/p/addi/source/default/source)* | `com.addi` | 1.98 |  | Science & Education | Framework | Resource-Not-Found Error | ActivityNotFoundException | - | - | - | -
*[WordPress-Android](https://github.com/wordpress-mobile/WordPress-Android)* | `org.wordpress.android` | 8.2 |  | WordPress for Android | Framework | Other Errors | ClassCastException | [issue](https://github.com/wordpress-mobile/WordPress-Android/issues/6661) | [buggy](https://github.com/wordpress-mobile/WordPress-Android/commit/5c09544b03e3f10cd07dc4b9e243dfeae21fc38a) | [fixed](https://github.com/wordpress-mobile/WordPress-Android/commit/827c0fb647435f49948e6bf770ce404965059037) | [discussion](https://github.com/wordpress-mobile/WordPress-Android/pull/6662)
*[CampFahrplan](https://github.com/tuxmobil/CampFahrplan)* | `nerd.tuxmobil.fahrplan.congress` | 1.32.2 |  | Time | Library | Parameter Error | IllegalArgumentException (unexpected url) | - | - | - | -
*[screenrecorder](https://github.com/vijai1996/screenrecorder)* | `com.orpheusdroid.screenrecorder` | 1.8.4 |  | Screen Recorder | Library | Compatibility Error | IllegalStateException (must call onStart before onStop) | [issue](https://github.com/vijai1996/screenrecorder/issues/32) | - | - | Compatibility issue between the app and the library [countly-sdk-android](https://github.com/Countly/countly-sdk-android) it uses to edit video
*[Password Maker](https://github.com/passwordmaker/android-passwordmaker)* | `org.passwordmaker.android` | 1.1.11 |  | Security | Application | NullPointer Error | NullPointerException | - | - | - | -
*[YalpStore](https://github.com/yeriomin/YalpStore)* | `com.github.yeriomin.yalpstore` | 0.17 |  | Yalp Store  | Application | NullPointer Error | NullPointerException (Attempt to invoke virtual method ... on a null object reference) | [issue](https://github.com/yeriomin/YalpStore/issues/204) | - | [fixed](https://github.com/yeriomin/YalpStore/commit/a36b98672cd0db3d9863f50509b268c273f566d8) | -
*[Zom-Android](https://github.com/zom/Zom-Android)* | `im.zom.messenger` | 15.2.0-RC-3 |  | Chat App | Application | NullPointer Error | NullPointerException | Attempt to read from field ... on a null object reference | [issue](https://github.com/zom/Zom-Android/issues/275) | - | - | - 
*[transistor](https://github.com/y20k/transistor)* | `org.y20k.transistor` | 1.2.3 |  | Radio App | Application | NullPointer Error | NullPointerException (Attempt to invoke virtual method ... on a null object reference) | [issue](https://github.com/y20k/transistor/issues/63) | - | - | -
*[LibreNews](https://github.com/milesmcc/LibreNews-Android)* | `app.librenews.io.librenews` | 1.4 |  | LibreNews client | Application | Index Error | ArrayIndexOutOfBoundsException | [issue](https://github.com/milesmcc/LibreNews-Android/issues/27) | - | - | -
*[LibreNews](https://github.com/milesmcc/LibreNews-Android)* | `app.librenews.io.librenews` | 1.4 |  | LibreNews client | Application | NullPointer Error | NullPointerException (Attempt to invoke virtual method ... on a null object | [issue](https://github.com/milesmcc/LibreNews-Android/issues/23) | - | - | -
*[AnyMemo](https://github.com/helloworld1/AnyMemo)* | `org.liberty.android.fantastischmemo` | 10.10.1 |  | Flashcard learning | Application | NullPointer Error | NullPointerException (Attempt to invoke virtual method ... on a null object | [issue](https://github.com/helloworld1/AnyMemo/issues/440) | - | - | -

Implementation details
----------------------

The directory structure of defects4android is as follows:

    defects4android
       |
       |--- cases:                                Typical exception cases 
            |
            | -- adsdroid
                    |
                    | --- .apk                    The apk file      
                    | ---  exception.txt          The exception trace
                    | ---  README.md              The reproduciable steps
                    | ---  rootcause.txt          The root cause
                    | ---  src                    The app source code
       |
       |--- dataset:                              The benchmark dataset
      

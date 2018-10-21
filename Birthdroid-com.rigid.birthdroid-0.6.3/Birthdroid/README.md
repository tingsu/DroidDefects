BIRTHDROID
==========

Simple, ad free & open android birthday app/widget.

Download latest APK at https://github.com/rigid/Birthdroid/releases/latest


[![Flattr this git repo](http://api.flattr.com/button/flattr-badge-large.png)](https://flattr.com/thing/1345949)



1. Features
===========
* free & open
* no fancy permissions
* low battery usage
* doesn't phone home


2. Compile
==========
Use the provided Makefile wrapper. Available targets are

* make keygen        - generate own keystore
* make update        - update project
* make all           - build release
* make debug         - build debug
* make sign          - sign release
* make install       - push .apk to device using adb
* make install_debug - push debugging .apk to device using adb

e.g. make update && make keygen && make && make sign && make install

You need the appcompat library to compile Birthdroid. You might have to update that path for the update target in the Makefile.


3. Usage
========

 1. Launch the app 
 2. You'll see a list of contacts with upcoming birthdays 
 3. Use the preferences menu to configure various settings
 4. (optional) add a widget to your home-screen that continously shows upcoming birthdays


4. FAQ
======

* How can I force-refresh the birthday list or the widget content?
** Data will be refreshed regularly every 24 hours and after you leave the preferences. To force a refresh, open and close the preferences dialog from the app menu.

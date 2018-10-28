README
=======

Copyright (C) 2011-2012 Takahiro Yoshimura <altakey@gmail.com>

This is another scribbling application for Android platform (2.2 and
up.)  You can use it for quick scribbling in an Android-powered
easy presentation session.  It is also my learning project.


0. HOW TO BUILD
=================

[Fire emulator/device up...]

$ ant clean uninstall install
...
BUILD SUCCESSFUL
Total time: xx seconds


If you have my lil' launcher script installed somewhere in your PATH,
then you can use the 'run' Ant task to quickly run/test it.

It is available at: https://gist.github.com/1223663 .


1. FEATURES
=============

 * Ability to directly scribble on live applications


2. BUGS
=========

 * OpenGL based application can stop when drawing mode is active

 * May lose points if you are moving fast enough

 * Pen color choices are lacking

 * Drop color is not configurable


3. ACKNOWLEDGES
=================

Icon is from the Nuovo theme, is work of SILVESTREHERRERA
(http://www.silvestre.com.ar/?p=5.)

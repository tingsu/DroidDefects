/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.googlecode.tesseract.android;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.Pixa;
import com.googlecode.leptonica.android.ReadFile;

import java.io.File;

/**
 * Java interface for the Tesseract OCR engine. Does not implement all available
 * JNI methods, but does implement enough to be useful. Comments are adapted
 * from original Tesseract source.
 *
 * @author alanv@google.com (Alan Viverette)
 */
public class TessBaseAPI {
    /**
     * Used by the native implementation of the class.
     */
    private int mNativeData;

    static {
        System.loadLibrary("lept");
        System.loadLibrary("tess");

        nativeClassInit();
    }

    public static final class PageSegMode {
        /** Orientation and script detection only. */
        public static final int PSM_OSD_ONLY = 0;

        /**
         * Automatic page segmentation with orientation and script detection.
         * (OSD)
         */
        public static final int PSM_AUTO_OSD = 1;

        /** Automatic page segmentation, but no OSD, or OCR. */
        public static final int PSM_AUTO_ONLY = 2;

        /** Fully automatic page segmentation, but no OSD. */
        public static final int PSM_AUTO = 3;

        /** Assume a single column of text of variable sizes. */
        public static final int PSM_SINGLE_COLUMN = 4;

        /** Assume a single uniform block of vertically aligned text. */
        public static final int PSM_SINGLE_BLOCK_VERT_TEXT = 5;

        /** Assume a single uniform block of text. (Default.) */
        public static final int PSM_SINGLE_BLOCK = 6;

        /** Treat the image as a single text line. */
        public static final int PSM_SINGLE_LINE = 7;

        /** Treat the image as a single word. */
        public static final int PSM_SINGLE_WORD = 8;

        /** Treat the image as a single word in a circle. */
        public static final int PSM_CIRCLE_WORD = 9;

        /** Treat the image as a single character. */
        public static final int PSM_SINGLE_CHAR = 10;

        /** Number of enum entries. */
        public static final int PSM_COUNT = 11;
    }

    /**
     * Elements of the page hierarchy, used in {@link ResultIterator} to provide
     * functions that operate on each level without having to have 5x as many
     * functions.
     * <p>
     * NOTE: At present {@link #RIL_PARA} and {@link #RIL_BLOCK} are equivalent
     * as there is no paragraph internally yet.
     */
    public static final class PageIteratorLevel {
        /** Block of text/image/separator line. */
        public static final int RIL_BLOCK = 0;

        /** Paragraph within a block. */
        public static final int RIL_PARA = 1;

        /** Line within a paragraph. */
        public static final int RIL_TEXTLINE = 2;

        /** Word within a text line. */
        public static final int RIL_WORD = 3;

        /** Symbol/character within a word. */
        public static final int RIL_SYMBOL = 4;
    }

    /** Default accuracy versus speed mode. */
    public static final int AVS_FASTEST = 0;

    /** Slowest and most accurate mode. */
    public static final int AVS_MOST_ACCURATE = 100;

    /** Whitelist of characters to recognize. */
    public static final String VAR_CHAR_WHITELIST = "tessedit_char_whitelist";

    /** Blacklist of characters to not recognize. */
    public static final String VAR_CHAR_BLACKLIST = "tessedit_char_blacklist";

    /** Accuracy versus speed setting. */
    public static final String VAR_ACCURACYVSPEED = "tessedit_accuracyvspeed";

    /**
     * Constructs an instance of TessBaseAPI.
     */
    public TessBaseAPI() {
        nativeConstruct();
    }

    /**
     * Called by the GC to clean up the native data that we set up when we
     * construct the object.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            nativeFinalize();
        } finally {
            super.finalize();
        }
    }

    /**
     * Initializes the Tesseract engine with a specified language model. Returns
     * <code>true</code> on success.
     * <p>
     * Instances are now mostly thread-safe and totally independent, but some
     * global parameters remain. Basically it is safe to use multiple
     * TessBaseAPIs in different threads in parallel, UNLESS you use SetVariable
     * on some of the Params in classify and textord. If you do, then the effect
     * will be to change it for all your instances.
     * <p>
     * The datapath must be the name of the parent directory of tessdata and
     * must end in / . Any name after the last / will be stripped. The language
     * is (usually) an ISO 639-3 string or <code>null</code> will default to
     * eng. It is entirely safe (and eventually will be efficient too) to call
     * Init multiple times on the same instance to change language, or just to
     * reset the classifier.
     * <p>
     * <b>WARNING:</b> On changing languages, all Tesseract parameters are reset
     * back to their default values. (Which may vary between languages.)
     * <p>
     * If you have a rare need to set a Variable that controls initialization
     * for a second call to Init you should explicitly call End() and then use
     * SetVariable before Init. This is only a very rare use case, since there
     * are very few uses that require any parameters to be set before Init.
     *
     * @param datapath the parent directory of tessdata ending in a forward
     *            slash
     * @param language (optional) an ISO 639-3 string representing the language
     * @return <code>true</code> on success
     */
    public boolean init(String datapath, String language) {
        if (datapath == null) {
            throw new IllegalArgumentException("Data path must not be null!");
        }
        if (!datapath.endsWith(File.separator)) {
            datapath += File.separator;
        }

        File tessdata = new File(datapath + "tessdata");
        if (!tessdata.exists() || !tessdata.isDirectory()) {
            throw new IllegalArgumentException("Data path must contain subfolder tessdata!");
        }

        return nativeInit(datapath, language);
    }

    /**
     * Frees up recognition results and any stored image data, without actually
     * freeing any recognition data that would be time-consuming to reload.
     * Afterwards, you must call SetImage or SetRectangle before doing any
     * Recognize or Get* operation.
     */
    public void clear() {
        nativeClear();
    }

    /**
     * Closes down tesseract and free up all memory. End() is equivalent to
     * destructing and reconstructing your TessBaseAPI.
     * <p>
     * Once End() has been used, none of the other API functions may be used
     * other than Init and anything declared above it in the class definition.
     */
    public void end() {
        nativeEnd();
    }

    /**
     * Set the value of an internal "variable" (of either old or new types).
     * Supply the name of the variable and the value as a string, just as you
     * would in a config file.
     * <p>
     * Example:
     * <code>setVariable(VAR_TESSEDIT_CHAR_BLACKLIST, "xyz"); to ignore x, y and z. * setVariable(VAR_BLN_NUMERICMODE, "1"); to set numeric-only mode. * </code>
     * <p>
     * setVariable() may be used before open(), but settings will revert to
     * defaults on close().
     *
     * @param var name of the variable
     * @param value value to set
     * @return false if the name lookup failed
     */
    public boolean setVariable(String var, String value) {
        return nativeSetVariable(var, value);
    }

    /**
     * Sets the page segmentation mode. This controls how much processing the
     * OCR engine will perform before recognizing text.
     *
     * @param mode the page segmentation mode to set
     */
    public void setPageSegMode(int mode) {
        nativeSetPageSegMode(mode);
    }

    /**
     * Sets debug mode. This controls how much information is displayed in the
     * log during recognition.
     *
     * @param enabled <code>true</code> to enable debugging mode
     */
    public void setDebug(boolean enabled) {
        nativeSetDebug(enabled);
    }

    /**
     * Restricts recognition to a sub-rectangle of the image. Call after
     * SetImage. Each SetRectangle clears the recogntion results so multiple
     * rectangles can be recognized with the same image.
     *
     * @param rect the bounding rectangle
     */
    public void setRectangle(Rect rect) {
        setRectangle(rect.left, rect.top, rect.width(), rect.height());
    }

    /**
     * Restricts recognition to a sub-rectangle of the image. Call after
     * SetImage. Each SetRectangle clears the recogntion results so multiple
     * rectangles can be recognized with the same image.
     *
     * @param left the left bound
     * @param top the right bound
     * @param width the width of the bounding box
     * @param height the height of the bounding box
     */
    public void setRectangle(int left, int top, int width, int height) {
        nativeSetRectangle(left, top, width, height);
    }

    /**
     * Provides an image for Tesseract to recognize.
     *
     * @param file absolute path to the image file
     */
    public void setImage(File file) {
        Pix image = ReadFile.readFile(file);

        if (image == null) {
            throw new RuntimeException("Failed to read image file");
        }

        nativeSetImagePix(image.getNativePix());
    }

    /**
     * Provides an image for Tesseract to recognize. Does not copy the image
     * buffer. The source image must persist until after Recognize or
     * GetUTF8Chars is called.
     *
     * @param bmp bitmap representation of the image
     */
    public void setImage(Bitmap bmp) {
        Pix image = ReadFile.readBitmap(bmp);

        if (image == null) {
            throw new RuntimeException("Failed to read bitmap");
        }

        nativeSetImagePix(image.getNativePix());
    }

    /**
     * Provides a Leptonica pix format image for Tesseract to recognize. Clones
     * the pix object. The source image may be destroyed immediately after
     * SetImage is called, but its contents may not be modified.
     *
     * @param image Leptonica pix representation of the image
     */
    public void setImage(Pix image) {
        nativeSetImagePix(image.getNativePix());
    }

    /**
     * Provides an image for Tesseract to recognize. Copies the image buffer.
     * The source image may be destroyed immediately after SetImage is called.
     * SetImage clears all recognition results, and sets the rectangle to the
     * full image, so it may be followed immediately by a GetUTF8Text, and it
     * will automatically perform recognition.
     *
     * @param imagedata byte representation of the image
     * @param width image width
     * @param height image height
     * @param bpp bytes per pixel
     * @param bpl bytes per line
     */
    public void setImage(byte[] imagedata, int width, int height, int bpp, int bpl) {
        nativeSetImageBytes(imagedata, width, height, bpp, bpl);
    }

    /**
     * The recognized text is returned as a String which is coded as UTF8.
     *
     * @return the recognized text
     */
    public String getUTF8Text() {
        // Trim because the text will have extra line breaks at the end
        String text = nativeGetUTF8Text();

        return text.trim();
    }

    public Pixa getRegions() {
        int pixa = nativeGetRegions();

        if (pixa == 0) {
            return null;
        }

        return new Pixa(pixa, 0, 0);
    }

    public Pixa getWords() {
        int pixa = nativeGetWords();

        if (pixa == 0) {
            return null;
        }

        return new Pixa(pixa, 0, 0);
    }

    /**
     * Returns the mean confidence of text recognition.
     *
     * @return the mean confidence
     */
    public int meanConfidence() {
        return nativeMeanConfidence();
    }

    /**
     * Returns all word confidences (between 0 and 100) in an array. The number
     * of confidences should correspond to the number of space-delimited words
     * in GetUTF8Text().
     *
     * @return an array of word confidences (between 0 and 100) for each
     *         space-delimited word returned by GetUTF8Text()
     */
    public int[] wordConfidences() {
        int[] conf = nativeWordConfidences();

        // We shouldn't return null confidences
        if (conf == null) {
            conf = new int[0];
        }

        return conf;
    }

    public ResultIterator getResultIterator() {
        int nativeResultIterator = nativeGetResultIterator();

        if (nativeResultIterator == 0) {
            return null;
        }

        return new ResultIterator(nativeResultIterator);
    }

    // ******************
    // * Native methods *
    // ******************

    /**
     * Initializes static native data. Must be called on object load.
     */
    private static native void nativeClassInit();

    /**
     * Initializes native data. Must be called on object construction.
     */
    private native void nativeConstruct();

    /**
     * Finalizes native data. Must be called on object destruction.
     */
    private native void nativeFinalize();

    private native boolean nativeInit(String datapath, String language);

    private native void nativeClear();

    private native void nativeEnd();

    private native void nativeSetImageBytes(
            byte[] imagedata, int width, int height, int bpp, int bpl);

    private native void nativeSetImagePix(int nativePix);

    private native void nativeSetRectangle(int left, int top, int width, int height);

    private native String nativeGetUTF8Text();

    private native int nativeGetRegions();

    private native int nativeGetWords();

    private native int nativeMeanConfidence();

    private native int[] nativeWordConfidences();

    private native boolean nativeSetVariable(String var, String value);

    private native void nativeSetDebug(boolean debug);

    private native void nativeSetPageSegMode(int mode);

    private native int nativeGetResultIterator();

}

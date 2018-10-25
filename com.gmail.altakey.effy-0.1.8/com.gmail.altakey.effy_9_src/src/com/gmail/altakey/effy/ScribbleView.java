/**
 * Copyright (C) 2011-2012 Takahiro Yoshimura
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Substantial portions of this file is based on Android SDK API demo,
 * originally licensed as:
 *
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.altakey.effy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.view.MotionEvent;

public class ScribbleView extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
	private Paint paint;

    private float x, y;
    private static final float TOUCH_TOLERANCE = 4;

    public ScribbleView(Context c, Paint paint) {
        super(c);
        this.path = new Path();
        this.bitmapPaint = new Paint(Paint.DITHER_FLAG);
		this.paint = paint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00000000);
        canvas.drawBitmap(this.bitmap, 0, 0, this.bitmapPaint);
        canvas.drawPath(this.path, this.paint);
    }

    private void touch_start(float x, float y) {
        this.path.reset();
        this.path.moveTo(x, y);
        this.x = x;
        this.y = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - this.x);
        float dy = Math.abs(y - this.y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            this.path.quadTo(this.x, this.y, (x + this.x)/2, (y + this.y)/2);
            this.x = x;
            this.y = y;
        }
    }
    private void touch_up() {
        this.path.lineTo(this.x, this.y);
        // commit the path to our offscreen
        this.canvas.drawPath(this.path, this.paint);
        // kill this so we don't double draw
        this.path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                this.touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                this.touch_up();
                invalidate();
                break;
        }
        return true;
    }
}

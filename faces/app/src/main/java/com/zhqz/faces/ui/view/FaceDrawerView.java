package com.zhqz.faces.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.zhqz.faces.utils.ELog;

import java.util.List;

/**
 * Created on 2017/12/21 14:36.
 *
 * @author Han Xu
 */
public class FaceDrawerView extends View {

    private static final float FACE_PAINT_STROKE_WIDTH = 10.0F;

    private int mImageWidth = 0;
    private int mImageHeight = 0;

    private Paint mPaint;

    private List<Rect> mFaceRects = null;
    private Canvas mycanvas;

    public FaceDrawerView(Context context) {
        super(context);
    }

    public FaceDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceDrawerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void drawFaces(List<Rect> faceRects, int imageWidth, int imageHeight, int color) {
        mFaceRects = faceRects;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;

        initPaint(color);

        invalidate();
    }

    public void drawNull() {
        mFaceRects = null;
        mImageWidth = 0;
        mImageHeight = 0;
        invalidate();

    }

    Paint getFaceRectPaint() {
        return mPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mycanvas = canvas;
        if (mFaceRects == null || mFaceRects.isEmpty()) {
            return;
        }

        mycanvas.scale(-1, -1, getWidth() / 2, getHeight() / 2);

        for (Rect rect : mFaceRects) {
            Rect faceRectOnView = getViewFaceRect(rect, mImageWidth, mImageHeight, getMeasuredWidth(),
                    getMeasuredHeight());
            mycanvas.drawRect(faceRectOnView, mPaint);
        }
    }

    private Rect getViewFaceRect(Rect faceRect, int imageWidth, int imageHeight,
                                 int viewWidth, int viewHeight) {
        // scale rect.
        float scale = getScaleRatio(imageWidth, imageHeight, viewWidth, viewHeight);
        faceRect.left = (int) (faceRect.left * scale);
        faceRect.top = (int) (faceRect.top * scale);
        faceRect.right = (int) (faceRect.right * scale);
        faceRect.bottom = (int) (faceRect.bottom * scale);

        return faceRect;
    }

    private float getScaleRatio(int imageWidth, int imageHeight, int viewWidth, int viewHeight) {
        if ((float) imageWidth / imageHeight < (float) viewWidth / viewHeight) {
            return (float) viewWidth / imageWidth;
        } else {
            return (float) viewHeight / imageHeight;
        }
    }

    private void initPaint(int color) {
        if (mPaint != null) {
            mPaint.setColor(color);
            return;
        }
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(FACE_PAINT_STROKE_WIDTH);
    }


}
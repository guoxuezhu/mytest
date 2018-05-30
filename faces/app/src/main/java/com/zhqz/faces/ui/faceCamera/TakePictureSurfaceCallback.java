package com.zhqz.faces.ui.faceCamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.ByteArrayOutputStream;

/**
 * Created by jingjingtan on 11/16/17.
 */

public class TakePictureSurfaceCallback implements SurfaceHolder.Callback{

    private static Camera camera;
    Bitmap bit = null;
    //传输五个bitmap数组
    int FACENUM = 10;

    public static int number = 100;//作为计数器用
    private CallBack mCallback;

    public TakePictureSurfaceCallback(CallBack cb) {
        this.mCallback = cb;
    }



    public interface CallBack {
        void image(Bitmap bitmap);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open(1);
            if (camera == null) {
                int cametacount = Camera.getNumberOfCameras();
                camera = Camera.open(cametacount - 1);
            }
            Camera.Parameters params = camera.getParameters();
            //params.setJpegQuality(80);//照片质量
            params.setPictureSize(1280,720);//图片分辨率
            params.setPreviewSize(1280,720);
            //params.setPreviewFrameRate(5);//预览帧率
            camera.setParameters(params);    //参数设置完毕

//            camera.setDisplayOrientation(270);//电路板上班显示的旋转
            camera.setDisplayOrientation(90);//
            /**
             * 设置预显示
             */
//                camera.setPreviewDisplay(surfaceView.getHolder());
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if(number<FACENUM){
                        //判断监听器 开始
                        //有byte数组转为bitmap
                        number = number+1;
                        if(number==10){
                            mCallback.image(byte2bitmap(data,camera));
                        }
                    }

                }
            });
            /**
             * 开启预览
             */
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        doStopCamera();
    }
    /**
     * 停止预览，释放Camera
     */
    public static void doStopCamera() {
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    private Bitmap byte2bitmap(byte[] bytes, Camera camera) {
        Bitmap bitmap = null;

        Camera.Size size = camera.getParameters().getPreviewSize(); // 获取预览大小
        final int w = size.width; // 宽度
        final int h = size.height;
        final YuvImage image = new YuvImage(bytes, ImageFormat.NV21, w, h,
                null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
        if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
            return null;
        }
        byte[] tmp = os.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

        Matrix matrix = new Matrix();
        matrix.setRotate(-90);//手机 //电路板上班显示的旋转
        //matrix.setRotate(90);//班牌
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);

        return bitmap;
    }
}

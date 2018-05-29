package com.zhqz.faces.utils.faceUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    /**
     * 创建图片缩略图对象<br>
     * create the thumbnail for image
     * 
     * @param imagePath
     *            源图片路径<br>
     *            the path of image
     * @param width
     *            缩略图宽度<br>
     *            the width of thumbnail
     * @param height
     *            缩略图高度<br>
     *            the height of thumbnail
     * @return 缩略图对象<br>
     *         the thumbnail object
     */
    public static Bitmap createImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = 0;
        int w = 0;
        h = options.outHeight;
        w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    
    /**
     * 保存图片对象到指定路径<br>
     * save the image object to the specific path
     * 
     * @param savePath
     *            保存路径<br>
     *            the path for saving
     * @param bitmap
     *            待保存的图片对象<br>
     *            the image to save
     * @return 保存结果<br>
     *         save result
     */
    public static boolean saveBitmap(String savePath, Bitmap bitmap) {
        if (null == bitmap)
            return false;
        File f = new File(savePath);
        FileOutputStream out = null;
        try {
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * /** 根据人脸框的位置从图片中抠出对应的人脸<br>
     * crop the face from image according to the face rectangle
     * 
     * @param sourceBitmap
     *            需要处理的原始图片<br>
     *            the source image object
     * @param orginRect
     *            想要抠出来的人脸框位置<br>
     *            the face rectangle to crop
     * @return 抠出来的人脸图<br>
     *         the cropped face image object
     */
    public static Bitmap getCropBitmap(Bitmap sourceBitmap, Rect orginRect) {
        if (sourceBitmap == null || sourceBitmap.isRecycled()) {
            return null;
        }
        Rect rect = getScaleRect(orginRect, sourceBitmap.getWidth(), sourceBitmap.getHeight());
        return Bitmap.createBitmap(sourceBitmap, rect.left, rect.top, rect.width(), rect.height());
    }

    /**
     * 计算抠脸所需的矩形框，根据人脸框往外分别扩展宽度和高度的15%，可以根据需要调整，如果左边框或者上边框重新计算抠图框之后位置小于0，
     * 那么左边框和上边框位置就设为0， 如果右边框和下边框重新计算抠图框之后位置大于原始图片的宽高，那么左边框和下边框位置就设为图片的宽度和高度<br>
     * calculate the rectangle required to crop face, expand 15% of the width
     * and height of face rectangle respectively, you can adjust as you need, if
     * the left or the top of rectangle after calculate is less than 0, then set
     * to 0, if the right or bottom of the rectangle after calculate is large
     * than the with and height of source image, then set the right to width,
     * set the bottom to height
     * 
     * @param rect
     *            人脸矩形框<br>
     *            the face rectangle
     * @param maxW
     *            抠脸所需矩形框限定的最大宽度<br>
     *            the max width of the rectangle for calculating
     * @param maxH
     *            抠脸所需矩形框限定的最大高度<br>
     *            the max height of the rectangle for calculating
     * @return 抠脸所需的新矩形框<br>
     *         the new rectangle
     */
    public static Rect getScaleRect(Rect rect, int maxW, int maxH) {
        Rect resultRect = new Rect();
        int left = (int) (rect.left - ((rect.width() * 15) / 100));
        int right = (int) (rect.right + ((rect.width() * 15) / 100));
        int bottom = (int) (rect.bottom + ((rect.height() * 15) / 100));
        int top = (int) (rect.top - ((rect.height() * 15) / 100));
        resultRect.left = left > 0 ? left : 0;
        resultRect.right = right > maxW ? maxW : right;
        resultRect.bottom = bottom > maxH ? maxH : bottom;
        resultRect.top = top > 0 ? top : 0;
        return resultRect;
    }
    
    /**
     * 根据指定的角度旋转图片<br>
     * rotate the image according to the specified angle
     * 
     * @param srcbitmap
     *            原始图片对象<br>
     *            the source image object
     * @param orientation
     *            旋转角度<br>
     *            the rotated angle
     * @return 旋转之后的图片对象<br>
     *         the image object after rotated
     */
    public static Bitmap adjustPhotoRotation(Bitmap srcbitmap, int orientation) {
        Bitmap rotatebitmap = null;
        Matrix m = new Matrix();
        int width = srcbitmap.getWidth();
        int height = srcbitmap.getHeight();
        // 设置旋转度
        // set the rotated angle
        m.setRotate(orientation); 
        try {
            // 新生成图片
            // create a new bitmap
            rotatebitmap = Bitmap.createBitmap(srcbitmap, 0, 0, width, height, m, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rotatebitmap == null) {
            rotatebitmap = srcbitmap;
        }

        if (srcbitmap != rotatebitmap) {
            srcbitmap.recycle();
        }
        return rotatebitmap;
    }

    /**
     * 根据图片路径获取图片对象<br>
     * get the image object according the path
     * 
     * @param absolutePath
     *            图片绝对路径<br>
     *            the absolute path of image
     * @return 图片对象<br>
     *         the image object
     */
    public static Bitmap getRotatedBitmap(String absolutePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
        if (bitmap == null) {
            return null;
        }
        // 获取图片方向
        // get the orientation of image object
        int orientation = getBitmapDegree(absolutePath);
        // 如果图片方向不等于0，那么对图片做旋转操作
        // rotate the image if orientation is not 0
        if (orientation != 0) {
            bitmap = adjustPhotoRotation(bitmap, orientation);
        }
        return bitmap;
    }

    /**
     * 根据图片路径获取图片的方向信息<br>
     * get the image orientation according the image path
     * 
     * @param path
     *            图片绝对路径<br>
     *            the absolute path of image
     * @return 方向信息<br>
     *         the orientation of the image
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            // get the exif according to the image path
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的方向信息
            //get the orientation according orientation tag
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}

package com.zhqz.faces.utils.faceUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.sensetime.senseid.facepro.jniwrapper.library.DetectResult;
import com.sensetime.senseid.facepro.jniwrapper.library.FaceLibrary;
import com.sensetime.senseid.facepro.jniwrapper.library.SearchResult;
import com.zhqz.faces.data.model.FaceFeature;
import com.zhqz.faces.utils.ELog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装了人脸搜索的相关操作<br>
 * wrapper some operation of face searching
 *
 * @author fenghx
 */
public class FaceSearchManager {
    private Object mStFaceDetector;
    private Object mStFaceVerify;
    private static FaceSearchManager mStFaceSearchManagerProxy;
    private FaceLibrary mLibrary;

    public static synchronized FaceSearchManager getInstance() {
        if (mStFaceSearchManagerProxy == null) {
            mStFaceSearchManagerProxy = new FaceSearchManager();
        }
        return mStFaceSearchManagerProxy;
    }

    private FaceSearchManager() {
    }

    /**
     * 初始化搜索所需要用到的对象<br>
     * initialize the objects for face searching
     *
     * @param context    应用上下文<br>
     *                   the context of application
     * @param verifyPath verify的模型路径<br>
     *                   the verify model path
     * @throws Exception 初始化过程中的异常信息<br>
     *                   the exception during initialization
     */
    public void init(Context context, String verifyPath) throws Exception {
        // sample中选择检测配置为大小脸混合模型检测（ST_DETECT_ANY_FACE），您可以根据不同的场景需要
        // 选择小脸、大脸或者大小脸联合模型
        // we choose ST_DETECT_ANY_FACE for face detecting, you can choose small face model , large face model 
        //or combined model of large face and small face as you need
        // 授权成功，初始化比对所需要的对象
        // authorized success, start to initialize the objects for verify
        try {
            mLibrary = new FaceLibrary();
            String verifyModelFilePath = getModelPath("M_Verify_MIMICG2_Common_3.17.0_v1.model", context);
            mStFaceVerify = mLibrary.verifyCreateHandle(verifyModelFilePath);
            if (mStFaceVerify == null) {
                return;
            }
            mStFaceDetector = mLibrary.createDetector(null, FaceLibrary.ST_DETECT_ENABLE_ALIGN_106);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取图片中的人脸特征信息<br>
     * get the face features in image
     *
     * @param imagePath     图片路径<br>
     *                      the path of image
     * @param thumbnailPath 缩略图路径<br>
     *                      the path of thumbnail
     * @param bitmap        图片对象<br>
     *                      the image object
     * @return 图片中特征信息的列表<br>
     * the feature list of the faces in image
     * @throws Exception 操作过程中的异常信息<br>
     *                   the exception during operation
     */
    public List<FaceFeature> getImageFeatures(String imagePath, String thumbnailPath, Bitmap bitmap) throws Exception {
        Log.i("====getImageFeatures==", "========111111111=====" + imagePath);
        Log.i("====getImageFeatures==", "========22222222=====" + thumbnailPath);
        Log.i("====getImageFeatures==", "========333333333=====" + bitmap);
        if (mStFaceDetector == null || mStFaceVerify == null) {
            return null;
        }
        int orientation = FaceLibrary.ST_FACE_UP;
        // 先进行人脸检测
        // detect the faces first
        if (mStFaceDetector == null) {
            return null;
        }
        byte[] leftImageData = getPixelsBgr(bitmap);
        List<DetectResult> detectResults = mLibrary.detect(mStFaceDetector, leftImageData, FaceLibrary.ST_PIX_FMT_BGR888, bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3, orientation);
        // 如果图片中没有检测到人脸，那么直接返回空
        // if no face detected, return null
        if (detectResults == null || detectResults.isEmpty()) {
            return null;
        }
        // 如果检测到了人脸，那么依次获取人脸特征，并且把特征对象转换成数组形式保存
        // if face detected, get the face feature in order, and change the
        // feature object to byte array to save
        List<FaceFeature> imageFeatures = new ArrayList<>();// 特征提取
        ELog.i("======detectResults.size()==detectResults.size()========" + detectResults.size());
        for (int i = 0; i < detectResults.size(); i++) {
            DetectResult detectResult = detectResults.get(i);
            String feature = mLibrary.verifyGetFeature(mStFaceVerify, leftImageData, FaceLibrary.ST_PIX_FMT_BGR888,
                    bitmap.getWidth(), bitmap.getHeight(), bitmap.getWidth() * 3, detectResult);
            FaceFeature item = new FaceFeature(imagePath, thumbnailPath, feature, detectResult.getRect());
            imageFeatures.add(item);
        }
        return imageFeatures;
    }

    /**
     * 从特征列表中进行人脸搜索，搜索出分数最高的指定数量的特征<br>
     *
     * @param features   整个特征列表<br>
     *                   the whole feature list for searching
     * @param feature    待搜索特征<br>
     *                   the feature to search
     * @param maxCount   想要搜索到的相似人脸的最大数量<br>
     *                   the max count of similar faces you want to search for
     * @param indexArray 用来保存搜索的索引结果的数组，数组的大小需设成maxCount的大小<br>
     *                   the array to set the index of features which is one of
     *                   searching result , the length of array should be maxCount
     * @param scoreArray 用来保存搜索的相似度结果的数组，数组的大小需要设成maxCount的大小<br>
     *                   the array to set the scores of similar faces which is one of
     *                   searching result, the length of array should be maxCount
     * @return 实际搜索到的人脸特征数<br>
     * the actual count of search result
     * @throws Exception 操作失败之后，丢出对应的错误异常<br>
     *                   throws corresponding exception if an exception occurs
     */
    public int searchFaceFromList(String[] features, String feature, int maxCount, int[] indexArray,
                                  float[] scoreArray) throws Exception {
        SearchResult result = null;
        if (mStFaceVerify != null) {
            result = mLibrary.verifySearchFaceFromList(mStFaceVerify, features, features.length, feature, maxCount, feature.length());
            if (result == null) {
                return 0;
            }
            for (int i = 0; i < result.getResultLength(); i++) {
                indexArray[i] = result.getTopIdxs()[i];
                scoreArray[i] = result.getTopScores()[i];
            }
        }
        return result == null ? 0 : result.getResultLength();
    }

    /**
     * 释放搜索相关资源<br>
     * release the resource for searching
     */
    public void releaseHandle() {
        if (mStFaceDetector != null) {
            mLibrary.destroyDetector(mStFaceDetector);
            mStFaceDetector = null;
        }
        if (mStFaceVerify != null) {
            mLibrary.verifyDestroyHandle(mStFaceVerify);
            mStFaceVerify = null;
        }
    }

    /**
     * 转换 Bitmap 到 BGR.
     */
    private byte[] getPixelsBgr(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[(temp.length / 4) * 3]; // Allocate for BGR

        // Copy pixels into place
        for (int i = 0; i < temp.length / 4; i++) {

            pixels[i * 3] = temp[i * 4 + 2];        //B
            pixels[i * 3 + 1] = temp[i * 4 + 1];    //G
            pixels[i * 3 + 2] = temp[i * 4];       //R
        }

        return pixels;
    }

    private String getModelPath(String assetFileName, Context context) {
        String externalFilePath = Environment.getExternalStorageDirectory().getPath() + "/sensetime/" + assetFileName;
        try {
            File file = new File(externalFilePath);
            if (file.exists()) {
                if (!file.delete()) {
                    return null;
                }
            } else {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    return null;
                }
            }
            ELog.i("==========1111111=======" + externalFilePath);
            ELog.i("==========1111111=======" + assetFileName);
            InputStream in = context.getAssets().open(assetFileName);
            OutputStream out = new FileOutputStream(externalFilePath);

            byte[] buf = new byte[1024];
            int len = in.read(buf);
            while (len > 0) {
                out.write(buf, 0, len);
                len = in.read(buf);
            }
            in.close();
            out.close();

            return externalFilePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

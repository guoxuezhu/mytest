package com.zhqz.faces.ui.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sensetime.senseid.facepro.jniwrapper.library.ActiveResult;
import com.sensetime.senseid.facepro.jniwrapper.library.DetectResult;
import com.sensetime.senseid.facepro.jniwrapper.library.FaceLibrary;
import com.zhqz.faces.MvpApplication;
import com.zhqz.faces.R;
import com.zhqz.faces.data.DbDao.FacesDataDao;
import com.zhqz.faces.data.model.SearchResult;
import com.zhqz.faces.ui.addFaces.AddFacesActivity;
import com.zhqz.faces.ui.base.BaseActivity;
import com.zhqz.faces.ui.view.CameraPreviewView;
import com.zhqz.faces.utils.ELog;
import com.zhqz.faces.utils.faceUtil.FaceDBAsyncTask;
import com.zhqz.faces.utils.faceUtil.ResultListener;
import com.zhqz.faces.utils.faceUtil.SearchFaceAsyncTask;
import com.zhqz.faces.utils.faceUtil.SearchResultListener;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements MainMvpView, SearchResultListener {

    @Inject
    MainPresenter mMainPresenter;

    @BindView(R.id.preview)
    CameraPreviewView preview;

    @BindView(R.id.img_result)
    ImageView img_result;

    @BindView(R.id.img_result_ok)
    ImageView img_result_ok;

    @BindView(R.id.img_result_score)
    TextView img_result_score;

    @BindView(R.id.txt_tip)
    TextView txt_tip;

//    @BindView(R.id.sw_save_data)
//    Switch sw_save_data;

    private Camera mCamera = null;
    private static final float THRESHOLD = 0.98F;

    private static final int FACE_ORIENTATION = FaceLibrary.ST_FACE_LEFT;
    private FaceLibrary mLibrary = null;
    private TrackThread mTrackThread = null;
    private LivenessThread mLivenessThread = new LivenessThread();
    private FacesDataDao facesDataDao;
    private Bitmap rotatedRgbBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activityComponent().inject(this);
        mMainPresenter.attachView(this);

        if (Camera.getNumberOfCameras() < 1) {
            txt_tip.setText("未找到相机");
        }

        facesDataDao = MvpApplication.getDaoSession().getFacesDataDao();

    }

    @OnClick(R.id.add_img)
    void add_img() {
        mLivenessThread.exit();
        mTrackThread.exit();
        startActivity(new Intent(MainActivity.this, AddFacesActivity.class));
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();


        try {
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(90);
            preview.setCamera(mCamera);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            txt_tip.setText("相机打开失败：" + ex.getLocalizedMessage());
        }


        mTrackThread = new TrackThread();
        mTrackThread.start();


    }

    @Override
    protected void onPause() {
        clearCameraPreviewCallback();

        mTrackThread.exit();

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        mMainPresenter.detachView();//？
        doStopCamera();
        super.onDestroy();
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void clearCameraPreviewCallback() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
        }
    }

    @Override
    public void onSearchResult(String errorMessage, List<SearchResult> result) {
        ELog.i("==========onSearchResult=====aaaaaaaaaaaaa=========" + result.size());
        ELog.i("==========onSearchResult=====aaaaaaaaaaaaa=========" + result.toString());

        Glide.with(MainActivity.this).load(result.get(0).mImagePath)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .fitCenter()
                .dontAnimate()
                .into(img_result_ok);

        img_result_score.setText("相识度：" + result.get(0).mScore);

    }


    private class TrackThread extends Thread {

        private static final int CACHED_FRAME_COUNT = 4;

        private boolean mIsLicenseInited = false;
        private boolean mIsHandleCreated = false;
        private boolean mIsExit = false;
        private int mCurrentCacheCount = 0;
        private float mFrameScoreCache = 0.0F;

        private Object mTrackHandle = null;
        private Object mSelectorHandle = null;

        private final ImageData mImageData = new ImageData();
        private ImageData mImageCache = null;
        private List<DetectResult> mResultCache = null;

        private void exit() {
            mIsExit = true;
        }

        private void receiveImage(byte[] data, int width, int height, int format, int faceOrientation) {
            synchronized (mImageData) {
                mImageData.clear();
                mImageData.data = Arrays.copyOf(data, data.length);
                mImageData.width = width;
                mImageData.height = height;
                mImageData.format = format;
                mImageData.faceOrientation = faceOrientation;
            }
        }

        @Override
        public void run() {
            // Check license inited.
            if (!mIsLicenseInited) {
                mLibrary = new FaceLibrary();
//                int result = initLicense();
//                if (result != FaceLibrary.ST_OK) {
//                    ELog.i("==========init license fail: " + result + ", " + FaceLibrary.getErrorNameByCode(result));
//                    return;
//                }
                mIsLicenseInited = true;
            }

            // Check hancle created.
            if (!mIsHandleCreated) {
                int result = createTrackHandle();
                if (result != FaceLibrary.ST_OK) {
                    ELog.i("==========create track handle fail: " + result + ", " + FaceLibrary.getErrorNameByCode(result));
                    return;
                }
                result = createSelectorHandle();
                if (result != FaceLibrary.ST_OK) {
                    ELog.i("==========create selector handle fail: " + result + ", " + FaceLibrary.getErrorNameByCode(result));
                    return;
                }
                mIsHandleCreated = true;

                addCameraPreviewCallback();
            }

            while (!mIsExit) {
                if (mImageData.isEmpty()) {
                    continue;
                }
                List<DetectResult> trackResults;
                synchronized (mImageData) {
                    // track.
                    ELog.i("====SenseId======begin to track, at: " + SystemClock.elapsedRealtime());
                    trackResults = mLibrary.track(mTrackHandle, mImageData.data, mImageData.format, mImageData.width, mImageData.height, mImageData.width, mImageData.faceOrientation);// 1 is NV21 stride, so pass mImageData.width.
                    ELog.i("==========SenseId====track: " + trackResults + ", at: " + SystemClock.elapsedRealtime());

                    processTrackResult(mImageData, trackResults);

                    mImageData.clear();
                }

                drawFaceRectOnPreview(trackResults);

                if (mCurrentCacheCount == CACHED_FRAME_COUNT) {
                    showTrackSelectResults();

                    if (mLivenessThread == null) {
                        mLivenessThread = new LivenessThread();
                        mLivenessThread.start();
                    }
                    mLivenessThread.livenessDetect(mImageCache, mResultCache);

                    clearImageCaches();
                }
            }

            mLivenessThread.exit();
            destoryTrackHandle();
            destorySelectorHandle();
        }


        private void showTrackSelectResults() {
            Bitmap bitmap = nv21ToBitmap(mImageCache.data, mImageCache.width, mImageCache.height);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawRect(mResultCache.get(0).getRect(), preview.getFaceRectPaint());
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);
            matrix.postRotate(90);
            rotatedRgbBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    img_result.setImageBitmap(rotatedRgbBitmap);
                    ELog.i("=====检测到==111111111111111111111111111=====" + rotatedRgbBitmap);
                }
            });
        }

        private void drawFaceRectOnPreview(List<DetectResult> trackResults) {
            if (trackResults == null || trackResults.isEmpty()) {
                return;
            }
            final List<Rect> rgbFaceRects = new ArrayList<>();
            for (DetectResult result : trackResults) {
                rgbFaceRects.add(result.getRect());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    preview.drawFaces(fixFaceRectsForScreen(rgbFaceRects), Color.GREEN);
                }
            });
        }

        private void processTrackResult(ImageData imageData, List<DetectResult> trackResults) {
            if (trackResults == null || trackResults.isEmpty()) {
                clearImageCaches();
            } else {
                mCurrentCacheCount++;
                double timestamp = SystemClock.uptimeMillis() / 1000D;// second.
                float currentFrameScore = mLibrary.livenessFrameSelectorDetector(mSelectorHandle, imageData.data,
                        imageData.format, imageData.width, imageData.height, imageData.width, timestamp, imageData.faceOrientation, trackResults.get(0));
                Log.d("SenseId", "frame select score: " + currentFrameScore + ", timestamp: " + timestamp);
                if (mResultCache == null) {
                    cacheTrackResult(imageData, trackResults, currentFrameScore);
                } else {
                    if (currentFrameScore > mFrameScoreCache) {
                        cacheTrackResult(imageData, trackResults, currentFrameScore);
                    }
                }
            }
        }

        private Bitmap nv21ToBitmap(byte[] data, int width, int height) {
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 70, outputStream);
            byte[] jpegData = outputStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
        }

        private void cacheTrackResult(ImageData imageData, List<DetectResult> trackResults, float frameScore) {
            mFrameScoreCache = frameScore;
            mResultCache = new ArrayList<>(trackResults);
            if (mImageCache != null && !mImageCache.isEmpty()) {
                mImageCache.clear();
            }
            mImageCache = imageData.copy();
        }

        private void clearImageCaches() {
            if (mImageCache != null && !mImageCache.isEmpty()) {
                mImageCache.clear();
            }
            mResultCache = null;
            mCurrentCacheCount = 0;
            mLibrary.resetLivenessSelector(mSelectorHandle);
            Log.d("SenseId", "reset frame select");
        }

        private List<Rect> fixFaceRectsForScreen(List<Rect> faceRects) {
            if (faceRects == null || faceRects.isEmpty()) {
                return null;
            }
            List<Rect> fixedRects = new ArrayList<>();
            for (Rect rect : faceRects) {
                Rect fixecRect = fixRectForScreen(rect);
                fixedRects.add(fixecRect);
            }
            return fixedRects;
        }

        private Rect fixRectForScreen(Rect rect) {
            // rotate 90 and mirror.
            return new Rect(rect.top, rect.left, rect.bottom, rect.right);
        }

        private void destoryTrackHandle() {
            // Destroy tracker.
            Log.d("SenseId", "begin to destroyTracker: " + mTrackHandle + ", at: " + SystemClock.elapsedRealtime());
            mLibrary.destroyTracker(mTrackHandle);
            Log.d("SenseId", "destroyTracker, at: " + SystemClock.elapsedRealtime());
        }

        private void destorySelectorHandle() {
            // Destroy selector.
            Log.d("SenseId", "begin to destroyLivenessSelector: " + mSelectorHandle + ", at: " + SystemClock.elapsedRealtime());
            mLibrary.destroyLivenessSelector(mSelectorHandle);
            Log.d("SenseId", "destroyLivenessSelector, at: " + SystemClock.elapsedRealtime());
        }

        private int createSelectorHandle() {
            // Create selector tracker handle.
            String modelPath = getModelPath("M_Liveness_Cnn_half_3.0.0_v1.model");
            Log.d("SenseId", "begin to createLivenessSelector: " + modelPath + " at: " + SystemClock.elapsedRealtime());
            mSelectorHandle = mLibrary.createLivenessSelector(modelPath);
            Log.d("SenseId", "createLivenessSelector: " + mTrackHandle + ", at: " + SystemClock.elapsedRealtime());
            if (mSelectorHandle == null) {
                return FaceLibrary.ST_E_FAIL;
            }

            // Reset selector.
            Log.d("SenseId", "begin to resetLivenessSelector: " + mSelectorHandle + ", at: " + SystemClock.elapsedRealtime());
            mLibrary.resetLivenessSelector(mSelectorHandle);// Reset before first track.
            Log.d("SenseId", "resetLivenessSelector, at: " + SystemClock.elapsedRealtime());

            return FaceLibrary.ST_OK;
        }

        private int createTrackHandle() {
            // Create tracker handle.
            int config = FaceLibrary.ST_DETECT_ENABLE_ALIGN_21 | FaceLibrary.ST_FACE_TRACKING_TWO_THREAD;
            Log.d("SenseId", "begin to createTracker with config: " + config + ", at: " + SystemClock.elapsedRealtime());
            mTrackHandle = mLibrary.createTracker(null, config);// Inner tracker model is no need to pass.
            Log.d("SenseId", "createTracker: " + mTrackHandle + ", at: " + SystemClock.elapsedRealtime());
            if (mTrackHandle == null) {
                return FaceLibrary.ST_E_FAIL;
            }

            // Reset tracker.
            Log.d("SenseId", "begin to resetTracker: " + mTrackHandle + ", at: " + SystemClock.elapsedRealtime());
            mLibrary.resetTracker(mTrackHandle);// Reset before first track.
            Log.d("SenseId", "resetTracker, at: " + SystemClock.elapsedRealtime());

            return FaceLibrary.ST_OK;
        }

        private int initLicense() {
//            // Load offline license.
//            String licenseContent = getStringContent("SENSEID_FACEPRO_68B7F141-90C3-41BE-90DD-10ABB89428D8.lic");
//            Log.d("SenseId", "begin to call addLicense: " + licenseContent + ", at: " + SystemClock.elapsedRealtime());
//            mLibrary = new FaceLibrary();
//            int result = mLibrary.addLicense(licenseContent);
//            Log.d("SenseId", "addLicense: " + result + ", " + FaceLibrary.getErrorNameByCode(result) + ", at: " + SystemClock.elapsedRealtime());
//            return result;

            // Load online license.
            String licenseContent = getStringContent();
            mLibrary = new FaceLibrary();
            Log.d("SenseId", "begin to call getActivationCode: " + licenseContent + ", at: " + SystemClock.elapsedRealtime());
            ActiveResult activationCode = mLibrary.getActivationCode(licenseContent);
            Log.d("SenseId", "getActivationCode: " + activationCode + ", at: " + SystemClock.elapsedRealtime());
            Log.d("SenseId", "begin to call activite: " + activationCode + ", at: " + SystemClock.elapsedRealtime());
            if (activationCode.getActiveCode() == null) {
                return activationCode.getResultCode();
            }

            int result = mLibrary.activite(activationCode.getActiveCode());
            Log.d("SenseId", "activite: " + result + ", " + FaceLibrary.getErrorNameByCode(result) + ", at: " + SystemClock.elapsedRealtime());
            return result;
        }
    }


    private String getStringContent() {
        try {
            StringBuilder buf = new StringBuilder();
            InputStream json = getAssets().open("SENSEID_FACEPRO_FAA58BC6-5330-4CBB-8CF7-1A2274719A1A.lic");
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
                buf.append("\n"); // MUST add \n or license will be invalid.
            }

            in.close();
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String getModelPath(String assetFileName) {
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
            InputStream in = MainActivity.this.getApplicationContext().getAssets().open(assetFileName);
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


    private void addCameraPreviewCallback() {
        if (mCamera != null) {
            final Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(0, info);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    mTrackThread.receiveImage(data, camera.getParameters().getPreviewSize().width,
                            camera.getParameters().getPreviewSize().height, convertImageFormatToPixelFormat(
                                    camera.getParameters().getPreviewFormat()), FACE_ORIENTATION);
                }
            });
        }
    }

    private int convertImageFormatToPixelFormat(int imageFormat) {
        switch (imageFormat) {
            case ImageFormat.NV21:
                return FaceLibrary.ST_PIX_FMT_NV21;
            case ImageFormat.RGB_565:
                return FaceLibrary.ST_PIX_FMT_BGR888;
            default:
                return -1;
        }
    }

    private class LivenessThread extends Thread {

        private boolean mIsHandleCreated = false;
        private boolean mIsExit = false;

        private Object mHandle = null;

        private final ImageData mImageData = new ImageData();
        private final List<DetectResult> mTrackResult = new ArrayList<>();

        private void livenessDetect(ImageData imageCache, List<DetectResult> resultCache) {
            synchronized (mImageData) {
                mImageData.clear();
                mImageData.data = Arrays.copyOf(imageCache.data, imageCache.data.length);
                mImageData.width = imageCache.width;
                mImageData.height = imageCache.height;
                mImageData.format = imageCache.format;
                mImageData.faceOrientation = imageCache.faceOrientation;
            }
            synchronized (mTrackResult) {
                mTrackResult.clear();
                mTrackResult.addAll(resultCache);
            }
        }

        private void exit() {
            mIsExit = true;
        }

        @Override
        public void run() {
            ELog.i("=====检测到==1111111=====");
            // Check hancle created.
            if (!mIsHandleCreated) {
                int result = createHandle();
                if (result != FaceLibrary.ST_OK) {
                    ELog.i("====检测到==create liveness handle fail: " + result + ", " + FaceLibrary.getErrorNameByCode(result));
                    return;
                }
                mIsHandleCreated = true;
            }

            while (!mIsExit) {
                if (mImageData.isEmpty() || mTrackResult.isEmpty()) {
                    continue;
                }
                float hacknessScore;
                synchronized (mImageData) {
                    synchronized (mTrackResult) {
                        // Liveness detect.
                        Log.d("SenseId", "==检测到=begin to singlelivenessDetect, at: " + SystemClock.elapsedRealtime());
                        hacknessScore = mLibrary.singlelivenessDetect(mHandle, mImageData.data, mImageData.format, mImageData.width,
                                mImageData.height, mImageData.width, mImageData.faceOrientation, mTrackResult.get(0));// 1 is NV21 stride, so pass mImageData.width. Use first detect result.
//                        if (sw_save_data.isChecked()) {
//                            saveLivenessInputData(hacknessScore, mImageData, mTrackResult.get(0));
//                        }
                        Log.d("SenseId", "singlelivenessDetect: " + hacknessScore + ", at: " + SystemClock.elapsedRealtime());
                        mTrackResult.clear();
                        ELog.i("=====检测到==ttttt=====");
                    }
                    mImageData.clear();
                }

                String resultText = "活体检测失败";
                if (hacknessScore > 0.0F) {
                    resultText = hacknessScore < THRESHOLD ? "检测到真人" : "检测到 Hack";
                    if (hacknessScore < THRESHOLD) {
                        // rotatedRgbBitmap
                        detectFace(rotatedRgbBitmap);

                    }

                }
                ELog.i("=====检测到==22222222222222222222222=====" + resultText);

            }

            destoryHandle();
        }

        private void saveLivenessInputData(float hacknessScore, ImageData imageData, DetectResult detectResult) {
            if (imageData.isEmpty()) {
                return;
            }

            long timestamp = SystemClock.elapsedRealtime();
            String folderPath = Environment.getExternalStorageDirectory().getPath() + "/sensetime/results/" + (hacknessScore < THRESHOLD ? "real_" : "hack_") + timestamp + "/";

            // Save nv21 image.
            String rgbImageFilePath = folderPath + imageData.width + "x" + imageData.height + ".nv21";
            saveDataToFile(imageData.data, rgbImageFilePath);

            // Save jpg image.
            rgbImageFilePath = rgbImageFilePath.replaceAll("nv21", "jpg");
            saveNV21ToFile(imageData.data, imageData.width, imageData.height, rgbImageFilePath);

            // Save input data txt.
            String builder = "hackness score: " + hacknessScore + "\r\n" + "input info: " + detectResult;
            saveDataToFile(builder.getBytes(), folderPath + "info.txt");

            File file = new File(folderPath + "info.txt");
            if (!file.exists()) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "文件存储失败，请检查存储空间。", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        private void saveDataToFile(byte[] data, String filePath) {
            if (data == null || data.length < 1) {
                return;
            }
            try {
                File file = new File(filePath);
                if (file.exists()) {
                    if (!file.delete()) {
                        return;
                    }
                    if (!file.createNewFile()) {
                        return;
                    }
                } else {
                    if (file.getParentFile().exists()) {
                        if (!file.createNewFile()) {
                            return;
                        }
                    } else {
                        if (file.getParentFile().mkdirs()) {
                            if (!file.createNewFile()) {
                                return;
                            }
                        }
                    }
                }
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveNV21ToFile(byte[] data, int width, int height, String filePath) {
            Bitmap bitmap = nv21ToBitmap(data, width, height);
            saveBitmapToFile(bitmap, filePath);
            bitmap.recycle();
        }

        private void saveBitmapToFile(Bitmap bitmap, String filePath) {
            if (bitmap == null || TextUtils.isEmpty(filePath)) {
                return;
            }
            try {
                File file = new File(filePath);
                if (!file.exists() && file.getParentFile().mkdirs() && !file.createNewFile()) {
                    return;
                }
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Bitmap nv21ToBitmap(byte[] data, int width, int height) {
            YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 70, outputStream);
            byte[] jpegData = outputStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            return BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
        }

        private void destoryHandle() {
            // Destroy liveness handle.
            Log.d("SenseId", "begin to destroySingleliveness: " + mHandle + ", at: " + SystemClock.elapsedRealtime());
            mLibrary.destroySingleliveness(mHandle);
            Log.d("SenseId", "destroySingleliveness, at: " + SystemClock.elapsedRealtime());
        }

        private int createHandle() {
            // Create liveness handle.
            String livenessModelPath = getModelPath("M_Liveness_Antispoofing_General_6.0.36.model");
            Log.d("SenseId", "begin to createSingleliveness: " + livenessModelPath + ", at: " + SystemClock.elapsedRealtime());
            mHandle = mLibrary.createSingleliveness(livenessModelPath, 0);// 0 for native param, from yangliang.
            Log.d("SenseId", "createSingleliveness: " + mHandle + ", at: " + SystemClock.elapsedRealtime());
            if (mHandle == null) {
                return FaceLibrary.ST_E_FAIL;
            }

            return FaceLibrary.ST_OK;
        }
    }

    private void detectFace(Bitmap rotatedRgbBitmap) {
        SearchFaceAsyncTask task = new SearchFaceAsyncTask(MainActivity.this, rotatedRgbBitmap, facesDataDao.loadAll(), this);
        task.execute();
    }


    private class ImageData {

        private byte[] data;
        private int width;
        private int height;
        private int format;
        private int faceOrientation;

        @Override
        public String toString() {
            return "ImageData{" +
                    "data=" + Arrays.toString(data) +
                    ", width=" + width +
                    ", height=" + height +
                    ", format=" + format +
                    ", faceOrientation=" + faceOrientation +
                    '}';
        }

        ImageData copy() {
            ImageData copyData = new ImageData();
            copyData.data = Arrays.copyOf(data, data.length);
            copyData.width = width;
            copyData.height = height;
            copyData.format = format;
            copyData.faceOrientation = faceOrientation;
            return copyData;
        }

        void clear() {
            data = null;
            width = 0;
            height = 0;
            format = 0;
            faceOrientation = 0;
        }

        boolean isEmpty() {
            return data == null;
        }
    }


}

//package com.zhqz.faces.utils.faceUtil;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.zhqz.faces.MvpApplication;
//import com.zhqz.faces.data.DbDao.FacesDataDao;
//import com.zhqz.faces.data.model.FaceFeature;
//import com.zhqz.faces.data.model.FacesData;
//import com.zhqz.faces.utils.ELog;
//
//import java.util.List;
//
///**
// * 创建人脸特征库的异步任务<br>
// * the async task for creating face database
// *
// * @author fenghx
// */
//public class FaceDBAsyncTask extends AsyncTask<Void, Integer, Integer> {
//    private List<String> mList = null;
//    private Context mContext = null;
//    private ResultListener mListener = null;
//    private String mErrorMessage = null;
//    private FacesDataDao facesDataDao;
//
//    /**
//     * 初始化构造函数，初始化用来构建人脸特征库的图片集<br>
//     * constructor initializes the image list which is used to build face
//     * feature database
//     *
//     * @param context   应用上下文<br>
//     *                  the context of application or activity
//     * @param imagelist 图片信息列表<br>
//     *                  the list of image item
//     * @param listener  创建人脸特征库的结果监听<br>
//     *                  the listener for the result of face feature database
//     */
//    public FaceDBAsyncTask(Context context, List<String> imagelist, ResultListener listener) {
//        mContext = context;
//        mList = imagelist;
//        mListener = listener;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected Integer doInBackground(Void... params) {
//        Integer features = 0;
//        try {
//            // 获取所有的人脸特征 个数
//            // get all the face features
//            features = getFeature();
//            // 如果特征列表不等于空并且尺寸不等于0，把特征插入到数据库中
//            // if the feature list is not null and the size of the list is not
//            // 0, insert the features to database
////            if (features != null && features.size() != 0) {
////                FaceSearchDBManager.getInstance(mContext).insertMultiFeatureInfos(features);
////            }
//        } catch (Exception e) {
//            mErrorMessage = e.getLocalizedMessage();
//        }
//        return features;
//    }
//
//    @Override
//    protected void onPostExecute(Integer result) {
//        super.onPostExecute(result);
//        if (mListener != null) {
//            // 有错误给监听发送错误信息，没有错误，则发送特征列表信息
//            // send error message if got message during operation, send feature
//            // list if success
//            if (mErrorMessage != null) {
//                mListener.onFailed(mErrorMessage);
//            } else {
//                mListener.onSuccess(result);
//            }
//        }
//    }
//
//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
//    }
//
//    public int getFeature() throws Exception {
//        if (mList == null || mList.size() == 0) {
//            return 0;
//        }
//
//        Bitmap bitmap = null;
//        facesDataDao = MvpApplication.getDaoSession().getFacesDataDao();
//        facesDataDao.deleteAll();
//
//        List<FaceFeature> features = null;
//
//        // 依次获取图片中的人脸特征信息
//        // get the face features in the picture in order
//        for (int i = 0; i < mList.size(); i++) {
//            bitmap = BitmapUtil.getRotatedBitmap(mList.get(i));
//            if (bitmap == null) {
//                continue;
//            }
//            features = FaceSearchManager.getInstance().getImageFeatures(mList.get(i), null, bitmap);
//            for (int j = 0; j < features.size(); j++) {
//                facesDataDao.insert(new FacesData(features.get(j).mImagePath, features.get(j).mThumbnailPath, features.get(j).mFeatureIndex,
//                        features.get(j).mByteFeature, features.get(j).mFaceRect, features.get(j).mFeature));
//            }
//        }
//        Log.i("=========", "=======facesDataDao.loadAll()===========" + facesDataDao.loadAll().toString());
//        return facesDataDao.loadAll().size();
//    }
//}

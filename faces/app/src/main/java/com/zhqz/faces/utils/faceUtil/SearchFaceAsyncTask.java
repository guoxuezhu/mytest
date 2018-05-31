package com.zhqz.faces.utils.faceUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


import com.zhqz.faces.R;
import com.zhqz.faces.data.model.FaceFeature;
import com.zhqz.faces.data.model.FacesData;
import com.zhqz.faces.data.model.SearchResult;
import com.zhqz.faces.ui.main.MainActivity;
import com.zhqz.faces.utils.ELog;

import java.util.ArrayList;
import java.util.List;

/**
 * 从特征列表中搜索的任务<br>
 * search from list task
 *
 * @author fenghx
 */
public class SearchFaceAsyncTask extends AsyncTask<Void, Void, ArrayList<SearchResult>> {
    private Bitmap rotatedRgbBitmap = null;
    private Context mContext = null;
    private SearchResultListener mListener = null;
    private String mErrorMessage = null;
    private List<FacesData> mFeatureInfoList = null;

    /**
     * 初始化构造函数，初始化待搜索图片路径、特征列表和监听器<br>
     * constructor initializes, initialize the image path for searching, the
     * feature list and the listener
     *
     * @param context  应用上下文<br>
     *                 the context of application or activity
     * @param bitmap   待搜索图片路径<br>
     *                 the path of image for searching
     * @param features 特征列表<br>
     *                 the feature list for searching
     * @param listener 搜索结果监听器<br>
     *                 the listener for the result of face searching
     */
    public SearchFaceAsyncTask(Context context, Bitmap bitmap, List<FacesData> features, SearchResultListener listener) {
        mContext = context;
        rotatedRgbBitmap = bitmap;
        mFeatureInfoList = features;
        mListener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected ArrayList<SearchResult> doInBackground(Void... params) {
        if (mFeatureInfoList == null || mFeatureInfoList.size() == 0)
            return null;
        ArrayList<SearchResult> results = new ArrayList<>();
        List<FaceFeature> searchfeatures = null;
        int actualCount = 0;
        int indexArray[] = new int[Constants.MAX_COUNT];
        float scoreArray[] = new float[Constants.MAX_COUNT];
        String[] featurelist = new String[mFeatureInfoList.size()];
        for (int i = 0; i < mFeatureInfoList.size(); i++) {
            featurelist[i] = mFeatureInfoList.get(i).mFeature;
        }
        try {
            // 获取待搜索图片中的人脸特征
            // get the face features in the image for searching
            searchfeatures = FaceSearchManager.getInstance().getImageFeatures(null, null, rotatedRgbBitmap);
            // 如果特征列表不为空，并且特征数不等于0，那么选择第一个特征来进行搜索
            // choose the first feature to search if the feature list is not
            // null and the size of list is not 0
            if (searchfeatures != null && searchfeatures.size() != 0) {
                String searchFeature = searchfeatures.get(0).mFeature;
                actualCount = FaceSearchManager.getInstance().searchFaceFromList(featurelist, searchFeature,
                        Constants.MAX_COUNT, indexArray, scoreArray);
            } else {
                mErrorMessage = mContext.getString(R.string.no_face_feature);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrorMessage = e.getLocalizedMessage();
        }
        // 处理搜索结果，最多返回三个分数最高的特征相关信息
        // process the result, at most return three features information with
        // the highest scores
        for (int i = 0; i < actualCount; i++) {
            // indexArray is from 1-n, not 0-n-1
            results.add(new SearchResult(scoreArray[i], mFeatureInfoList.get(indexArray[i] - 1).mImagePath,
                    mFeatureInfoList.get(indexArray[i] - 1).name, mFeatureInfoList.get(indexArray[i] - 1).sex));
        }
        ELog.i("==11====detectResults.size()==111111111111=======" + results.toString());
        ELog.i("===22===detectResults.size()==222222222222=======" + results.size());
        return results;
    }

    protected void onPostExecute(ArrayList<SearchResult> result) {
        super.onPostExecute(result);

        if (mListener != null) {
            mListener.onSearchResult(mErrorMessage, result);
        }
    }

    ;
}

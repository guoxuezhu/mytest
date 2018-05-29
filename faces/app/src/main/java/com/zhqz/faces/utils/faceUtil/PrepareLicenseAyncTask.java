package com.zhqz.faces.utils.faceUtil;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.sensetime.senseid.facepro.jniwrapper.library.ActiveResult;
import com.sensetime.senseid.facepro.jniwrapper.library.FaceLibrary;
import com.zhqz.faces.R;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * license初始化线程<br>
 * task for license initialize
 *
 * @author fenghx
 */
public class PrepareLicenseAyncTask extends AsyncTask<Void, Void, String> {
    private Context mContext = null;
    private LicenseResultListener mListener = null;

    public PrepareLicenseAyncTask(Context context, LicenseResultListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        String errorMessage = prepareLicense();
        return errorMessage;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (mListener != null) {
            if (result != null) {
                mListener.onLicenseInitFailed(result);
            } else {
                mListener.onLicenseInitSuccess();
            }
        }
    }

    /**
     * 在调用其他sdk的api之前需要先初始化license<br>
     * init license before using other sdk api
     *
     * @return 初始化license的错误码<br>
     * the error message
     */
    private String prepareLicense() {
        String licensePath = null;
        String errorMessage = null;
        try {
            licensePath = LicenseUtils.copyLicenseFile(mContext);
            String licenseStr = LicenseUtils.readLicenseFromAssets(mContext);
            /*此接口只支持非在线激活类型的子证书，比如芯片授权证书
            int rst = StFaceLicense.initLicense(licenseStr);
            * 
            */
            //在线激活授权
            try {
                // Load online license.
                FaceLibrary library = new FaceLibrary();
                Log.d("SenseId", "begin to call getActivationCode: " + licenseStr + ", at: " + SystemClock.elapsedRealtime());
                ActiveResult activationCode = library.getActivationCode(licenseStr);
                Log.d("SenseId", "getActivationCode: " + activationCode + ", at: " + SystemClock.elapsedRealtime());
                Log.d("SenseId", "begin to call activite: " + activationCode + ", at: " + SystemClock.elapsedRealtime());
                if (activationCode.getActiveCode() == null) {
                    return "get activation code error: " + activationCode.getResultCode();
                }
                int result = library.activite(activationCode.getActiveCode());
                Log.d("SenseId", "activite: " + result + ", " + FaceLibrary.getErrorNameByCode(result) + ", at: " + SystemClock.elapsedRealtime());

                if (result != 0) {
                    errorMessage = mContext.getString(R.string.license_error_hint)
                            + String.format(mContext.getString(R.string.error_code_hint), result)
                            + mContext.getString(R.string.error_message_hint);
                }
            } catch (Exception e) {
                errorMessage = mContext.getString(R.string.license_error_hint)
                        + mContext.getString(R.string.error_message_hint);
            }
        } catch (FileNotFoundException ex) {
            if (licensePath == null) {
                errorMessage = mContext.getString(R.string.no_licensefile_hint);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = mContext.getString(R.string.file_error_hint);
            Log.d("SenseId", "file error 1");
        }
        return errorMessage;
    }
}

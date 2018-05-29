package com.zhqz.faces.utils.faceUtil;

public interface LicenseResultListener {
    public void onLicenseInitSuccess();

    public void onLicenseInitFailed(String errorMessage);
}

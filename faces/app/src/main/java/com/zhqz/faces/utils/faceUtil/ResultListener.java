package com.zhqz.faces.utils.faceUtil;


public interface ResultListener {
    public void onSuccess(Integer result);

    public void onFailed(String errorMessage);
}

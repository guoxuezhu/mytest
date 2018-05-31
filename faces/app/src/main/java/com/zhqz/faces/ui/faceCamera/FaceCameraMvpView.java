package com.zhqz.faces.ui.faceCamera;

import com.zhqz.faces.ui.base.MvpView;

public interface FaceCameraMvpView extends MvpView {
    void updataFaceOK();

    void updataFaceNO(String errorMsg);
}

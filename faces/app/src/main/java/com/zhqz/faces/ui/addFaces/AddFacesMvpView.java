package com.zhqz.faces.ui.addFaces;


import com.zhqz.faces.data.model.FaceUser;
import com.zhqz.faces.ui.base.MvpView;

import java.util.List;

public interface AddFacesMvpView extends MvpView {
    void showFaceUsers(List<FaceUser> data);

    void showErrorMsg(String errorMsg);
}

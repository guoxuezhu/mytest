package com.zhqz.faces.ui.addFaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhqz.faces.R;
import com.zhqz.faces.data.model.FaceUser;
import com.zhqz.faces.ui.adapter.FaceUserAdapter;
import com.zhqz.faces.ui.base.BaseActivity;
import com.zhqz.faces.ui.faceCamera.FaceCameraActivity;
import com.zhqz.faces.ui.main.MainActivity;
import com.zhqz.faces.utils.ELog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddFacesActivity extends BaseActivity implements AddFacesMvpView, FaceUserAdapter.CallBack {

    @Inject
    AddFacesPresenter mAddFacesPresenter;

    @BindView(R.id.face_recyclerView)
    RecyclerView face_recyclerView;

    private FaceUserAdapter faceUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faces);
        ButterKnife.bind(this);
        activityComponent().inject(this);
        mAddFacesPresenter.attachView(this);


        face_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        face_recyclerView.setHasFixedSize(true);
        faceUserAdapter = new FaceUserAdapter(this, null, this);
        face_recyclerView.setAdapter(faceUserAdapter);

        mAddFacesPresenter.getFaces();

    }


    @Override
    public void showFaceUsers(List<FaceUser> data) {
        faceUserAdapter.setFaceUserData(data);
    }


    @Override
    public void showErrorMsg(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFaceUserItemClicked(FaceUser faceUser) {
        ELog.i("========onFaceUserItemClicked========" + faceUser.toString());
        Intent intent = new Intent(AddFacesActivity.this, FaceCameraActivity.class);
        intent.putExtra("faceUserId", faceUser.id);
        intent.putExtra("faceUserName", faceUser.name);
        intent.putExtra("faceUserSex", faceUser.sex);
        startActivity(intent);
        finish();
    }


    @OnClick(R.id.info_back_img)
    void info_back_img() {
        addtiaozhuan();
    }

    private void addtiaozhuan() {
        startActivity(new Intent(AddFacesActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        addtiaozhuan();
    }

    @Override
    protected void onDestroy() {
        mAddFacesPresenter.detachView();//？
        super.onDestroy();
    }


}

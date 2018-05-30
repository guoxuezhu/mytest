package com.zhqz.faces.ui.addFaces;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.zhqz.faces.R;
import com.zhqz.faces.data.model.FaceUser;
import com.zhqz.faces.ui.adapter.FaceUserAdapter;
import com.zhqz.faces.ui.base.BaseActivity;
import com.zhqz.faces.utils.ELog;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddFacesActivity extends BaseActivity implements AddFacesMvpView,FaceUserAdapter.CallBack{

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

        mAddFacesPresenter.getFaces("0372956048");


    }


    @Override
    public void showFaceUsers(List<FaceUser> data) {
        faceUserAdapter.setFaceUserData(data);
    }


    @Override
    public void onFaceUserItemClicked(FaceUser faceUser) {
        ELog.i("========onFaceUserItemClicked========" + faceUser.toString());

    }

    @Override
    protected void onDestroy() {
        mAddFacesPresenter.detachView();//？
        super.onDestroy();
    }


}

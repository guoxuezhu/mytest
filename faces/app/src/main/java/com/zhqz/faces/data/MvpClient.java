package com.zhqz.faces.data;


import android.text.TextUtils;

import com.google.gson.Gson;
import com.zhqz.faces.MvpApplication;
import com.zhqz.faces.data.model.FaceUser;
import com.zhqz.faces.data.model.School;
import com.zhqz.faces.data.model.User;
import com.zhqz.faces.data.remote.MvpService;
import com.zhqz.faces.exception.ClientRuntimeException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


@Singleton
public class MvpClient {


    @SuppressWarnings("unchecked")
    private static ObservableTransformer schedulersTransformer = new ObservableTransformer() {
        @Override
        public ObservableSource apply(@NonNull Observable upstream) {
            return ((Observable<HttpResult>) upstream).subscribeOn(Schedulers.io())
                    .timeout(30, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(httpResult -> {
                        if (!httpResult.code.equals("200")) {
                            throw Exceptions.propagate(new ClientRuntimeException(httpResult.code,
                                    httpResult.msg));

                        }
                    });
        }
    };

    private MvpService mvpService;

    @Inject
    public MvpClient(MvpService mMvpService) {
        this.mvpService = mMvpService;
    }

    @SuppressWarnings("unchecked")
    private static <T> ObservableTransformer<T, T> applySchedulers() {
        return (ObservableTransformer<T, T>) schedulersTransformer;
    }


    public void setLoginUser(User user) {
        Gson gson = new Gson();
        String str = gson.toJson(user, User.class);
        MvpApplication.prefs.setUserString(str);
        MvpService.Factory.setUser(user);
    }

    public boolean loadUserIfAvailble() {
        String str = MvpApplication.prefs.getUserString();
        Gson gson = new Gson();
        User u = TextUtils.isEmpty(str) ? null : gson.fromJson(str, User.class);
        if (null != u && u.isValid()) {
            setLoginUser(u);
            return true;
        }
        return false;
    }


    public Observable<HttpResult<List<School>>> schoolList() {
        return mvpService
                .schoolList()
                .compose(this.<HttpResult<List<School>>>applySchedulers());
    }

    public Observable<HttpResult<List<FaceUser>>> getfaceUsers() {
        return mvpService
                .getfaces()
                .compose(this.<HttpResult<List<FaceUser>>>applySchedulers());
    }

    public Observable<HttpResult> updataface(int faceUserId, String path) {
        File file = new File(path);
        //组装partMap对象
        Map<String, RequestBody> partMap = new HashMap<>();

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        partMap.put("files\"; filename=\"" + file.getName() + "", fileBody);

        return mvpService
                .updataFace(faceUserId,partMap)
                .compose(this.<HttpResult>applySchedulers());
    }
}

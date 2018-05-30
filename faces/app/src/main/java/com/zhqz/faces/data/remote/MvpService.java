package com.zhqz.faces.data.remote;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zhqz.faces.BuildConfig;
import com.zhqz.faces.data.HttpResult;
import com.zhqz.faces.data.model.FaceUser;
import com.zhqz.faces.data.model.School;
import com.zhqz.faces.data.model.User;
import com.zhqz.faces.utils.Coder;
import com.zhqz.faces.utils.ELog;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;


public interface MvpService {

    /**
     * www.lark.ink
     * 192.168.31.180
     * smart.rovemaker.com  //https
     * 192.168.10.105
     */
    final String URLIP = "192.168.31.180";
    final String ENDPOINT = "http://" + URLIP + "/api/";

    final String TIME_STAMP_HEADER = "timestamp";
    final String CLIENT_ID_HEADER = "clientId";
    final String CLIENT_ID_KEY = "client_id";
    final String CLIENT_ID = "SWQxcGJxM2RrRkoyOTAxNGU";
    final String APPKEY_TOKEN = "S2V5MzY3MDg5YTBkMWRiNDlmZmI0NzY4Yzg3MzdjMzlkOWU";
    final String ACCEPT_HEADER = "Accept: application/json";
    final String UA_HEADER = "User-Agent: Retrofit-ecampus-App";
    final String SIGNSEAT_HEADER = "signSeat: HEADER";
    final String SIGN_HEADER = "sign";


    final String CONTENT_CHECKSUM_HEADER = "Content-Checksum";
    final String SKIP_SIGN_HEADER_NAME = "X-SkipSign";
    final String SKIP_SIGN_HEADER = "X-SkipSign: true";
    static final String JSON_CONTENT_TYPE = "application/json";


    //学校列表
    @Headers({
            SIGNSEAT_HEADER,
            ACCEPT_HEADER,
            UA_HEADER
    })
    @GET("school/menu")
    Observable<HttpResult<List<School>>> schoolList();

    //设备列表
    @Headers({
            SIGNSEAT_HEADER,
            ACCEPT_HEADER,
            UA_HEADER,
            SKIP_SIGN_HEADER
    })
    @GET("sewage/device/list")
    Observable<HttpResult> getSewageDevice(@Query("schoolId") int schoolId);

    /*班主任获取本班学生录入信息*/
    @Headers({
            SIGNSEAT_HEADER,
            ACCEPT_HEADER,
            UA_HEADER
    })
    @POST("cb/getAllStudent")
    Observable<HttpResult<List<FaceUser>>> getfaces(@Query("cardNumber") String cardNumber,
                                                    @Query("schoolId") int schoolId);


    @Headers({
            SIGNSEAT_HEADER,
            ACCEPT_HEADER,
            UA_HEADER
    })
    @Multipart
    @POST("cb/face/detectface")
    Observable<HttpResult> updataFace(@Query("faceUserId") int faceUserId, @PartMap Map<String, RequestBody> params);


    /********
     * Factory class that sets up a new ribot services
     *******/
    class Factory {
        static final int CONNECTION_TIMEOUT_MS = 60 * 1000 * 2;
        static final int SOCKET_READ_TIMEOUT_MS = 60 * 1000 * 2;

        public static void setUser(User u) {
            HeaderSignagureInterceptor.setUser(u);
        }


        public static MvpService makeRibotService(Context context) {
            OkHttpClient.Builder builer = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                    .readTimeout(SOCKET_READ_TIMEOUT_MS, TimeUnit.MILLISECONDS);

            // add auth header
            builer.interceptors().add(new HeaderSignagureInterceptor());

            // add log interceptor
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);
            builer.interceptors().add(logging);

            OkHttpClient okHttpClient = builer.build();


            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MvpService.ENDPOINT)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            return retrofit.create(MvpService.class);
        }

        static final class HeaderSignagureInterceptor implements Interceptor {

            private static String userId;
            private static String userToken;
            private static String securetId;

            public static void setUser(User u) {
                userId = u.getUserId();
                userToken = u.getToken();
                securetId = u.getSecuretId();
            }


            private static String generateQueryString(Map<String, String> params) {
                if (params == null || params.isEmpty()) {
                    return null;
                }
                StringBuffer buffer = new StringBuffer();
                List<String> keys = new ArrayList<String>(params.keySet());
                Collections.sort(keys);
                for (String key : keys) {
                    String value = params.get(key);
                    if (null == value) {
                        value = "";
                    }
                    value = getUtf8EscapedString(value);
                    buffer.append("&").append(key).append("=").append(value);
                }
                return buffer.substring(1);
            }

            public static String getUtf8EscapedString(String input) {
                String result = null;
                try {
                    result = URLEncoder.encode(input, "utf8");
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
                return result;
            }

            private static String mapToCheckSum(Map<String, String> querys, String token) {
                String queryString = generateQueryString(querys) + token;
                ELog.i("=======签名===sign====" + queryString);
                try {
                    return Coder.hashMD5(queryString.getBytes("utf8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            private static String bodyCheckSum(final Request request) {
                String returnVal = "";
                final Buffer buffer = new Buffer();
                try {
                    request.body().writeTo(buffer);
                    InputStream input = buffer.inputStream();
                    MessageDigest md5Hash = MessageDigest.getInstance("MD5");
                    int numRead = 0;
                    byte[] buf = new byte[1024];
                    while (numRead != -1) {
                        numRead = input.read(buf);
                        if (numRead > 0) {
                            md5Hash.update(buf, 0, numRead);
                        }
                    }
                    input.close();

                    byte[] md5Bytes = md5Hash.digest();
                    for (int i = 0; i < md5Bytes.length; i++) {
                        returnVal += Integer.toString((md5Bytes[i] & 0xff) + 0x100, 16).substring(1);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                return returnVal;
            }

            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request.Builder newBuilder = chain.request().newBuilder();

                final Request req = chain.request();
                final String timeStamp = String.valueOf(System.currentTimeMillis() / 1000l);

                Map<String, String> queryParameters = new HashMap<>();
                queryParameters.put(TIME_STAMP_HEADER, timeStamp);
                newBuilder.addHeader(TIME_STAMP_HEADER, timeStamp);

                final HttpUrl httpUrl = req.url();
                // put query parameter in map
                final int queryParameterCounts = httpUrl.querySize();
                for (int i = 0; i < queryParameterCounts; i++) {
                    final String key = httpUrl.queryParameterName(i);
                    final String value = httpUrl.queryParameterValue(i);
                    queryParameters.put(key, value);
                }

                if (null == req.header(SKIP_SIGN_HEADER_NAME)) {
                    ELog.i("=======签名===1111====");
                    queryParameters.put(CLIENT_ID_KEY, CLIENT_ID);
                    newBuilder.addHeader(CLIENT_ID_HEADER, CLIENT_ID);
                    // add Content-CheckSum header
//                    if (req.body() != null && req.body().contentLength() > 0 && req.body().contentType().toString().startsWith(JSON_CONTENT_TYPE)) {
//                        String checkSum = bodyCheckSum(req);
//                        newBuilder.addHeader(CONTENT_CHECKSUM_HEADER, checkSum);
//                        queryParameters.put(CONTENT_CHECKSUM_HEADER, checkSum);
//                    }
                    // calculate X-Sign
                    newBuilder.addHeader(SIGN_HEADER, mapToCheckSum(queryParameters, APPKEY_TOKEN));
                } else {
                    ELog.i("=======签名===2222====");
                    queryParameters.put("securetId", securetId);
                    newBuilder.addHeader("securetId", securetId);

                    queryParameters.put("userId", userId);
                    newBuilder.addHeader("userId", userId);

                    // calculate X-Sign
                    newBuilder.addHeader(SIGN_HEADER, mapToCheckSum(queryParameters, userToken));
                }

                return chain.proceed(newBuilder.build());
            }


        }


    }

}

package com.android.retorfit.network;
import com.android.retorfit.bean.Demo;
import com.android.retorfit.bean.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiUrl {



    @FormUrlEncoded
    @POST(Constans.getCode)
    Observable<BaseResponse<BaseResponse>>getCode(@Field("mobile") String mobile);



    @FormUrlEncoded
    @POST(Constans.loginUrl)
    Observable<BaseResponse<UserInfo>> login(@Field("mobile") String mobile, @Field("password") String password);



    //亲测可用
    @Multipart
    @POST("member/avatar")
    Observable<Demo> uploadImage(@HeaderMap Map<String, String> headers, @Part MultipartBody.Part file);

    @Multipart
    @POST("member/avatar")
    Observable<Demo> uploadImage1(@HeaderMap Map<String, String> headers, @Part List<MultipartBody.Part> file);
}

package com.android.retorfit.network;
import com.android.retorfit.bean.Demo;
import com.android.retorfit.bean.UserInfo;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiUrl {


    //第二种方式：GET带参数
    @GET("api/data/{type}/{count}/{page}")
    Observable<Demo> getUser(@Path("type") String type, @Path("count") int count, @Path("page") int page);
    @GET("users/whatever")
    Observable<Demo> getUser(@Query("client_id") String id, @Query("client_secret") String secret);
    @GET("users/whatever")
    Observable<Demo> getUser(@QueryMap Map<String, String> info);





    @Headers("Accept:application/json")
    @POST("auth/login")
    @FormUrlEncoded
    Observable<Demo> postUser(@Field("username") String username, @Field("password") String password);
    //多个参数
    Observable<Demo> postUser(@FieldMap Map<String, String> map);



    //    @Headers("Accept:application/json")
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




    /**
     * TODO DELETE
     */
    @DELETE("member_follow_member/{id}")
    Observable<Demo> delete(@Header("Authorization") String auth, @Path("id") int id);

    /**
     * TODO PUT
     */
    @PUT("member")
    Observable<Demo> put(@HeaderMap Map<String, String> headers,
                         @Query("nickname") String nickname);
}

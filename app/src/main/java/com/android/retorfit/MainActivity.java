package com.android.retorfit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.retorfit.bean.UserInfo;
import com.android.retorfit.network.BaseResponse;
import com.android.retorfit.network.HttpHelper;
import com.android.retorfit.network.MyObserver;
import com.android.retorfit.network.RetrofitUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
             //   getCode();
            }
        });




    }

    private  void getCode(){
        HttpHelper.http(RetrofitUtils.getInstance().mApiUrl.getCode("15824857592"), new MyObserver<BaseResponse>(this) {
            @Override
            protected void onSuccess(BaseResponse baseResponse) {
               if(baseResponse!=null&&baseResponse.getMsg()!=null){
                   Log.e("zjun","msg:"+baseResponse.getMsg());
               }
            }

            @Override
            protected void onError(BaseResponse s) {

            }
        });
    }


    private  void login(){
        HttpHelper.http(RetrofitUtils.getInstance().mApiUrl.login("15824857592", "664986"), new MyObserver<UserInfo>(this) {
            @Override
            protected void onSuccess(UserInfo userInfo) {
               if(userInfo!=null){
                   Toast.makeText(MainActivity.this,"欢迎"+userInfo.getNick(),Toast.LENGTH_SHORT).show();
               }
            }
        });
    }



//    /**
//     * 上传图片
//     * @param context
//     * @param observer
//     */
//    public static void upImagView(RxFragment context, String  access_token,String str, Observer<Demo>  observer){
//        File file = new File(str);
////        File file = new File(imgPath);
//        Map<String,String> header = new HashMap<String, String>();
//        header.put("Accept","application/json");
//        header.put("Authorization",access_token);
////        File file =new File(filePath);
//        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
////        RequestBody requestFile =
////                RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("file", file.getName(), reqFile);
//        RetrofitUtils.getApiUrl().uploadImage(header,body).compose(RxHelper.observableIO2Main(context))
//                .subscribe(observer);
//    }



//    /**
//     * 上传多张图片
//     * @param files
//     */
//    public static void upLoadImg(RxFragment context, String access_token, List<File> files, Observer<Demo> observer1){
//        Map<String,String> header = new HashMap<String, String>();
//        header.put("Accept","application/json");
//        header.put("Authorization",access_token);
//        MultipartBody.Builder builder = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM);//表单类型
//        for (int i = 0; i < files.size(); i++) {
//            File file = files.get(i);
//            RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/*"), file);
//            builder.addFormDataPart("file", file.getName(), photoRequestBody);
//        }
//        List<MultipartBody.Part> parts = builder.build().parts();
//        RetrofitUtils.getApiUrl().uploadImage1(header,parts).compose(RxHelper.observableIO2Main(context))
//                .subscribe(observer1);
//    }
}

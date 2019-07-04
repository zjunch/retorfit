package com.android.retorfit.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class MyObserver<T> implements Observer<BaseResponse<T>> {
    private ProgressDialog dialog;
    Context mContext;
    boolean mShowDialog;
    private Disposable d;
    public MyObserver(Context context, Boolean showDialog) {
        mContext = context;
        mShowDialog = showDialog;
        Log.e("zjun","MyObserver");
    }

    public MyObserver(Context context) {
        this(context,false);
    }

    @Override
    public void onSubscribe(Disposable d) {
        Log.e("zjun","onSubscribe");
        this.d = d;
        if (!isConnected(mContext)) {
            Toast.makeText(mContext,"未连接网络",Toast.LENGTH_SHORT).show();
            if (d.isDisposed()) {
                d.dispose();
            }
        } else {
            if (dialog == null && mShowDialog == true) {
                dialog = new ProgressDialog(mContext);
                dialog.setMessage("正在加载中");
                dialog.show();
            }
        }
    }

    @Override
    public void onNext(BaseResponse<T> value) {
        if(value.getCode()==200){
            Log.e("zjun","成功");
            if(value.getData()!=null){
                onSuccess(value.getData());
            }else{
                onSuccess((T) value);
            }

        }else{
            onError(value);
            Log.e("zjun","失败");
        }

    }

    @Override
    public void onError(Throwable e) {
        Log.e("zjun","onError");
        if (d.isDisposed()) {
            d.dispose();
        }
    //    hidDialog();
    }

    @Override
    public void onComplete() {
        if (d.isDisposed()) {
            d.dispose();
        }
        //    hidDialog();
    }



    /**
     * 是否有网络连接，不管是wifi还是数据流量
     * @param context
     * @return
     */
    public static boolean isConnected(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null)
        {
            return false;
        }
        boolean available = info.isAvailable();
        return available;
    }


    protected abstract  void onSuccess(T  t);
    protected     void onError(BaseResponse  s){

    }
}

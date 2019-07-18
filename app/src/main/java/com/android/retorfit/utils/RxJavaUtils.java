package com.android.retorfit.utils;

import com.android.retorfit.bean.ImageInfo;
import com.android.retorfit.interface_common.FunctionManager;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RxJavaUtils {
    public  static  int finishNumber=0;
    public static void toCompress(final ImageInfo imageInfo, final int size) {
        try {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> emitter) {
                    String newPath = VMBitmap.compressTempImage(imageInfo.getImagePath());
                    emitter.onNext(newPath);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String newPath) {
                            //回调后在UI界面上展示出来
                            imageInfo.setImagePath(newPath);
                            finishNumber+=1;
                            callBack(size);
                        }
                    });
        }catch (Exception ex){
             finishNumber+=1;
            String oldPath=imageInfo.getImagePath();
            imageInfo.setImagePath(oldPath);
            callBack(size);
        }

    }

    public  static  void callBack(int size){
        if(size==finishNumber){
            FunctionManager.getInstance().invokeNoneFunc("compress");
            finishNumber=0;
        }

    }

}

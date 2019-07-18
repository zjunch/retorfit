package com.creativearts.ymt.utils;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/13 0013.
 */

public class CheckPermissionUtils {

    /**
     * 获取权限
     * @return 所有的申请权限都可可在这里传值
     */
    public static <T>boolean getPermissions(T activityOrFragment, int requestCode, String... permissions) {
        return requestPerssions(activityOrFragment, requestCode, permissions);
    }


    /**
     * 是否需要检查权限
     */
    private static boolean needCheckPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * 获取sd存储卡读写权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getExternalStoragePermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 获取拍照权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static<T> boolean getCameraPermissions(@NonNull T activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 获取拍照权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static<T> boolean getCameraPermissionsByFragment(@NonNull T fragment, int requestCode) {
        return requestPerssions(fragment, requestCode, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    /**
     * 获取sd存储卡读写权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static <T> boolean getExternalStoragePermissionsByFragment(@NonNull T fragment, int requestCode) {
        return requestPerssions(fragment, requestCode, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }



    /**
     * 获取麦克风权限
     *
     * @return 是否已经获取权限，没有自动申请
     */
    public static boolean getAudioPermissions(@NonNull Activity activity, int requestCode) {
        return requestPerssions(activity, requestCode, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    /**
     * 申请权限<br/>
     * 使用onRequestPermissionsResult方法，实现回调结果或者自己普通处理
     *
     * @return 是否已经获取权限
     */
    public static <T> boolean requestPerssions(T activityOrFragment, int requestCode, String... permissions) {
        if (!needCheckPermission()) {
            return true;
        }
        Activity activity=null;
        if (activityOrFragment instanceof Activity) {
            activity = (Activity) activityOrFragment;
        } else if(activityOrFragment instanceof Fragment) {
            Fragment fragment = (Fragment) activityOrFragment;
            activity = fragment.getActivity();
        }
        if(activity==null){
            return false;
        }
        if (!hasPermissons(activity, permissions)) {
            if (deniedRequestPermissonsAgain(activity, permissions)) {
                startApplicationDetailsSettings(activity, requestCode);
                //返回结果onActivityResult
            } else {
                List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
                if (deniedPermissions != null) {
                    if (activityOrFragment instanceof Activity) {
                        ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
                    } else {
                        Fragment fragment = (Fragment) activityOrFragment;
                        fragment.requestPermissions(permissions, requestCode);
                    }

                    //返回结果onRequestPermissionsResult
                }
            }
            return false;
        }
        return true;
    }

    public static List<String> getDeniedPermissions(@NonNull Context activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return null;
        }
        if(permissions==null||permissions.length==0){
            return null;
        }
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        if (!deniedPermissions.isEmpty()) {
            return deniedPermissions;
        }

        return null;
    }

    /**
     * 是否拥有权限
     */
    public static boolean hasPermissons(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 是否拒绝了再次申请权限的请求（点击了不再询问）
     */
    public static boolean deniedRequestPermissonsAgain(@NonNull Activity activity, @NonNull String... permissions) {
        if (!needCheckPermission()) {
            return false;
        }
        if(permissions==null||permissions.length==0){
            return false;
        }
        List<String> deniedPermissions = getDeniedPermissions(activity, permissions);
        for (String permission : deniedPermissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_DENIED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    //当用户之前已经请求过该权限并且拒绝了授权这个方法返回true
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 打开app详细设置界面<br/>
     * <p>
     * 在 onActivityResult() 中没有必要对 resultCode 进行判断，因为用户只能通过返回键才能回到我们的 App 中，<br/>
     * 所以 resultCode 总是为 RESULT_CANCEL，所以不能根据返回码进行判断。<br/>
     * 在 onActivityResult() 中还需要对权限进行判断，因为用户有可能没有授权就返回了！<br/>
     */
    public static void startApplicationDetailsSettings(@NonNull Activity activity, int requestCode) {
        Log.e("zjun", "点击权限，并打开全部权限");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, requestCode);
        activity.startActivityForResult(intent, requestCode);


    }
}

package com.android.retorfit.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

public class ValueUtil {


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public  static  int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public  static  int getNavigationBarHeight(Context context) {

        Resources resources = context.getResources();

        int resourceId=resources.getIdentifier("navigation_bar_height","dimen","android");

        int height = resources.getDimensionPixelSize(resourceId);


        return height;

    }
    public static double change(double a){
        return a * Math.PI  / 180;
    }

    public static double changeAngle(double a){
        return a * 180 / Math.PI;
    }


    /**
     * 判断是否是全面屏
     */
    private volatile static boolean mHasCheckAllScreen;
    private volatile static boolean mIsAllScreenDevice;

    public static boolean isAllScreenDevice(Context context) {
        if (mHasCheckAllScreen) {
            return mIsAllScreenDevice;
        }
        mHasCheckAllScreen = true;
        mIsAllScreenDevice = false;
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            float width, height;
            if (point.x < point.y) {
                width = point.x;
                height = point.y;
            } else {
                width = point.y;
                height = point.x;
            }
            if (height / width >= 1.97f) {
                mIsAllScreenDevice = true;
            }
        }
        return mIsAllScreenDevice;
    }




    public static boolean[] isSystemUiVisible(Window window) {
        boolean[] result = new boolean[]{false, false};
        if (window == null) {
            return result;
        }
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (attributes != null) {
            result[0] = (attributes.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            result[1] = (((attributes.systemUiVisibility | decorView.getWindowSystemUiVisibility()) &
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) && (attributes.flags & WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) != 0;
        }
        //
        Object decorViewObj = window.getDecorView();
        Class<?> clazz = decorViewObj.getClass();
        int mLastBottomInset = 0, mLastRightInset = 0, mLastLeftInset = 0;
        try {
            Field mLastBottomInsetField = clazz.getDeclaredField("mLastBottomInset");
            mLastBottomInsetField.setAccessible(true);
            mLastBottomInset = mLastBottomInsetField.getInt(decorViewObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Field mLastRightInsetField = clazz.getDeclaredField("mLastRightInset");
            mLastRightInsetField.setAccessible(true);
            mLastRightInset = mLastRightInsetField.getInt(decorViewObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Field mLastLeftInsetField = clazz.getDeclaredField("mLastLeftInset");
            mLastLeftInsetField.setAccessible(true);
            mLastLeftInset = mLastLeftInsetField.getInt(decorViewObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isNavBarToRightEdge = mLastBottomInset == 0 && mLastRightInset > 0;
        int size = isNavBarToRightEdge ? mLastRightInset : (mLastBottomInset == 0 && mLastLeftInset > 0 ? mLastLeftInset : mLastBottomInset);
        result[1] = result[1] && size > 0;
        return result;

    }

}

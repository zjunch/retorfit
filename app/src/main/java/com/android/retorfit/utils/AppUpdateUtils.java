package com.creativearts.ymt.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creativearts.common.interface_common.FunctionManager;
import com.creativearts.common.interface_common.FunctionNoParamNoResult;
import com.creativearts.common.interface_common.FunctionOnlyParam;
import com.creativearts.ymt.BuildConfig;
import com.creativearts.ymt.R;
import com.creativearts.ymt.base.BaseActivity;
import com.creativearts.ymt.view.MyProgressBar;
import com.mobanker.eagleeye.utils.PreferencesUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import static com.umeng.socialize.utils.ContextUtil.getPackageName;

public class AppUpdateUtils {

    AlertDialog downLoadDialog;
    BaseActivity mContext;
    private String apkUrl, versionContent, updateContent;
    private  boolean isSlience;
    public AppUpdateUtils(BaseActivity context, String apkUrl, String versionContent, String updateContent) {
        this.mContext = context;
        this.apkUrl = apkUrl;
        this.versionContent = versionContent;
        this.updateContent = updateContent;
        this.isSlience=false;
        if(TextUtils.isEmpty(updateContent)){
            this.updateContent="1.全新视觉界面，优化用户体验";
        }
    }

    public AppUpdateUtils(BaseActivity mContext, String apkUrl) {
        this.mContext = mContext;
        this.apkUrl = apkUrl;
        this.isSlience=true;
        Log.e("zjun","开始静默下载");
    }

    public void showDialog(boolean isForce) {
        boolean isDownLoading = PreferencesUtils.getBoolean(mContext, "isDownLoading", false);
        if(isDownLoading){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View mInflate = LayoutInflater.from(mContext).inflate(R.layout.dialog_app_update, null);
        TextView text_cancle = mInflate.findViewById(R.id.text_cancle);
        LinearLayout lv_content = mInflate.findViewById(R.id.lv_content);
        TextView tv_force = mInflate.findViewById(R.id.tv_force);
        if(isForce){
            tv_force.setVisibility(View.VISIBLE);
        }else {
            tv_force.setVisibility(View.GONE);
        }
//        TextView textView = new TextView(mContext);
//        textView.setPadding(ValueUtil.dip2px(mContext, 18), ValueUtil.dip2px(mContext, 3), ValueUtil.dip2px(mContext, 18), ValueUtil.dip2px(mContext, 3));
//        textView.setTextColor(mContext.getResources().getColor(R.color.gray_7));
//        textView.setTextSize(11);
//        textView.setText(updateContent);
//        lv_content.addView(textView);
        addView(lv_content,updateContent);
        addView(lv_content,"2.修复已知bug");
        TextView tv_load = mInflate.findViewById(R.id.tv_load);
        text_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (downLoadDialog != null) {
                    downLoadDialog.dismiss();
                }
            }
        });
        tv_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStorePermission();
            }
        });
        tv_force.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStorePermission();
            }
        });

        downLoadDialog = builder.create();
        downLoadDialog.show();
        Window window = downLoadDialog.getWindow();
        window.setContentView(mInflate);
        WindowManager windowManager = mContext.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = downLoadDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 7 / 10); //设置宽度
        downLoadDialog.getWindow().setAttributes(lp);
        downLoadDialog.getWindow().setBackgroundDrawableResource(R.drawable.shape_round5_solid_white);
        downLoadDialog.setCanceledOnTouchOutside(false);
        downLoadDialog.setCancelable(false);
    }

    private  void addView(LinearLayout linearLayout,String updateContent){
        TextView textView = new TextView(mContext);
        textView.setPadding(ValueUtil.dip2px(mContext, 18), ValueUtil.dip2px(mContext, 3), ValueUtil.dip2px(mContext, 18), ValueUtil.dip2px(mContext, 3));
        textView.setTextColor(mContext.getResources().getColor(R.color.gray_7));
        textView.setTextSize(11);
        textView.setText(updateContent);
        linearLayout.addView(textView);
    }

    /**
     * 公用方法，静默下载也可以使用
     */
    public void toUpdateServiceDownload() {
        if(!isSlience){
            FunctionManager.getInstance().addFunction(new FunctionOnlyParam<File>("downloadFinish") {
                @Override
                public void funtion(File file) {
                    checkInstallPermission(file);
                }
            });
        }else{
            FunctionManager.getInstance().addFunction(new FunctionOnlyParam<File>("silence_downloadFinish") {
                @Override
                public void funtion(File file) {
                    checkInstallPermission(file);
                }
            });
        }

        if (downLoadDialog != null&&downLoadDialog.isShowing()) {
            downLoadDialog.dismiss();
        }
        Intent intent = new Intent(mContext, UpdateService.class);
        intent.putExtra("apkUrl", apkUrl);
        intent.putExtra("isSlience",isSlience);
        mContext.startService(intent);
    }


    public void checkStorePermission() {
        boolean bl = CheckPermissionUtils.getExternalStoragePermissions(mContext, mContext.APP_STORAGE_PERMMISION);
        if (bl) {
            toUpdateServiceDownload();
        }else{
            FunctionManager.getInstance().addFunction(new FunctionNoParamNoResult("downapp_storage") {
                @Override
                public void funtion() {
                    toUpdateServiceDownload();
                }
            });
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity(final File file) {
        Uri packageURI = Uri.parse("package:" + getPackageName());
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
        FunctionManager.getInstance().addFunction(new FunctionNoParamNoResult("install_app") {
            @Override
            public void funtion() {
                openFile(file, mContext);
            }
        });
        mContext.startActivityForResult(intent, mContext.INSTALL_PERMMISION);
    }


    /**
     * 8.0需要安装的权限
     */
    private void checkInstallPermission(final File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            boolean haveInstallPermission = mContext.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                startInstallPermissionSettingActivity(file);
            } else {
                openFile(file, mContext);
            }
        } else {
            openFile(file, mContext);
        }
    }


    /**
     * 安装应用
     * @param file
     */
    public    void  openFile(File file, Context mContext) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".file_Provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

}

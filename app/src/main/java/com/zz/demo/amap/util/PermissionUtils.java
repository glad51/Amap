package com.zz.demo.amap.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

/**
 * 权限对话框管理
 */
public class PermissionUtils {

    public static final int REQUEST_CODE = 0;
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝

    public static void PermissionDialog(final Activity activity) {
        Dialog permissionDialog = new AlertDialog.Builder(activity)
                .setTitle("帮助")
                .setCancelable(false)
                .setMessage("当前应用缺少必要权限。\n请点击\"设置\"-\"权限\"-打开所需权限。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.finish();
                    }
                })
                .setPositiveButton("去设置",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startSettingIntent(activity);
                            }
                        }).create();

        permissionDialog.show();
    }

    /**
     * 启动app设置授权界面
     *
     * @param context
     */
    public static void startSettingIntent(Activity context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivityForResult(localIntent, REQUEST_CODE);
    }
}

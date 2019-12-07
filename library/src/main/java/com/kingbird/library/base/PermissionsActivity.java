package com.kingbird.library.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.PermissionsUtils;
import com.kingbird.library.utils.Plog;
import com.kingbird.library.utils.SharedPreferencesUtils;
import com.kingbird.library.view.AdvertistingView;

import static android.os.Build.VERSION_CODES.M;

/**
 * @ClassName: PermissionsActivity
 * @Description: 权限申请
 * @Author: Pan
 * @CreateDate: 2019/11/25 10:51
 */
public class PermissionsActivity extends AppCompatActivity {

    private Context context;
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        if (Build.VERSION.SDK_INT >= M) {
            Plog.e("开始申请权限");
            PermissionsUtils.getInstance().chekPermissions(this, permissions, permissionsResult);
        } else {
            Plog.e("不需要申请");
            SharedPreferencesUtils.writeBoolean(this, Const.PERMISSONS, true);
            Base.intentActivity("5");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 创建监听权限的接口对象
     */
    PermissionsUtils.IPermissionsResult permissionsResult = new PermissionsUtils.IPermissionsResult() {
        @Override
        public void passPermissons() {
            Plog.e("权限通过");
            SharedPreferencesUtils.writeBoolean(PermissionsActivity.this, Const.PERMISSONS, true);
            AdvertistingView advertistingView = new AdvertistingView(context);
            advertistingView.startAdvertisting();
            PackageManager packageManager = getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(SharedPreferencesUtils.readString(PermissionsActivity.this, Const.MAIN_APP_NAME));
            if (intent != null) {
                startActivity(intent);
            }
        }

        @Override
        public void forbitPermissons() {
            PermissionsUtils.getInstance().chekPermissions(PermissionsActivity.this, permissions, permissionsResult);
        }
    };
}

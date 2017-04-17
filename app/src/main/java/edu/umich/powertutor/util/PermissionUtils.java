package edu.umich.powertutor.util;

import android.Manifest;
import android.app.Activity;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

/**
 * Created by kaixuan on 17-3-2.
 */

public class PermissionUtils {
    public static void requestPermission(final Activity activity, final OnPermissionListener listener) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean grant) {
                if (grant) {
                    listener.onGranted();
                } else {
                    listener.onDenied();
                }
            }
        });
    }

    public interface OnPermissionListener {
        void onGranted();

        void onDenied();
    }
}

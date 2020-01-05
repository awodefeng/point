package com.xxun.pointsystem.utils;

import android.app.Service;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xiaoxun.sdk.IResponseDataCallBack;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.XiaoXunNetworkManager;

import net.minidev.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.os.BatteryManager;
import android.os.Bundle;
import android.content.SharedPreferences;
import com.xxun.pointsystem.utils.LogUtil;

/**
 * @author zhangweinan
 * @createtime 2018.12.21
 * @class describe 上传总经验值
 */
public class UploadExpUtils {
    private XiaoXunNetworkManager mXiaoXunNetworkManager;
    private String gid;
    private String[] keys = {"integral"};
    private String[] values = new String[1];
    private String time;
    private long oldTime;

    private NetworkStatsManager networkStatsManager;

    public static UploadExpUtils getUploadStatusUtilsInstance() {
        return UploadExpUtilshodler.uploadStatusUtilsInstance;
    }

    private static class UploadExpUtilshodler {
        private static UploadExpUtils uploadStatusUtilsInstance = new UploadExpUtils();
    }

    public synchronized void uploadExp(Context context, int exp) {
        LogUtil.i("System.currentTimeMillis() - oldTime = " + (System.currentTimeMillis() - oldTime));
        if ((System.currentTimeMillis() - oldTime) > 1000) {
            if (mXiaoXunNetworkManager == null) {
                mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
            }

            LogUtil.i(" uploadStatus!!! ");


            getValues(context, exp);
            LogUtil.i("gid = " + gid + "\n" + "keys = " + keys[0] + "\n" +  "values = " + values[0]);
            if (mXiaoXunNetworkManager.isLoginOK()) {
                LogUtil.i("isLoginOK");
                mXiaoXunNetworkManager.setMapMSetValue(gid, keys, values, new SendMessageCallback());
            }
            oldTime = System.currentTimeMillis();
        }
    }

    public synchronized void uploadExpForpadding(Context context, int exp) {
        LogUtil.i("System.currentTimeMillis() - oldTime = " + (System.currentTimeMillis() - oldTime));
        if ((System.currentTimeMillis() - oldTime) > 1000) {
            if (mXiaoXunNetworkManager == null) {
                mXiaoXunNetworkManager = (XiaoXunNetworkManager) context.getSystemService("xun.network.Service");
            }


            LogUtil.i(" uploadStatusForpadding!!! ");
            getValues(context, exp);
            if (mXiaoXunNetworkManager.isLoginOK()) {
                mXiaoXunNetworkManager.paddingSetMapMSetValue(gid, keys, values, new SendMessageCallback());
            }
            oldTime = System.currentTimeMillis();
        }
    }

    private void getValues(Context context, int exp) {
        gid = mXiaoXunNetworkManager.getWatchGid();
        values[0] = getExp(exp);
    }

    public String getExp(int exp){
        time = getCurrentTime();
        return time + "_" + exp;
    }

    private static String getCurrentTime() {
        Date d = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return format.format(d);
    }

    private class SendMessageCallback extends IResponseDataCallBack.Stub {
        @Override
        public void onSuccess(ResponseData responseData) {
            LogUtil.i("upload success!" + responseData);
        }

        @Override
        public void onError(int i, String s) {
            LogUtil.i("upload error! i = " + i + ",s = :" + s);
        }
    }
}

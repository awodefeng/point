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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import java.io.IOException;
import java.util.ArrayList;
import android.content.Context;
import com.xxun.pointsystem.XunPointApplication;
import com.xxun.pointsystem.R;

/**
 * @author zhangweinan
 * @createtime 2019.01.23
 * @class describe 工具类
 */
public class PointSystemUtils {


    private NetworkStatsManager networkStatsManager;

    public static PointSystemUtils getUploadStatusUtilsInstance() {
        return PointSystemUtilshodler.pointSystemUtilsInstance;
    }

    private static class PointSystemUtilshodler {
        private static PointSystemUtils pointSystemUtilsInstance = new PointSystemUtils();
    }

    public static void readExpConfigFromXml(Context context) {
        Resources r = context.getResources();
        XmlResourceParser xrp;
        xrp = r.getXml(R.xml.exp_config);
        int count = 0;
        Config.Clear_testitem();
        try {
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String name = xrp.getName();
                    if (name.equals("testitem")) {
                        String item_value0 = xrp.getAttributeValue(0);
                        String item_value1 = xrp.getAttributeValue(1);
                        String item_value2 = xrp.getAttributeValue(2);
                        Config.Set_item(count, xrp.getAttributeValue(0),
                                xrp.getAttributeValue(1));
                        count++;
                        XunPointApplication.mExpConfigList.add(Integer.parseInt(item_value2));
                    }
                } else if (xrp.getEventType() == XmlPullParser.END_TAG) {
                } else if (xrp.getEventType() == XmlPullParser.TEXT) {
                }
                xrp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readLabelConfigFromXml(Context context) {
        Resources r = context.getResources();
        XmlResourceParser xrp;
        xrp = r.getXml(R.xml.label_config);
        int count = 0;
        LogUtil.i("readLabelConfigFromXml ");
        try {
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String name = xrp.getName();
                    if (name.equals("testitem")) {
                        String item_value0 = xrp.getAttributeValue(0);
                        String item_value1 = xrp.getAttributeValue(1);
                        String item_value2 = xrp.getAttributeValue(2);
                        String item_value3 = xrp.getAttributeValue(3);
                        count++;
                        XunPointApplication.mLabelConfigNameList.add(item_value0);
                        XunPointApplication.mLabelConfigTypeList.add(Integer.parseInt(item_value2));
                        XunPointApplication.mLabelConfigValueList.add(Integer.parseInt(item_value3));
                        LogUtil.i("mLabelConfigValueList = " + XunPointApplication.mLabelConfigValueList);
                    }
                } else if (xrp.getEventType() == XmlPullParser.END_TAG) {
                } else if (xrp.getEventType() == XmlPullParser.TEXT) {
                }
                xrp.next();
            }
            LogUtil.i("read from xml item count = " + count);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            LogUtil.i("XmlPullParserException = " + e);
        } catch (IOException e) {
            LogUtil.i("IOException = " + e);
            e.printStackTrace();
        }
    }

    public static boolean isSameDate(String currentTime,String lastTime) {
        try {
            Calendar nowCal = Calendar.getInstance();
            Calendar dataCal = Calendar.getInstance();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            Long nowLong = new Long(currentTime);
            Long dataLong = new Long(lastTime);
            String data1 = df1.format(nowLong);
            String data2 = df2.format(dataLong);
            java.util.Date now = df1.parse(data1);
            java.util.Date date = df2.parse(data2);
            LogUtil.i("now = " + now + "\n" + "date = " + date);
            nowCal.setTime(now);
            dataCal.setTime(date);
            return isSameDay(nowCal, dataCal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if(cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
           return false;
        }
    }
}

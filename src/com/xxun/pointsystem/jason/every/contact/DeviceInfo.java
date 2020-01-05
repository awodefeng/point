package com.xxun.pointsystem.jason.every.contact;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2017/11/13.
 */

public class DeviceInfo {


    /**
     * guid : c8fa84af3dade4f253d936f3f409dd6a
     * BtMac : DF:7B:7B:C4:E7:90
     * Weight : 0.0
     * deviceType : SW710
     * Sex : 0
     * MachSerialNo : 60015614
     * WifiMac : 90:E7:C4:7B:7B:DF
     * ExpireTime :
     * Activated : true
     * isReturn : false
     * Imei : 359076060015614
     * account_status : 0
     * identity_status : -1
     * CreateTime : 20171102152113830
     * EID : 8F16216E30CDD2DDDF51BEA62184B5C6
     * SN : 1030118179
     * qqlicense : 3044022022495C77CEB2C475065B8898D0B62C513944E7B14ABCA9A96E5281EB4C0220471971FB6FCE7FCB48BEB621BAA60A74FA683BF65712F72261EF0B9C9FD87B07
     * Height : 0.0
     * Name : 359076060015614
     */

    @SerializedName("guid")
    public String guid;
    @SerializedName("BtMac")
    public String BtMac;
    @SerializedName("Weight")
    public double Weight;
    @SerializedName("deviceType")
    public String deviceType;
    @SerializedName("Sex")
    public int Sex;
    @SerializedName("MachSerialNo")
    public String MachSerialNo;
    @SerializedName("WifiMac")
    public String WifiMac;
    @SerializedName("ExpireTime")
    public String ExpireTime;
    @SerializedName("Activated")
    public boolean Activated;
    @SerializedName("isReturn")
    public boolean isReturn;
    @SerializedName("Imei")
    public String Imei;
    @SerializedName("account_status")
    public int accountStatus;
    @SerializedName("identity_status")
    public int identityStatus;
    @SerializedName("CreateTime")
    public String CreateTime;
    @SerializedName("EID")
    public String EID;
    @SerializedName("SN")
    public int SN;
    @SerializedName("qqlicense")
    public String qqlicense;
    @SerializedName("Height")
    public double Height;
    @SerializedName("Name")
    public String Name;
    @SerializedName("SimNo")
    public String SimNo;
    @SerializedName("NickName")
    public String NickName;
    
    
    public static DeviceInfo objectFromData(String str) {

        return new Gson().fromJson(str, DeviceInfo.class);
    }

    public static DeviceInfo objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), DeviceInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<DeviceInfo> arrayDeviceInfoFromData(String str) {

        Type listType = new TypeToken<ArrayList<DeviceInfo>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<DeviceInfo> arrayDeviceInfoFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<DeviceInfo>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}

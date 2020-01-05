package com.xxun.pointsystem.jason.Contacts;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jixiang on 2017/9/15.
 */

public class Contacts {
    /**
     * EID : 83195194EE8B0218A5A07D8498D0714E
     * sync_array : [{"number":"13817873481","contactsType":0,"oldupdateTS":null,"attri":0,"id":"20170915144759289","name":"爸爸","contact_weight":0,"user_gid":"9630244E70BC936E56049C4DB07A3596","updateTS":"20170915144821387","user_eid":"5E0FBF12F7728F1B40F71855998DA312","sub_number":"666666666666666666"},{"number":"15121005015","contactsType":1,"oldupdateTS":"20170915144821387","attri":1,"id":"20170915144919854","name":"妈妈","contact_weight":0,"updateTS":"20170915144946469","sub_number":"666666666666666666"},{"number":"9868665949945","contactsType":1,"oldupdateTS":"20170915144946469","attri":20,"id":"20170915144950594","name":"哥哥","contact_weight":0,"updateTS":"20170915145049809","sub_number":"96689866688"},{"number":"68686864616","contactsType":1,"oldupdateTS":"20170915145049809","attri":21,"id":"20170915145054428","name":"姐姐","contact_weight":0,"updateTS":"20170915145109889","sub_number":"9868684"}]
     * SN : 591747292
     * lastTS : 20170915145109889
     */

    @SerializedName("EID")
    public String EID;
    @SerializedName("SN")
    public int SN;
    @SerializedName("lastTS")
    public String lastTS;
    @SerializedName("sync_array")
    public List<SyncArrayBean> syncArray;

    public static Contacts objectFromData(String str) {

        return new Gson().fromJson(str, Contacts.class);
    }

    public static Contacts objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), Contacts.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Contacts> arrayContactsFromData(String str) {

        Type listType = new TypeToken<ArrayList<Contacts>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<Contacts> arrayContactsFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<Contacts>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }
}

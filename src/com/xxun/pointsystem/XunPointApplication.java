package com.xxun.pointsystem;

import android.app.Application;
import java.util.ArrayList;
import com.xxun.pointsystem.utils.PointSystemUtils;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.xxun.pointsystem.utils.LogUtil;

public class XunPointApplication extends Application {
    private int mPreTotalExp;
    private static int mTotalExp;
    private static int mStepExp;
    private static int mLuckyExp;
    public static int mCallExp;
    public static int mStoryExp;
    public static int mDurationExp;
    public static int mEnglishExp;
    public static boolean isClickLuckyBag = true;
    private int mGrade;
    private static XunPointApplication instance;
    public static ArrayList<Integer> mExpConfigList= new ArrayList<Integer>();
    public static ArrayList<Integer> mLabelConfigTypeList= new ArrayList<Integer>();
    public static ArrayList<Integer> mLabelConfigValueList= new ArrayList<Integer>();
    public static ArrayList<String> mLabelConfigNameList= new ArrayList<String>();
    public static long mExitTime = 0;
    public static long mEnterTime = System.currentTimeMillis(); 


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LogUtil.i("Application onCreate");
        PointSystemUtils.readExpConfigFromXml(this);
        PointSystemUtils.readLabelConfigFromXml(this);
    }

    public static XunPointApplication getInstance() {
        return instance;
    }

    public void initExp(){
        try{
            mExitTime = Settings.Global.getLong(getContentResolver(), "exit_time");
        }catch (SettingNotFoundException e){
            mExitTime = System.currentTimeMillis();
        }
        try{
            mGrade = Settings.Global.getInt(getContentResolver(), "my_grade");
        }catch (SettingNotFoundException e){
            mGrade = 0;
        }
        try{
            mStepExp = Settings.Global.getInt(getContentResolver(), "step_exp");
        }catch (SettingNotFoundException e){
            mStepExp = 0;
        }
        try{
            mLuckyExp = Settings.Global.getInt(getContentResolver(), "lucky_exp");
        }catch (SettingNotFoundException e){
            mLuckyExp = 0;
        }
        try{
            mCallExp = Settings.Global.getInt(getContentResolver(), "call_exchange_exp");
        }catch (SettingNotFoundException e){
            mCallExp = 0;
        }
        try{
            mStoryExp = Settings.Global.getInt(getContentResolver(), "story_exchange_exp");
            LogUtil.i("application onCreate mStoryExp = " + mStoryExp);
        }catch (SettingNotFoundException e){
            mStoryExp = 0;
        }
        try{
            mDurationExp = Settings.Global.getInt(getContentResolver(), "duration_exchange_exp");
            LogUtil.i("application onCreate mDurationExp = " + mDurationExp);
        }catch (SettingNotFoundException e){
            mDurationExp = 0;
        }
        try{
            mEnglishExp = Settings.Global.getInt(getContentResolver(), "english_exchange_exp");
        }catch (SettingNotFoundException e){
            mEnglishExp = 0;
        }
        try{
            mTotalExp = Settings.Global.getInt(getContentResolver(), "total_exp");
        }catch (SettingNotFoundException e){
            mTotalExp = 0;
        }
    }

    public int getTotalExp(){
        try{
            mTotalExp = Settings.Global.getInt(getContentResolver(), "total_exp");
        }catch (SettingNotFoundException e){
            LogUtil.i("get total_exp is error:" + e);
            Settings.Global.putInt(getContentResolver(),"total_exp", 0);
        }
        return this.mTotalExp;
    }

    public void setTotalExp(int exp){
        LogUtil.i("mTotalExp ++++++++++++" + mTotalExp);
        mTotalExp = mTotalExp + exp;
        LogUtil.i("exp ++++++++++++" + exp);
        Settings.Global.putInt(getContentResolver(),"total_exp", mTotalExp);
    }

    public int getGrade(){
        try{
            mGrade = Settings.Global.getInt(getContentResolver(), "my_grade");
        }catch (SettingNotFoundException e){
            LogUtil.i("get my_grade is error:" + e);
        }finally {
            return this.mGrade;
        }
    }

    public void setGrade(int grade){
        mGrade = grade;
        Settings.Global.putInt(getContentResolver(),"my_grade", mGrade);
    }

    public int getStepExp(){
        try{
            mStepExp = Settings.Global.getInt(getContentResolver(), "step_exp");
        }catch (SettingNotFoundException e){
            LogUtil.i("get step_exp is error:" + e);
        }finally {
            return this.mStepExp;
        }
    }

    public void setStepExp(int stepexp){
        mStepExp = mStepExp + stepexp;
        setTotalExp(stepexp);
        Settings.Global.putInt(getContentResolver(),"step_exp", mStepExp);
    }

    public int getLuckyExp(){
        try{
            mLuckyExp = Settings.Global.getInt(getContentResolver(), "lucky_exp");
        }catch (SettingNotFoundException e){
            LogUtil.i("get lucky_exp is error:" + e);
        }finally {
            return this.mLuckyExp;
        }
    }

    public void setLuckyExp(int luckyexp){
        mLuckyExp = mLuckyExp + luckyexp;
        LogUtil.i("mLuckyExp +++++++++++= " + mLuckyExp);
        LogUtil.i("luckyexp +++++++++++= " + luckyexp);
        setTotalExp(luckyexp);
        Settings.Global.putInt(getContentResolver(),"lucky_exp", mLuckyExp);
    }

}

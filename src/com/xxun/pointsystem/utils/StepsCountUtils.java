package com.xxun.pointsystem.utils;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.SharedPreferences;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;


public class StepsCountUtils {

    public static String getTimeStampLocal() {
        Date d = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(d).toString();
    }
        
    public static long calcTwoTimeStampInterval(String mDateStart, String mDateEnd) {
        long result = 0;
        if (mDateStart == null || mDateEnd == null) {
            return 0;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date startTime = null;
        Date endTime = null;
        try {
            startTime = format.parse(mDateStart);
            endTime = format.parse(mDateEnd);
            result = (endTime.getTime() - startTime.getTime()) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPhoneStepsByFirstSteps(Context context,String oldSteps, String mTotalSteps){
        String curSteps = "0";
        if(mTotalSteps == null){
            return curSteps;
        }
        try{
            String phoneStepsPref = oldSteps;
            if(phoneStepsPref.equals("0")) {
                return curSteps;
            }
            String steps[] = phoneStepsPref.split("_");
            if(steps.length >= 3 && compareTodayToLastInfo(steps[0])) {
                if (Integer.valueOf(mTotalSteps) < Integer.valueOf(steps[2])) {
                    int stepOffset = Integer.valueOf(steps[2]) - Integer.valueOf(steps[1]);
                    if (stepOffset < 0) {
                        stepOffset = 0;
                    }
                    curSteps = String.valueOf(Integer.valueOf(mTotalSteps) + stepOffset);
                } else if (Integer.valueOf(mTotalSteps) >= Integer.valueOf(steps[1])) {
                    curSteps = String.valueOf(Integer.valueOf(mTotalSteps) - Integer.valueOf(steps[1]));
                } else {
                    curSteps = String.valueOf(Integer.valueOf(mTotalSteps));
                }
            }
        }catch(Exception e){
            curSteps = "0";
        }

        return curSteps;
    }

    public static boolean compareTodayToLastInfo(String oldData) {
        boolean isToday = false;
        String curTime =  getTimeStampLocal();
        String curDate = curTime.substring(0, 8);
        String oldDate = oldData.substring(0, 8);
        if (curDate.equals(oldDate)) {
            isToday = true;
        }
        return isToday;
    }

    public static String getSensorSteps(String path) {
        String temp = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            if (reader != null) {
                try {
                    temp = reader.readLine();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    reader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

}

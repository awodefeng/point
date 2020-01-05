package com.xxun.pointsystem;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xxun.pointsystem.utils.Config;
import com.xxun.pointsystem.utils.LogUtil;
import com.xxun.pointsystem.utils.StepsCountUtils;
import com.xxun.pointsystem.utils.PointSystemUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import android.widget.ImageView;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.xxun.pointsystem.utils.UploadExpUtils;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

public class MainActivity extends Activity {
    RelativeLayout mainLayout;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private float mCurPosX = 0.0f;
    private float mCurPosY = 0.0f;
    private int mTotalExp = 0;
    private int mDailyStep = 0;
    private int mDailyEnglish = 0;
    private int mDailyCallTime = 0;
    private int mDailyStoryTime = 0;
    private int mDailyDurationTime = 0;
    private ImageView mGrade;
    private ImageView mLabel;
    private ImageView mLabelText;
    private ImageView star1;
    private ImageView star2;
    private ImageView star3;
    private String oldSteps = "0";
    private static final String SENSOR_STEPS = "/sys/bus/platform/drivers/gsensor/step_counter_val";
    private XunPointApplication myApplication;
//    private IntentFilter mFilter;
//    private final String ACTION_ZERO_OCLOCK = "com.xiaoxun.statistics.zeroOClock";
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myApplication = (XunPointApplication) getApplication();
        if(myApplication != null){
            myApplication.initExp();
        }
        mContext = this;
        InitView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("myApplication.getGrade() = " + myApplication.getGrade());
        if(myApplication.getGrade() > 0){
            int mProgressValue = ((myApplication.getTotalExp() - myApplication.mExpConfigList.get(myApplication.getGrade() - 1)) * 100)  / (myApplication.mExpConfigList.get(myApplication.getGrade()) - myApplication.mExpConfigList.get(myApplication.getGrade() - 1));
            if(mProgressValue > 0 && mProgressValue <= 33){
                star1.setImageResource(R.drawable.main_star_light);
                star2.setImageResource(R.drawable.main_star_unlight);
                star3.setImageResource(R.drawable.main_star_unlight);
            }else if(mProgressValue > 33 && mProgressValue <= 66){
                star1.setImageResource(R.drawable.main_star_light);
                star2.setImageResource(R.drawable.main_star_light);
                star3.setImageResource(R.drawable.main_star_unlight);
            }else if(mProgressValue > 66){
                star1.setImageResource(R.drawable.main_star_light);
                star2.setImageResource(R.drawable.main_star_light);
                star3.setImageResource(R.drawable.main_star_light);
            }
        }
    }

    private void InitView(){
        LogUtil.i("myApplication.mEnterTime = " + myApplication.mEnterTime +
                "&&" + "myApplication.mExitTime = " + myApplication.mExitTime);

        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        LogUtil.i("mCurPosX = " + mCurPosX);
                        LogUtil.i("mCurPosY = " + mCurPosY);
                        LogUtil.i("mPosX = " + mPosX);
                        LogUtil.i("mPosY = " + mPosY);
                        if ((mPosY - mCurPosY > 8) && Math.abs(mCurPosX - mPosX) < 80) {
                            //向上滑動
                            Intent intent = new Intent(MainActivity.this, StepExchangeActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (mPosX - mCurPosX > 8 && Math.abs(mCurPosY - mPosY) < 80) {
                            //向左滑動
                            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                            startActivity(intent);
                        }
                        else if (mCurPosX - mPosX > 5 && Math.abs(mCurPosY - mPosY) < 80) {
                            //向右滑動
                            myApplication.mExitTime = System.currentTimeMillis();
                            Settings.Global.putLong(getContentResolver(),"exit_time", System.currentTimeMillis());
                            LogUtil.i("myApplication.mExitTime = " + myApplication.mExitTime);
                            LogUtil.i("killed process");
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                        break;
                }
                return true;
            }
        });
        mGrade = (ImageView) findViewById(R.id.grade);
        mLabel = (ImageView) findViewById(R.id.label);
        mLabelText = (ImageView) findViewById(R.id.label_text);

        star1 = (ImageView) findViewById(R.id.star1);
        star2 = (ImageView) findViewById(R.id.star2);
        star3 = (ImageView) findViewById(R.id.star3);

        oldSteps = Settings.Global.getString(getContentResolver(),"step_local");
        mDailyStep = Integer.parseInt(StepsCountUtils.getPhoneStepsByFirstSteps(this, oldSteps, StepsCountUtils.getSensorSteps(SENSOR_STEPS)));
        LogUtil.i("mDailyStep =" + mDailyStep);

        mTotalExp = myApplication.getTotalExp();
        LogUtil.i("MainActivity_mTotalExp =" + mTotalExp);
        mDailyEnglish = myApplication.mEnglishExp;
        try{
            mDailyCallTime = Settings.Global.getInt(getContentResolver(), "call_exp_time");
            mDailyStoryTime = Settings.Global.getInt(getContentResolver(), "story_exp_time");
            mDailyDurationTime = Settings.Global.getInt(getContentResolver(), "duration_exp_time");
        }catch (SettingNotFoundException e){

        }
        LogUtil.i("MainActivity_mDailyCallTime =" + mDailyCallTime);
        LogUtil.i("MainActivity_mDailyStoryTime =" + mDailyStoryTime);
        LogUtil.i("MainActivity_mDailyDurationTime =" + mDailyDurationTime);

        if(!PointSystemUtils.isSameDate(myApplication.mEnterTime + "", myApplication.mExitTime + "")){
            LogUtil.i("MainActivity set first enter true");
            UploadExpUtils.getUploadStatusUtilsInstance().uploadExp(this, myApplication.getTotalExp());
            mLabel.setImageResource(R.drawable.label_step1);
            mLabelText.setImageResource(0);
            myApplication.isClickLuckyBag = false;
        }

        myApplication.setGrade(1);
        if(mTotalExp >= myApplication.mExpConfigList.get(0) && mTotalExp < myApplication.mExpConfigList.get(5)){
            if(mTotalExp >= myApplication.mExpConfigList.get(0) && mTotalExp < myApplication.mExpConfigList.get(1)){
                mGrade.setImageResource(R.drawable.grade1);
                myApplication.setGrade(1);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(1) && mTotalExp < myApplication.mExpConfigList.get(2)){
                mGrade.setImageResource(R.drawable.grade2);
                myApplication.setGrade(2);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(2) && mTotalExp < myApplication.mExpConfigList.get(3)){
                mGrade.setImageResource(R.drawable.grade3);
                myApplication.setGrade(3);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(3) && mTotalExp < myApplication.mExpConfigList.get(4)){
                mGrade.setImageResource(R.drawable.grade4);
                myApplication.setGrade(4);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(4) && mTotalExp < myApplication.mExpConfigList.get(5)){
                mGrade.setImageResource(R.drawable.grade5);
                myApplication.setGrade(5);
            }
            LogUtil.i("1~5");
            mainLayout.setBackgroundResource(R.drawable.main_bg1);
        }else if(mTotalExp >= myApplication.mExpConfigList.get(5) && mTotalExp < myApplication.mExpConfigList.get(10)){
            if(mTotalExp >= myApplication.mExpConfigList.get(5) && mTotalExp < myApplication.mExpConfigList.get(6)){
                mGrade.setImageResource(R.drawable.grade6);
                myApplication.setGrade(6);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(6) && mTotalExp < myApplication.mExpConfigList.get(7)){
                mGrade.setImageResource(R.drawable.grade7);
                myApplication.setGrade(7);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(7) && mTotalExp < myApplication.mExpConfigList.get(8)){
                mGrade.setImageResource(R.drawable.grade8);
                myApplication.setGrade(8);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(8) && mTotalExp < myApplication.mExpConfigList.get(9)){
                mGrade.setImageResource(R.drawable.grade9);
                myApplication.setGrade(9);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(9) && mTotalExp < myApplication.mExpConfigList.get(10)){
                mGrade.setImageResource(R.drawable.grade10);
                myApplication.setGrade(10);
            }
            LogUtil.i("2~10");
            mainLayout.setBackgroundResource(R.drawable.main_bg2);
        }else if(mTotalExp >= myApplication.mExpConfigList.get(10) && mTotalExp < myApplication.mExpConfigList.get(15)){
            if(mTotalExp >= myApplication.mExpConfigList.get(10) && mTotalExp < myApplication.mExpConfigList.get(11)){
                mGrade.setImageResource(R.drawable.grade11);
                myApplication.setGrade(11);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(11) && mTotalExp < myApplication.mExpConfigList.get(12)){
                mGrade.setImageResource(R.drawable.grade12);
                myApplication.setGrade(12);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(12) && mTotalExp < myApplication.mExpConfigList.get(13)){
                mGrade.setImageResource(R.drawable.grade13);
                myApplication.setGrade(13);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(13) && mTotalExp < myApplication.mExpConfigList.get(14)){
                mGrade.setImageResource(R.drawable.grade14);
                myApplication.setGrade(14);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(14) && mTotalExp < myApplication.mExpConfigList.get(15)){
                mGrade.setImageResource(R.drawable.grade15);
                myApplication.setGrade(15);
            }
            LogUtil.i("11~15");
            mainLayout.setBackgroundResource(R.drawable.main_bg3);
        }else if(mTotalExp >= myApplication.mExpConfigList.get(15) && mTotalExp < myApplication.mExpConfigList.get(20)){
            if(mTotalExp >= myApplication.mExpConfigList.get(15) && mTotalExp < myApplication.mExpConfigList.get(16)){
                mGrade.setImageResource(R.drawable.grade16);
                myApplication.setGrade(16);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(16) && mTotalExp < myApplication.mExpConfigList.get(17)){
                mGrade.setImageResource(R.drawable.grade17);
                myApplication.setGrade(17);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(17) && mTotalExp < myApplication.mExpConfigList.get(18)){
                mGrade.setImageResource(R.drawable.grade18);
                myApplication.setGrade(18);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(18) && mTotalExp < myApplication.mExpConfigList.get(19)){
                mGrade.setImageResource(R.drawable.grade19);
                myApplication.setGrade(19);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(19) && mTotalExp < myApplication.mExpConfigList.get(20)){
                mGrade.setImageResource(R.drawable.grade20);
                myApplication.setGrade(20);
            }
            LogUtil.i("16~20");
            mainLayout.setBackgroundResource(R.drawable.main_bg4);
        }else if(mTotalExp >= myApplication.mExpConfigList.get(20) && mTotalExp < myApplication.mExpConfigList.get(25)){
            if(mTotalExp >= myApplication.mExpConfigList.get(20) && mTotalExp < myApplication.mExpConfigList.get(21)){
                mGrade.setImageResource(R.drawable.grade21);
                myApplication.setGrade(21);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(21) && mTotalExp < myApplication.mExpConfigList.get(22)){
                mGrade.setImageResource(R.drawable.grade22);
                myApplication.setGrade(22);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(22) && mTotalExp < myApplication.mExpConfigList.get(23)){
                mGrade.setImageResource(R.drawable.grade23);
                myApplication.setGrade(23);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(23) && mTotalExp < myApplication.mExpConfigList.get(24)){
                mGrade.setImageResource(R.drawable.grade24);
                myApplication.setGrade(24);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(24) && mTotalExp < myApplication.mExpConfigList.get(25)){
                mGrade.setImageResource(R.drawable.grade25);
                myApplication.setGrade(25);
            }
            LogUtil.i("21~25");
            mainLayout.setBackgroundResource(R.drawable.main_bg5);
        }else if(mTotalExp >= myApplication.mExpConfigList.get(25) && mTotalExp < myApplication.mExpConfigList.get(30)){
            if(mTotalExp >= myApplication.mExpConfigList.get(25) && mTotalExp < myApplication.mExpConfigList.get(25)){
                mGrade.setImageResource(R.drawable.grade26);
                myApplication.setGrade(26);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(26) && mTotalExp < myApplication.mExpConfigList.get(27)){
                mGrade.setImageResource(R.drawable.grade27);
                myApplication.setGrade(27);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(27) && mTotalExp < myApplication.mExpConfigList.get(28)){
                mGrade.setImageResource(R.drawable.grade28);
                myApplication.setGrade(28);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(28) && mTotalExp < myApplication.mExpConfigList.get(29)){
                mGrade.setImageResource(R.drawable.grade29);
                myApplication.setGrade(29);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(29) && mTotalExp < myApplication.mExpConfigList.get(30)){
                mGrade.setImageResource(R.drawable.grade30);
                myApplication.setGrade(30);
            }
            LogUtil.i("26~30");
            mainLayout.setBackgroundResource(R.drawable.main_bg6);
        }else if(mTotalExp >= myApplication.mExpConfigList.get(30) && mTotalExp < myApplication.mExpConfigList.get(35)){
            if(mTotalExp >= myApplication.mExpConfigList.get(30) && mTotalExp < myApplication.mExpConfigList.get(31)){
                mGrade.setImageResource(R.drawable.grade31);
                myApplication.setGrade(31);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(31) && mTotalExp < myApplication.mExpConfigList.get(32)){
                mGrade.setImageResource(R.drawable.grade32);
                myApplication.setGrade(32);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(32) && mTotalExp < myApplication.mExpConfigList.get(33)){
                mGrade.setImageResource(R.drawable.grade33);
                myApplication.setGrade(33);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(33) && mTotalExp < myApplication.mExpConfigList.get(34)){
                mGrade.setImageResource(R.drawable.grade34);
                myApplication.setGrade(34);
            }else if(mTotalExp >= myApplication.mExpConfigList.get(34) && mTotalExp < myApplication.mExpConfigList.get(35)){
                mGrade.setImageResource(R.drawable.grade35);
                myApplication.setGrade(35);
            }
            LogUtil.i("31~35");
            mainLayout.setBackgroundResource(R.drawable.main_bg7);
        }
        for(int i = 0;i < 11; i++){
            LogUtil.i("mLabelConfigValueList.get" + i + " "+ myApplication.mLabelConfigValueList.get(i));
        }
        if(mDailyStep >= myApplication.mLabelConfigValueList.get(0) && mDailyStep < myApplication.mLabelConfigValueList.get(1)){
            LogUtil.i("mDailyStep1 = " + mDailyStep);
            mLabel.setImageResource(R.drawable.label_step1);
            mLabelText.setImageResource(R.drawable.label_step1_text);
        }else if(mDailyStep >= myApplication.mLabelConfigValueList.get(1) && mDailyStep < myApplication.mLabelConfigValueList.get(2)){
            LogUtil.i("mDailyStep2 = " + mDailyStep);
            mLabel.setImageResource(R.drawable.label_step1);
            mLabelText.setImageResource(R.drawable.label_step1_text);
        }else if(mDailyStep >= myApplication.mLabelConfigValueList.get(2)){
            LogUtil.i("mDailyStep3 = " + mDailyStep);
            mLabel.setImageResource(R.drawable.label_step1);
            mLabelText.setImageResource(R.drawable.label_step1_text);
        }

        LogUtil.i("MainActivity lucky exp = " + myApplication.getLuckyExp());
        if(myApplication.getLuckyExp() >= myApplication.mLabelConfigValueList.get(3) && myApplication.getLuckyExp() < myApplication.mLabelConfigValueList.get(4)){
            mLabel.setImageResource(R.drawable.label_luck1);
            mLabelText.setImageResource(R.drawable.label_luck1_text);
        }else if(myApplication.getLuckyExp() >= myApplication.mLabelConfigValueList.get(4) && myApplication.getLuckyExp() < myApplication.mLabelConfigValueList.get(5)){
            mLabel.setImageResource(R.drawable.label_luck2);
            mLabelText.setImageResource(R.drawable.label_luck2_text);
        }else if(myApplication.getLuckyExp() == myApplication.mLabelConfigValueList.get(5)){
            mLabel.setImageResource(R.drawable.label_luck3);
            mLabelText.setImageResource(R.drawable.label_luck3_text);
        }

        if(mDailyCallTime >= myApplication.mLabelConfigValueList.get(6) && mDailyCallTime < myApplication.mLabelConfigValueList.get(7)){
            mLabel.setImageResource(R.drawable.label_call1);
            mLabelText.setImageResource(R.drawable.label_call1_text);
        }else if(mDailyCallTime >= myApplication.mLabelConfigValueList.get(7)){
            mLabel.setImageResource(R.drawable.label_call2);
            mLabelText.setImageResource(R.drawable.label_call2_text);
        }

        if(mDailyStoryTime >= myApplication.mLabelConfigValueList.get(8)){
            mLabel.setImageResource(R.drawable.label_story);
            mLabelText.setImageResource(R.drawable.label_story_text);
        }

        if(mDailyDurationTime >= myApplication.mLabelConfigValueList.get(9) && mDailyDurationTime < myApplication.mLabelConfigValueList.get(10)){
            mLabel.setImageResource(R.drawable.label_duration1);
            mLabelText.setImageResource(R.drawable.label_duration1_text);
        }else if(mDailyDurationTime >= myApplication.mLabelConfigValueList.get(10)){
            mLabel.setImageResource(R.drawable.label_duration2);
            mLabelText.setImageResource(R.drawable.label_duration2_text);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i("MainActivity onDestroy");
    }

}

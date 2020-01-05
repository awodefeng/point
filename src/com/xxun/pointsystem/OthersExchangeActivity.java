package com.xxun.pointsystem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.xxun.pointsystem.utils.LogUtil;
import com.xxun.pointsystem.utils.UploadExpUtils;
import android.widget.TextView;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import com.xxun.pointsystem.utils.PointSystemUtils;

public class OthersExchangeActivity extends Activity {
    LinearLayout mainLayout;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private float mCurPosX = 0.0f;
    private float mCurPosY = 0.0f;
    private TextView mDescribe;
    private TextView mCallExp;
    private TextView mStoryExp;
    private TextView mDurationExp;
    private TextView mEnglishExp;
    private Context mContext;
    private XunPointApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.others_exchange);
        myApplication = (XunPointApplication) getApplication();
        mContext = this;
        InitView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void InitView(){
        mainLayout = (LinearLayout) findViewById(R.id.others_exchange_layout);
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
                        if ((mCurPosY - mPosY > 8) && (Math.abs(mCurPosX - mPosX) < 40)) {
                            //向下滑動
                            Intent intent = new Intent(OthersExchangeActivity.this, LuckyBagActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (mPosX - mCurPosX > 8 && Math.abs(mCurPosY - mPosY) < 40) {
                            //向左滑動
                            Intent intent = new Intent(OthersExchangeActivity.this, DetailActivity.class);
                            startActivity(intent);
                        }
                        break;
                }
                return true;
            }
        });

        mDescribe = (TextView) findViewById(R.id.describe);
        try{
            mCallExp = (TextView) findViewById(R.id.call_exp);
            mStoryExp = (TextView) findViewById(R.id.story_exp);
            mDurationExp = (TextView) findViewById(R.id.duration_exp);
            mEnglishExp = (TextView) findViewById(R.id.english_exp);
            LogUtil.i("myApplication.mEnterTime = " + myApplication.mEnterTime +
                    "&&" + "myApplication.mExitTime = " + myApplication.mExitTime);
            if(PointSystemUtils.isSameDate(myApplication.mEnterTime + "", myApplication.mExitTime + "")){
                mCallExp.setText("+" + Settings.Global.getInt(getContentResolver(), "call_exchange_exp"));
                mStoryExp.setText("+" + Settings.Global.getInt(getContentResolver(), "story_exchange_exp"));
                mDurationExp.setText("+" + Settings.Global.getInt(getContentResolver(), "duration_exchange_exp"));
                mEnglishExp.setText("+" + Settings.Global.getInt(getContentResolver(), "english_exchange_exp"));
            }else{
                mCallExp.setText("+" + 0);
                mStoryExp.setText("+" + 0);
                mDurationExp.setText("+" + 0);
                mEnglishExp.setText("+" + 0);
                Settings.Global.putInt(getContentResolver(), "call_exchange_exp", 0);
                Settings.Global.putInt(getContentResolver(), "story_exchange_exp", 0);
                Settings.Global.putInt(getContentResolver(), "duration_exchange_exp", 0);
                Settings.Global.putInt(getContentResolver(), "english_exchange_exp", 0);
            }            
        }catch (SettingNotFoundException e){
            LogUtil.i("SettingNotFoundException = " + e);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

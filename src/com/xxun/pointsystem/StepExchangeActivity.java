package com.xxun.pointsystem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import com.xxun.pointsystem.utils.CircleProgressBar;
import android.provider.Settings;
import android.provider.Settings.Global;
import com.xxun.pointsystem.utils.LogUtil;
import com.xxun.pointsystem.utils.StepsCountUtils;
import com.xxun.pointsystem.utils.Const;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.io.*;
import com.xxun.pointsystem.utils.ToastCustom;
import android.view.KeyEvent;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import com.xxun.pointsystem.utils.PointSystemUtils;

public class StepExchangeActivity extends Activity implements View.OnClickListener{
    RelativeLayout mainLayout;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private float mCurPosX = 0.0f;
    private float mCurPosY = 0.0f;
    private float mUpPosX = 0.0f;
    private float mUpPosY = 0.0f;
    private ImageView mExchangeBtn;
    private ImageView mStepExchangeStar;
    private TextView dailyStep;
    private Toast mToast;
    private ToastCustom mToastCustom;
    private CircleProgressBar mCircle;
    private String oldSteps = "0";
    private int curSteps = 0;
    private int exchangeSteps = 0;//可以兑换的步数
    private int exchangedSteps;//已经兑换的步数
    private int TOTAL_STEP_COUNT = 10000;
    private static final String SENSOR_STEPS = "/sys/bus/platform/drivers/gsensor/step_counter_val";
    private int STEP_EXCHANGE_LIMIT = 3000;//暂定，后续会修改
    private int STEP_EXCHANGE_RANGE1 = 3000;
    private int STEP_EXCHANGE_RANGE2 = 5000;
    private int STEP_EXCHANGE_RANGE3 = 6000;
    private int STEP_EXCHANGE_RANGE4 = 8000;
    private int STEP_EXCHANGE_MAX_RANGE = 10000;
    private XunPointApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_exchange);
        myApplication = (XunPointApplication) getApplication();
        InitView();
    }

    private void InitView(){
        mExchangeBtn = (ImageView) findViewById(R.id.exchange_btn);
        mStepExchangeStar = (ImageView) findViewById(R.id.step_exchange_star);
        mCircle = (CircleProgressBar) findViewById(R.id.circleProgressBar);
        dailyStep = (TextView) findViewById(R.id.total_steps);
        mainLayout = (RelativeLayout) findViewById(R.id.step_exchange_layout);
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
                        mUpPosX = event.getX();
                        mUpPosY = event.getY();
                        LogUtil.i("StepExchange_mCurPosX = " + mCurPosX);
                        LogUtil.i("StepExchange_mCurPosY = " + mCurPosY);
                        LogUtil.i("StepExchange_mPosX = " + mPosX);
                        LogUtil.i("StepExchange_mPosY = " + mPosY);
                        LogUtil.i("StepExchange_mUpPosX = " + mUpPosX);
                        LogUtil.i("StepExchange_mUpPosY = " + mUpPosY);
                        //*/ xiaoxun.zhangweinan, 20190405. click
                        if(Math.abs(mUpPosX - mPosX) < 5
                                && Math.abs(mUpPosY - mPosY) < 5
                                && mPosX > 10 && mPosX < 240 && mPosY > 150 && mPosY < 220){
                            if(curSteps == 0){
                                showToast(getString(R.string.no_step_exchange_toast));
                            }else if(curSteps - exchangedSteps > 0 && curSteps - exchangedSteps < STEP_EXCHANGE_LIMIT){
                                showToast(getString(R.string.no_enough_step_exchange_toast));
                            }else if(curSteps - exchangedSteps >= STEP_EXCHANGE_LIMIT){
                                if(curSteps  > STEP_EXCHANGE_MAX_RANGE){
                                    showToast(getString(R.string.step_exchange_full_toast));
                                }else {
                                    exchangeWithSteps(curSteps - exchangedSteps);
                                    exchangeSteps = 0;
                                    exchangedSteps = curSteps;
                                    mCircle.setProgress(0);
                                    Settings.Global.putInt(getContentResolver(),"exchangedSteps", exchangedSteps);
                                    mExchangeBtn.setImageResource(R.drawable.no_step_exchage_btn);
                                }
                            }
                        }
                        //*/
                        if ((mCurPosY - mPosY > 8) && (Math.abs(mCurPosX - mPosX) < 80)) {
                            //向下滑動
                            Intent intent = new Intent(StepExchangeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (mPosX - mCurPosX > 8 && Math.abs(mCurPosY - mPosY) < 80) {
                            //向左滑動
                            Intent intent = new Intent(StepExchangeActivity.this, DetailActivity.class);
                            startActivity(intent);
                        } else if ((mPosY - mCurPosY > 8) && (Math.abs(mCurPosX - mPosX) < 80)) {
                            //向上滑動
                            Intent intent = new Intent(StepExchangeActivity.this, LuckyBagActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        break;
                }
                return true;
            }
        });
        oldSteps = Settings.Global.getString(getContentResolver(),"step_local");
        LogUtil.i("oldSteps:" + oldSteps);
        curSteps = Integer.parseInt(StepsCountUtils.getPhoneStepsByFirstSteps(this, oldSteps, StepsCountUtils.getSensorSteps(SENSOR_STEPS)));
        LogUtil.i("curSteps:" + curSteps);
        dailyStep.setText(curSteps + ""); 
        try{
            exchangedSteps = Settings.Global.getInt(getContentResolver(), "exchangedSteps");
        }catch (SettingNotFoundException e){

        }
        if(!PointSystemUtils.isSameDate(myApplication.mEnterTime + "", myApplication.mExitTime + "")){
            LogUtil.i("set exchangedSteps to 0 when after a day");
            Settings.Global.putInt(getContentResolver(),"exchangedSteps", 0);
        }

        mToastCustom = new ToastCustom(getApplicationContext(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int mStep = curSteps - exchangedSteps;
        LogUtil.i("mStep:" + mStep);
        if(curSteps <= STEP_EXCHANGE_MAX_RANGE){
            if(mStep >= 0){
                if(mStep >= STEP_EXCHANGE_LIMIT){
                    mStepExchangeStar.setVisibility(View.VISIBLE);
                    mCircle.setProgress(mStep/100);
                    mCircle.setOutsideColor(getColor(R.color.sandybrown));
                    mExchangeBtn.setImageResource(R.drawable.step_exchage_btn);
                }else{
                    mStepExchangeStar.setVisibility(View.GONE);
                    mCircle.setProgress(mStep/100);
                    mCircle.setOutsideColor(getColor(R.color.gray_cc));
                    mExchangeBtn.setImageResource(R.drawable.no_step_exchage_btn);
                }
            }
        }else {
            mStepExchangeStar.setVisibility(View.VISIBLE);
            mCircle.setProgress(100);
            mCircle.setOutsideColor(getColor(R.color.gray_cc));
            mExchangeBtn.setImageResource(R.drawable.no_step_exchage_btn);
        }
    }

    @Override
    public void onClick(View v) {
//        if (v == mExchangeBtn) {
//            if(curSteps == 0){
//                showToast(getString(R.string.no_step_exchange_toast));
//            }else if(curSteps - exchangedSteps > 0 && curSteps - exchangedSteps < STEP_EXCHANGE_LIMIT){
//                showToast(getString(R.string.no_enough_step_exchange_toast));
//            }else if(curSteps - exchangedSteps >= STEP_EXCHANGE_LIMIT){
//                if(curSteps  > STEP_EXCHANGE_MAX_RANGE){
//                    showToast(getString(R.string.step_exchange_full_toast));
//                }else {
//                    exchangeWithSteps(curSteps - exchangedSteps);
//                    exchangeSteps = 0;
//                    exchangedSteps = curSteps;
//                    mCircle.setProgress(0);
//                    Settings.Global.putInt(getContentResolver(),"exchangedSteps", exchangedSteps);
//                }
//            }
//            mExchangeBtn.setImageResource(R.drawable.no_step_exchage_btn);
//        }
    }

    private void exchangeWithSteps(int step){
        if(step >= STEP_EXCHANGE_RANGE1 && step <= STEP_EXCHANGE_RANGE2){
            showToast(getString(R.string.step_exchange_toast, 1));
            myApplication.setStepExp(1);
            LogUtil.i("setStepExp1");
        }else if(step > STEP_EXCHANGE_RANGE2 && step <= STEP_EXCHANGE_RANGE3){
            showToast(getString(R.string.step_exchange_toast, 2));
            myApplication.setStepExp(2);
            LogUtil.i("setStepExp2");
        }else if(step > STEP_EXCHANGE_RANGE3 && step <= STEP_EXCHANGE_RANGE4){
            showToast(getString(R.string.step_exchange_toast, 3));
            myApplication.setStepExp(3);
            LogUtil.i("setStepExp3");
        }else if(step > STEP_EXCHANGE_RANGE4 && step <= STEP_EXCHANGE_MAX_RANGE){
            showToast(getString(R.string.step_exchange_toast, 4));
            myApplication.setStepExp(4);
            LogUtil.i("setStepExp4");
        }else if(step > STEP_EXCHANGE_MAX_RANGE){
            showToast(getString(R.string.step_exchange_toast, 4));
            myApplication.setStepExp(4);
            LogUtil.i("setStepExp4 extra");
        }
    }

    private void showToast(String toast){
        ToastCustom.makeText(getApplicationContext(), this, toast, ToastCustom.LENGTH_SHORT).show();
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

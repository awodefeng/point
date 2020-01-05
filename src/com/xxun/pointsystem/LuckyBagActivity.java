package com.xxun.pointsystem;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.xxun.pointsystem.utils.LogUtil;
import java.util.Random;
import com.xxun.pointsystem.utils.GifTempletView;
import android.os.Handler;
import com.xxun.pointsystem.utils.ToastCustom;
import android.view.KeyEvent;
import com.xxun.pointsystem.utils.PointSystemUtils;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;

public class LuckyBagActivity extends Activity {
    RelativeLayout mainLayout;
    ImageView mBag;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private float mCurPosX = 0.0f;
    private float mCurPosY = 0.0f;
    private float mUpPosX = 0.0f;
    private float mUpPosY = 0.0f;
    private Toast mToast;
    private ToastCustom mToastCustom;
    Random mRandom = new Random();
    GifTempletView gifTempletView;
    Handler mhandler = new Handler();
    private boolean isFirst;
    //private boolean isClicked = true;
    private XunPointApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lucky_bag);
        myApplication = (XunPointApplication) getApplication();
        try{
            isFirst = Settings.Global.getInt(getContentResolver(), "is_first_enter_daily") == 1 ? true : false;
        }catch (SettingNotFoundException e){
            LogUtil.i("LuckyBagActivity SettingNotFoundException = " + e);
            isFirst = true;
        }
        LogUtil.i("isFirst = " + isFirst);
        if(isFirst){
            myApplication.isClickLuckyBag = false;
        }else {
            myApplication.isClickLuckyBag = true;
        }
        InitView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int randomExp(){
        int number = mRandom.nextInt(100);
        LogUtil.i("number = " + number);
        gifTempletView.setVisibility(View.VISIBLE);
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifTempletView.setVisibility(View.GONE);
            }
        }, 3000);
        if(number >= 0  && number < 27){
            gifTempletView.setMovieResource(R.raw.lucky_bag_open1);
            return 1;
        }else if(number >= 27  && number < 54){
            gifTempletView.setMovieResource(R.raw.lucky_bag_open1);
            return 2;
        }else if (number >= 54  && number < 80) {
            gifTempletView.setMovieResource(R.raw.lucky_bag_open1);
            return 3;
        }else if (number >= 80  && number < 87) {
            gifTempletView.setMovieResource(R.raw.lucky_bag_open2);
            return 4;
        }else if (number >= 87  && number < 95) {
            gifTempletView.setMovieResource(R.raw.lucky_bag_open2);
            return 5;
        }else if (number >= 95  && number < 100) {
            gifTempletView.setMovieResource(R.raw.lucky_bag_open3);
            return 6;
        }              
        return 0; 
    }

    private void InitView(){
        mainLayout = (RelativeLayout) findViewById(R.id.lucky_bag_layout);
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
                        LogUtil.i("LuckyBag_mCurPosX = " + mCurPosX);
                        LogUtil.i("LuckyBag_mCurPosY = " + mCurPosY);
                        LogUtil.i("LuckyBag_mPosX = " + mPosX);
                        LogUtil.i("LuckyBag_mPosY = " + mPosY);
                        LogUtil.i("LuckyBag_mUpPosX = " + mPosX);
                        LogUtil.i("LuckyBag_mUpPosY = " + mPosY);
                        LogUtil.i("myApplication.isClickLuckyBag = " + myApplication.isClickLuckyBag);
                        if(Math.abs(mUpPosX - mPosX) < 5
                                && Math.abs(mUpPosY - mPosY) < 5
                                && mPosX > 30 && mPosX < 200 && mPosY > 30 && mPosY < 200){
                            if(!myApplication.isClickLuckyBag){
                                myApplication.isClickLuckyBag = true;
                                Settings.Global.putInt(getContentResolver(),"is_first_enter_daily", 0);
                                int randomExp = randomExp();
                                showToast(randomExp);
                                myApplication.setLuckyExp(randomExp);
                            }else{
                                ToastCustom.makeText(getApplicationContext(), LuckyBagActivity.this, getString(R.string.lucky_bag_exchange_full_toast), ToastCustom.LENGTH_SHORT).show();
                            }
                        }
                        if ((mCurPosY - mPosY > 8) && (Math.abs(mCurPosX - mPosX) < 80)) {
                            //向下滑動
                            Intent intent = new Intent(LuckyBagActivity.this, StepExchangeActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (mPosX - mCurPosX > 8 && Math.abs(mCurPosY - mPosY) < 80) {
                            //向左滑動
                            Intent intent = new Intent(LuckyBagActivity.this, DetailActivity.class);
                            startActivity(intent);
                        } else if ((mPosY - mCurPosY > 8) && (Math.abs(mCurPosX - mPosX) < 80)) {
                            //向上滑動
                            Intent intent = new Intent(LuckyBagActivity.this, OthersExchangeActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (mCurPosX - mPosX > 0) {
                            //向右滑动
                            LogUtil.i("forbid slide right");
                            return false;
                        }
                        break;
                }
                return true;
            }
        });

        gifTempletView = (GifTempletView)findViewById(R.id.open_bag);
        mToastCustom = new ToastCustom(getApplicationContext(), this);
    }   

    private void showToast(int exp){
        ToastCustom.makeText(getApplicationContext(), this, getString(R.string.lucky_bag_exchange_toast, exp), ToastCustom.LENGTH_SHORT).show();

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

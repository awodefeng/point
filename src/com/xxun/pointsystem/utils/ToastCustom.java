package com.xxun.pointsystem.utils;
import android.content.Context;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.view.View;
import android.os.Handler;
import com.xxun.pointsystem.R;

public class ToastCustom {

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    Toast toast;
    Context mContext;
    TextView toastTextField;

    public ToastCustom(Context context, Activity activity) {
        mContext = context;
        toast = new Toast(mContext);
        toast.setGravity(Gravity.CENTER, 0, 0);
        View toastRoot = activity.getLayoutInflater().inflate(R.layout.toast_view, null);
        toastTextField = (TextView) toastRoot.findViewById(R.id.toast_text);
        toast.setView(toastRoot);
    }

    public void setDuration(int d) {
        toast.setDuration(d);
    }

    public void setText(String t) {
        toastTextField.setText(t);
    }

    public static ToastCustom makeText(Context context, Activity activity, String text, int duration) {
        ToastCustom toastCustom = new ToastCustom(context, activity);
        toastCustom.setText(text);
        toastCustom.setDuration(duration);
        return toastCustom;
    }

    public void show() {
        toast.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cancel();
            }
        }, 1000);
    }

    public void cancel() {
        toast.cancel();
    }
}

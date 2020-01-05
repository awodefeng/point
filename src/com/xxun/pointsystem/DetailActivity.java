package com.xxun.pointsystem;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;
import com.xxun.pointsystem.jason.Contacts.SyncArrayBean;
import com.xxun.pointsystem.jason.every.contact.DeviceInfo;
import java.util.ArrayList;
import android.os.AsyncTask;
import java.util.Collections;
import java.util.Comparator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import com.xxun.pointsystem.imageCache.AsyncImageLoader;
import com.xxun.pointsystem.imageCache.FileCache;
import com.xxun.pointsystem.imageCache.ImageUtil;
import com.xxun.pointsystem.imageCache.MemoryCache;
import com.xxun.pointsystem.utils.CircleDrawable;
import java.io.File;
import com.xxun.pointsystem.utils.LogUtil;
import com.xxun.pointsystem.utils.PointSystemUtils;
import android.widget.ProgressBar;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.view.KeyEvent;
import org.json.JSONObject;
import org.json.JSONException;
import com.xiaoxun.sdk.XiaoXunNetworkManager;
import com.xiaoxun.sdk.ResponseData;
import com.xiaoxun.sdk.IResponseDataCallBack;
import org.json.JSONException;
import android.os.Handler;

public class DetailActivity extends Activity {
    RelativeLayout mainLayout;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private float mCurPosX = 0.0f;
    private float mCurPosY = 0.0f;
    ListView mListView;
    ListAdapter mListAdapter;
    List<SyncArrayBean> mlist_arraybean  = new ArrayList<SyncArrayBean>();
    private TextView mNoRank;
    private Context mContext;
    private String[]  project= new String[]{"mimetype","data1","data2","data3","data4","data5","data6","data7"
            ,"data8","data9","data10","data11","data12"
            ,"data13","data14","data15"};  
    private List<SyncArrayBean> mdata = null;              
    private AsyncImageLoader imageLoader;
    LoadData mloadData = null;
    ProgressBar mProgress;
    private XunPointApplication myApplication;
    private TextView mLeftGrade;
    private TextView mRightGrade;
    private TextView mDescribe1;
    private TextView mDescribe2;
    private TextView mDescribe3;
    private TextView mMyPoint;
    private int mProgressValue = 0;
    private TextView mName;
    private DeviceInfo mDeviceInfo = null;
    private XiaoXunNetworkManager mxiaoXunNetworkManager = null;
    private String kid_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        mContext = this;
        myApplication = (XunPointApplication) getApplication();
        if(mloadData == null){
            mloadData = new LoadData();
        }
        mloadData.execute();
        PointSystemUtils.readExpConfigFromXml(this);        
        InitView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            kid_name = Settings.Global.getString(getContentResolver(),"kid_name");
            mName.setText(kid_name);
        }catch (Exception e){
            LogUtil.i("DetailActivity JSONException e = " + e);
        }
    }

    private void InitView(){
        mainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        mListView = (ListView) findViewById(R.id.listview);
        mNoRank = (TextView) findViewById(R.id.rank_no_statics);
        mProgress = (ProgressBar) findViewById(R.id.progesss);
        mLeftGrade = (TextView) findViewById(R.id.level_left);
        mRightGrade = (TextView) findViewById(R.id.level_right);
        mMyPoint = (TextView) findViewById(R.id.mypoint);
        mMyPoint.setText(myApplication.getTotalExp() + "");
        mName = (TextView) findViewById(R.id.myname);
        mDescribe1 = (TextView) findViewById(R.id.decribe1);
        mDescribe2 = (TextView) findViewById(R.id.decribe2);
        mDescribe3 = (TextView) findViewById(R.id.decribe3);
        mDescribe1.setText(getString(R.string.total_point, myApplication.getTotalExp()));
        mDescribe2.setText(getString(R.string.covert_point_from_step, myApplication.getStepExp()));
        mDescribe3.setText(getString(R.string.obtain_point_from_bag, myApplication.getLuckyExp()));
        if(myApplication != null){
            mLeftGrade.setText("LV" + myApplication.getGrade());
            mRightGrade.setText("LV" + (myApplication.getGrade() + 1));
        }
        LogUtil.i("total exp = " + myApplication.getTotalExp());
        if(myApplication.getGrade() > 0){
            mProgressValue = ((myApplication.getTotalExp() - myApplication.mExpConfigList.get(myApplication.getGrade() -1)) * 100)  / (myApplication.mExpConfigList.get(myApplication.getGrade()) - myApplication.mExpConfigList.get(myApplication.getGrade() -1));
        }else{
            mProgressValue = 0;
        }
        LogUtil.i("mProgressValue = " + mProgressValue);
        mProgress.setProgress(mProgressValue);
    }

    private class LoadData extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            getData();
            getDeviceInfo();
            publishProgress(100);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
                if(progresses[0] == 100){
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if(kid_name != null){
                                mName.setText(kid_name);
                            }
                        }
                    }, 500);
                    LogUtil.i("kid_name1 = " + kid_name);
                    LogUtil.i("mlist_arraybean.size() = " + mlist_arraybean.size());
                    if(mlist_arraybean.size() > 0) {
                        mListAdapter = new MyRankAdapter(mContext, mlist_arraybean);
                        mListView.setAdapter(mListAdapter);
                    }else{
                        mNoRank.setVisibility(View.VISIBLE);
                        mNoRank.setText(getString(R.string.rank_no_statics));
                    }
              }
        }
    }

    private void getData(){
        mlist_arraybean.clear();
        LogUtil.i("getData");
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = getApplicationContext().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        if(cursor != null){
                while(cursor.moveToNext()){
                    int contactsId = cursor.getInt(0);
                    uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data");
                    Cursor dataCursor = resolver.query(uri, project, null, null, null);
                    SyncArrayBean  arrayBean = new SyncArrayBean();
                    while(dataCursor.moveToNext()) {
                        String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));
                        if ("vnd.android.cursor.item/name".equals(type)) {
                             arrayBean.id = dataCursor.getString(dataCursor.getColumnIndex(project[1]));
                             arrayBean.name = dataCursor.getString(dataCursor.getColumnIndex(project[2]));
                             arrayBean.avatar = dataCursor.getString(dataCursor.getColumnIndex(project[3]));
                             arrayBean.exp = dataCursor.getString(dataCursor.getColumnIndex(project[4]));
                             LogUtil.i("arrayBean.name = " + arrayBean.name);
                             LogUtil.i("arrayBean.avatar = " + arrayBean.avatar);
                             LogUtil.i("arrayBean.exp = " + arrayBean.exp);
                        }else if ("vnd.android.cursor.item/nickname".equals(type)) {
                            arrayBean.contactsType = dataCursor.getInt(dataCursor.getColumnIndex(project[1]));
                            LogUtil.i("arrayBean.contactsType_n = " + arrayBean.contactsType);
                        }
                    }
                    if(arrayBean != null && arrayBean.id != null && arrayBean.contactsType == 2){
                            LogUtil.i("mlist_arraybean.add +++++++++++++++++");
                            mlist_arraybean.add(arrayBean);
                    }
                }
                cursor.close();
        }

    }

    public void sort(List<SyncArrayBean> data) {
        Collections.sort(data, new Comparator<SyncArrayBean>() {
            public int compare(SyncArrayBean o1, SyncArrayBean o2) {
                Integer a =  Integer.parseInt(o1.exp);
                Integer b =  Integer.parseInt(o2.exp);
                return b.compareTo(a);
            }
        });
    }

    private class MyRankAdapter extends BaseAdapter {

        private List<Message> Datas;
        private Context mContext;

        public MyRankAdapter(Context context, List<SyncArrayBean> mdata_update) {
            mContext = context;
            MemoryCache mcache=new MemoryCache();//内存缓存
            File sdCard = android.os.Environment.getExternalStorageDirectory();//获得SD卡
            File cacheDir = new File(sdCard, "xiaoxun_cache" );//缓存根文件夹
            FileCache fcache = new FileCache(mContext, cacheDir, "photo_img");//文件缓存
            imageLoader = new AsyncImageLoader(mContext, mcache,fcache);            
            mdata = mdata_update;
            sort(mdata);

        }

        /**
         * 返回item的个数
         *
         * @return
         */
        @Override
        public int getCount() {
            LogUtil.i("friends list size == " + mlist_arraybean.size());
            return mlist_arraybean.size();
        }

        /**
         * 返回每一个item对象
         *
         * @param i
         * @return
         */
        @Override
        public Object getItem(int i) {
            return mlist_arraybean.size();
        }

        /**
         * 返回每一个item的id
         *
         * @param i
         * @return
         */
        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final TrackViewHolder trackHolder = new TrackViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.rank_list_item, viewGroup, false);
            String url = mdata.get(i).avatar;
            Bitmap bitmap = null;            
            trackHolder.itemLayout = (RelativeLayout) view.findViewById(R.id.layout_album_item);
            trackHolder.itemAvatarHead = (ImageView) view.findViewById(R.id.avatar_head);
            trackHolder.itemAvatar = (ImageView) view.findViewById(R.id.avatar);
            trackHolder.itemName = (TextView) view.findViewById(R.id.friends_name);
            trackHolder.itemPointRank = (TextView) view.findViewById(R.id.point_rank);
            trackHolder.itemPointRankImg = (ImageView) view.findViewById(R.id.point_rank_img);
            if(i == 0){
                trackHolder.itemAvatarHead.setVisibility(View.VISIBLE);
                trackHolder.itemPointRankImg.setVisibility(View.VISIBLE);
                trackHolder.itemPointRank.setVisibility(View.GONE);
                trackHolder.itemAvatarHead.setImageResource(R.drawable.rank_1);
                trackHolder.itemPointRankImg.setImageResource(R.drawable.rank_1_point);
            }else if(i == 1){
                trackHolder.itemAvatarHead.setVisibility(View.VISIBLE);
                trackHolder.itemPointRankImg.setVisibility(View.VISIBLE);
                trackHolder.itemPointRank.setVisibility(View.GONE);
                trackHolder.itemAvatarHead.setImageResource(R.drawable.rank_2);
                trackHolder.itemPointRankImg.setImageResource(R.drawable.rank_2_point);
            }else if(i == 2){
                trackHolder.itemAvatarHead.setVisibility(View.VISIBLE);
                trackHolder.itemPointRankImg.setVisibility(View.VISIBLE);
                trackHolder.itemPointRank.setVisibility(View.GONE);
                trackHolder.itemAvatarHead.setImageResource(R.drawable.rank_3);
                trackHolder.itemPointRankImg.setImageResource(R.drawable.rank_3_point);
            }else{
                trackHolder.itemAvatarHead.setVisibility(View.GONE);
                trackHolder.itemPointRank.setText(i + "");
            }
            if(url != null){
                    bitmap = imageLoader.getLocalBitmap(url.contains("https")?url:url.replace("http","https"));
                    if(bitmap != null){
                            LogUtil.i("bitmap = " + bitmap);
                            trackHolder.itemAvatar.setImageDrawable(new CircleDrawable(bitmap));
                    }else{
                            LogUtil.i("bitmap = null");  
                    }
            }else{
                   LogUtil.i("url = null"); 
            }
            trackHolder.itemName.setText(mdata.get(i).name);

            return view;
        }

        class TrackViewHolder {
            RelativeLayout itemLayout;
            ImageView itemAvatarHead;
            ImageView itemAvatar;
            TextView itemName;
            TextView itemPointRank;
            ImageView itemPointRankImg;
        }

    }

    private void getDeviceInfo() {
        if(mxiaoXunNetworkManager == null) mxiaoXunNetworkManager = (XiaoXunNetworkManager)getSystemService("xun.network.Service");
        mxiaoXunNetworkManager.getDeviceInfo(mxiaoXunNetworkManager.getWatchEid(),new mDeviceInfoCallBack() {
            @Override
            public void onSuccess(ResponseData responseData) {
                mDeviceInfo = DeviceInfo.objectFromData(responseData.getResponseData());
                JSONObject Json = new JSONObject();
                LogUtil.i("mDeviceInfo.NickName = " + mDeviceInfo.NickName);
                if(mDeviceInfo.NickName != null){
                    kid_name = mDeviceInfo.NickName;
                    Settings.Global.putString(getContentResolver(), "kid_name", kid_name);
                    LogUtil.i("kid_name = " + kid_name);
                }
            }
            @Override
            public void onError(int i, String s) {
                LogUtil.i("onError = " + s);
            }
        });
    }

    public class mDeviceInfoCallBack extends IResponseDataCallBack.Stub{
        @Override
        public void onSuccess(ResponseData responseData) {}
        @Override
        public void onError(int i, String s) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

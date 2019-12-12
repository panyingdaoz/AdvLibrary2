package com.kingbird.library.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.kingbird.library.R;
import com.kingbird.library.base.Base;
import com.kingbird.library.base.PermissionsActivity;
import com.kingbird.library.jsonbean.BaiDuAdvParam;
import com.kingbird.library.jsonbean.StartParam;
import com.kingbird.library.litepal.AddJingDongAdv;
import com.kingbird.library.litepal.Parameter;
import com.kingbird.library.litepal.PlayList;
import com.kingbird.library.manager.CustomActivityManager;
import com.kingbird.library.manager.ExecutorServiceManager;
import com.kingbird.library.manager.ProtocolManager;
import com.kingbird.library.manager.SocketManager;
import com.kingbird.library.manager.ThreadManager;
import com.kingbird.library.service.MonitorService;
import com.kingbird.library.utils.AreaAveragingScale;
import com.kingbird.library.utils.Const;
import com.kingbird.library.utils.GlideUtil;
import com.kingbird.library.utils.MacUtil;
import com.kingbird.library.utils.NetUtil;
import com.kingbird.library.utils.SharedPreferencesUtils;
import com.kuaifa.ad.KuaiFaClient;
import com.kuaifa.ad.entry.DeviceEntry;
import com.kuaifa.ad.entry.DeviceNetworkEntry;
import com.kuaifa.ad.entry.DeviceUDIDEntry;
import com.kuaifa.ad.entry.ScreenSizeEntry;
import com.kuaifa.ad.result.GetAdResult;
import com.kuaifa.ad.value.MaterialType;
import com.kuaifa.ad.value.NetworkOperatorType;
import com.socks.library.KLog;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tsy.sdk.myokhttp.MyOkHttp;
import com.tsy.sdk.myokhttp.response.DownloadResponseHandler;
import com.tsy.sdk.myokhttp.response.JsonResponseHandler;
import com.tsy.sdk.myokhttp.response.RawResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.os.Build.VERSION_CODES.M;
import static com.kingbird.library.base.Base.addParameterData;
import static com.kingbird.library.base.Base.checkMd5;
import static com.kingbird.library.base.Base.dataQuery;
import static com.kingbird.library.base.Base.fileIsExists;
import static com.kingbird.library.base.Base.fileNameQuery;
import static com.kingbird.library.base.Base.getActivity;
import static com.kingbird.library.base.Base.getQuery;
import static com.kingbird.library.base.Base.isAccessibilitySettingsOn;
import static com.kingbird.library.base.Base.isFirstStart;
import static com.kingbird.library.base.Base.readBaiDuShow;
import static com.kingbird.library.base.Base.readIntervalTimeShow;
import static com.kingbird.library.base.Base.readJingDongShow;
import static com.kingbird.library.base.Base.readLocalFile;
import static com.kingbird.library.base.Base.removeFile;
import static com.kingbird.library.base.Base.setCount;
import static com.kingbird.library.base.Base.setIntentData;
import static com.kingbird.library.base.Base.showReport;
import static com.kingbird.library.base.Base.showSum;
import static com.kingbird.library.base.Base.timeTrigger;
import static com.kingbird.library.base.Base.updateDownloadCount;
import static com.kingbird.library.base.Base.updateDownloadState;
import static com.kingbird.library.base.Base.updateDownloadState2;
import static com.kingbird.library.base.Base.vipShowSum;
import static com.kingbird.library.utils.Config.ADD_JINGDONG;
import static com.kingbird.library.utils.Config.APK_CHECK;
import static com.kingbird.library.utils.Config.APPID;
import static com.kingbird.library.utils.Config.APPKEY;
import static com.kingbird.library.utils.Config.BAIDU_IS_PLAYED;
import static com.kingbird.library.utils.Config.CONSTANT_ELEVEN;
import static com.kingbird.library.utils.Config.CONSTANT_FIFTEEN;
import static com.kingbird.library.utils.Config.CONSTANT_FORTY;
import static com.kingbird.library.utils.Config.CONSTANT_ONE;
import static com.kingbird.library.utils.Config.CONSTANT_ONE_THOUSAND;
import static com.kingbird.library.utils.Config.CONSTANT_TEN;
import static com.kingbird.library.utils.Config.CONSTANT_THREE;
import static com.kingbird.library.utils.Config.CONSTANT_TWO;
import static com.kingbird.library.utils.Config.DELETE_PLAYED_MEDIA;
import static com.kingbird.library.utils.Config.DEVICE_TIME;
import static com.kingbird.library.utils.Config.DOMAIN_NAME;
import static com.kingbird.library.utils.Config.DOMAIN_NAME2;
import static com.kingbird.library.utils.Config.END_JINGDONG;
import static com.kingbird.library.utils.Config.FILE_SAVE_URL;
import static com.kingbird.library.utils.Config.GET_BAIDU_AD;
import static com.kingbird.library.utils.Config.GET_REDPACKET_AMOUNT;
import static com.kingbird.library.utils.Config.GET_START_PARAM;
import static com.kingbird.library.utils.Config.ILLEGAL_LOGO_URR;
import static com.kingbird.library.utils.Config.INITIAL_HERATBEAT;
import static com.kingbird.library.utils.Config.INITIAL_IP;
import static com.kingbird.library.utils.Config.INITIAL_PORT;
import static com.kingbird.library.utils.Config.JINGDONG_APP_HOST;
import static com.kingbird.library.utils.Config.JINGDONG_APP_ID;
import static com.kingbird.library.utils.Config.JINGDONG_APP_KEY;
import static com.kingbird.library.utils.Config.JINGDONG_CODE;
import static com.kingbird.library.utils.Config.JINGDONG_CODE1;
import static com.kingbird.library.utils.Config.JINGDONG_CODE2;
import static com.kingbird.library.utils.Config.JINGDONG_CODE3;
import static com.kingbird.library.utils.Config.JINGDONG_REPORT;
import static com.kingbird.library.utils.Config.PACKAGE_NAME;
import static com.kingbird.library.utils.Config.REDPACKET_AMOUNT;
import static com.kingbird.library.utils.Config.ROOT_DIRECTORY_URL;
import static com.kingbird.library.utils.Config.SAVE_BAIDU_LOG;
import static com.kingbird.library.utils.Config.SET_DEVICE_PARAM;
import static com.kingbird.library.utils.Config.START_JINGDONG;
import static com.kingbird.library.utils.Config.SUCCESS;
import static com.kingbird.library.utils.Config.SUCCESS2;
import static com.kingbird.library.utils.Config.USER_NAME;
import static com.kingbird.library.utils.Const.AD_MATERIAL_ID;
import static com.kingbird.library.utils.Const.LOGO_SIZE;
import static com.kingbird.library.utils.Const.QR_SIZE;
import static com.kingbird.library.utils.Const.VOICE_CONTENT;
import static com.kingbird.library.utils.Const.VOICE_ENABLE;
import static com.kingbird.library.utils.NetUtil.isNetConnected;
import static com.kingbird.library.utils.Plog.e;
import static com.kingbird.library.utils.SharedPreferencesUtils.readString;

/**
 * @ClassName: AdvertistingView  TextureView
 * @Description: java类作用描述
 * @Author: Pan
 * @CreateDate: 2019/11/11 10:11
 */
public class AdvertistingView extends FrameLayout {

    private Context context;
    private Activity activity;
    private static final String MY_BROADCAST_TAG = "tcpServerReceiver";
    private String startParamUrl;
    private String logoUrl;
    private int deviceAppType, startCount = 0;
    private int isRedPacket;
    private int jdType = 0;
    private boolean logoShow, qrCodeShow, redPacket, hasRedPacket;
    private IntentFilter filter;
    private String mUri;
    private String currentPlayId;
    private String stopRequestId;
    private int deviceScreenWidth;
    private int deviceScreenHeight;
    private int screenRatio;
    private int showSumSize;
    private int imgeCount;

    private VideoView mVideoView;
    private ImageView mAppLogo;
    private ImageView mCoverView;
    private ImageView mZxing;
    private ImageView mImage;
    private ImageView mRedPacket;
    private TextView mText;
    private GifImageView mGifImage;

    private AudioManager mAudio;
    public MonitorService monitorService;
    private GifDrawable gifFromAssets;
    private RequestOptions optionsLogo;
    private RequestOptions optionsRedPacket;
    private RequestOptions optionsImage2;
    private RequestOptions optionsStandby;
    private int redPacketMoney, hasRedPacketMoney;
    private String imagePath;

    private static boolean mIsStopped = false;
    private boolean isExistence;
    private boolean isVisible = true;
    private boolean isJdStart = true;
    private boolean mIsNewPlay = true;
    private boolean mIsVip = false;
    private boolean isJson = true;
    private boolean isRequestBaiDu = true, isRequestJd;
    private boolean isPlayIntervalTimeShow;
    private int jdCount;
    private long jdQuestTime = 0;
    private int index = 0;
    private String rCode;
    private String showId;
    private int count = 0;
    private List<PlayList> qery;
    private List<String> time;
    private Toast toast;

    private static String endJdPlay;
    private static ArrayList<String> currentJdPlay;

    private List<String> names = Arrays.asList("JPG", "JPEG", "PNG", "GIF");
    private byte[] command = new byte[1];
    private byte[] number = new byte[1];

    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    private static ArrayList<String> deleteFailure = new ArrayList<>();
    ArrayList<Integer> showSumList = new ArrayList<>();
    ArrayList<String> downloadList = new ArrayList<>();
    ArrayList<Integer> showIntervalTimeList = new ArrayList<>();
    private MyOkHttp mMyOkHttp;
    private MediaPlayer mMediaPlayer;

    public AdvertistingView(@NonNull Context context) {
        super(context);
    }

    public AdvertistingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvertistingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void initAdvertising(Activity activity) {

        this.activity = activity;
        Base.setActivity(activity);

        if (Build.VERSION.SDK_INT >= M) {
            if (SharedPreferencesUtils.readBoolean(activity, Const.PERMISSONS)) {
                startAdvertisting();
                e("已获得相关权限");
            } else {
                Intent intent = new Intent(activity, PermissionsActivity.class);
                activity.startActivity(intent);
                e("开始申请权限");
            }
        } else {
            SharedPreferencesUtils.writeBoolean(activity, Const.PERMISSONS, true);
            startAdvertisting();
            e("不需要申请");
        }
    }

    public void startAdvertisting() {
        createFile();
        if (activity == null) {
            activity = getActivity();
        }
        if (isFirstStart(getActivity())) {
            addLitepal();
        }
        init(getActivity());
        String cpuId = readString(activity, Const.CPU_ID);
        if (cpuId.isEmpty()) {
            cpuId = MacUtil.getCpuId(activity);
            SharedPreferencesUtils.writeString(activity, Const.CPU_ID, cpuId);
        } else {
            e("CPUID已存在：" + cpuId);
        }
        CustomActivityManager.getInstance().setTopActivity(activity);

        initView(activity);
    }

    /**
     * 控件初始化
     */
    private void init(Activity activity) {
        SharedPreferencesUtils.writeInt(activity, Const.RESTART_APP_TIME, 30);
        mAudio = (AudioManager) activity.getSystemService(Service.AUDIO_SERVICE);

        View view = LayoutInflater.from(activity).inflate(R.layout.constraint, this, true);

        mVideoView = view.findViewById(R.id.VideoView);
        mImage = view.findViewById(R.id.ImagePlay);
        mRedPacket = view.findViewById(R.id.RedPacket);
        mGifImage = view.findViewById(R.id.GifImage);
        mZxing = view.findViewById(R.id.ZXing);
        mAppLogo = view.findViewById(R.id.AppLogo);
        mText = view.findViewById(R.id.DeviceId);
        mCoverView = view.findViewById(R.id.CoverView);

        optionsImage2 = new RequestOptions();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        mMyOkHttp = new MyOkHttp(okHttpClient);
    }

    /**
     * 创建文件件
     */
    private void createFile() {
        File appDir = new File(ROOT_DIRECTORY_URL + PACKAGE_NAME);
        if (!appDir.exists()) {
            boolean isSuccess = appDir.mkdirs();
            e("创建情况：" + isSuccess);
            Parameter add = new Parameter();
            add.setFileUrl(appDir.toString());
            add.save();
            isExistence = false;
        } else {
            isExistence = true;
            setIntentData("fileUrl", appDir.getAbsolutePath());
        }
    }

    /**
     * 创建数据库并添加初始值
     */
    private void addLitepal() {//添加参数表
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                //获取MAC地址
                String mac = MacUtil.getMac(activity);
                if (!TextUtils.isEmpty(mac)) {
                    e("网络MAC地址：" + mac);
                    SharedPreferencesUtils.writeString(activity, Const.MAC, mac);
                }

                String deviceId = mac.substring(1, 12);
                if (isExistence) {
                    addParameterData(deviceId);
                } else {
                    setIntentData("deviceID", deviceId);
                    setIntentData("rCode", "1111");
                    setIntentData("ip", INITIAL_IP);
                    setIntentData("port", INITIAL_PORT);
                    setIntentData("protocolType", 1);
                    setIntentData("startPlayType", 2);
                    setIntentData("startPlayUrl", "0.mp4");
                    setIntentData("playType", 1);
                    setIntentData("playUrl", "rtmp://live.hkstv.hk.lxdns.com/live/hks");
                    setIntentData("isResult", 2);
                    setIntentData("startHour", 6);
                    setIntentData("startMinute", 0);
                    setIntentData("endHour", 23);
                    setIntentData("endMinute", 59);
                    setIntentData("isUploading", 2);
                    setIntentData("heartBeat", INITIAL_HERATBEAT);
                    setIntentData("screenSize", 1);
                    setIntentData("decodingWay", 1);
                    setIntentData("networkType", 1);
                    setIntentData("applicationType", 1);
                    setIntentData("uniqueness", "Pan");
                }
            }
        });
    }

    private void initView(final Activity activity) {
        initVodeo(activity);
        readLocalFile();
        intervalTimeInspect();
        updateShowList();

        Intent intent = new Intent(activity, MonitorService.class);
        activity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        bindReceiver();
        playInitialize();
        activity.registerReceiver(myBroadcastReceiver, filter);
        getScreenWidthHeigth();
        videoPlay();
        queryLitepal();
        getStartParam();
        if (!isAccessibilitySettingsOn(activity)) {
            ExecutorServiceManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(R.string.accessibility_remind);
                        }
                    });
                }
            }, 2, TimeUnit.SECONDS);
        }
    }

    private void initVodeo(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //让视频全屏播放
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        //屏幕常亮
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public String getQrCodePath() {
        String saveDir = null;
        List<Parameter> query = LitePal.findAll(Parameter.class);
        for (Parameter parameter : query) {
            String fileName = "qr" + parameter.getDeviceId() + ".jpg";
            saveDir = FILE_SAVE_URL + fileName;
        }
        return saveDir;
    }

    public String getLongitude() {
        return readString(activity, Const.LONGITUDE);
    }

    public String getLatitude() {
        return readString(activity, Const.LATITUDE);
    }

    public void setAppPackageName(String packageName) {
        SharedPreferencesUtils.writeString(activity, Const.MAIN_APP_NAME, packageName);
    }

    public void bindDealer(String code) {
        Base.bindDealer(context, code);
    }

    public void videoStop() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mIsStopped = true;
        }
    }

    public void videoPause() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
            mIsStopped = false;
        }
    }

    public void videoStart() {
        String videoName = ".MP4", videoName2 = ".MOV", videoName3 = ".mp4", videoName4 = ".mov";
        if (mIsStopped) {
            List<Parameter> query = LitePal.findAll(Parameter.class);
            for (Parameter parameter : query) {
                String videoPath;
                String playUrl = parameter.getPlayUrl();
                if (playUrl == null) {
                    e("获取开机路径播放");
                    assignLoop();
                } else {
                    e("获取实时路径播放：" + playUrl);
                    if (playUrl.endsWith(videoName) || playUrl.endsWith(videoName2) || playUrl.endsWith(videoName3) || playUrl.endsWith(videoName4)) {
                        videoPath = FILE_SAVE_URL + playUrl;
                        if (fileIsExists(videoPath)) {
                            localPlay(videoPath);
                        }
                    } else {
                        localPlay(playUrl);
                    }
                }
            }
        } else {
            e("执行直接播放");
            mVideoView.start();
        }
    }

    public String getDeviceId() {
        String deviceId = null;
        List<Parameter> query = LitePal.findAll(Parameter.class);
        for (Parameter parameter : query) {
            deviceId = parameter.getDeviceId();
        }
        return deviceId;
    }

    public void stop() {
        if (activity != null) {
            try {
                if (conn != null) {
                    activity.unbindService(conn);
                }
                if (mVideoView != null) {
                    mVideoView.stopPlayback();
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                activity.unregisterReceiver(myBroadcastReceiver);
                SocketManager.getInstance().close();
                ThreadManager.getInstance().shutdown();
                ExecutorServiceManager.getInstance().shutdown();
                if (isTimeReceiver) {
                    activity.unregisterReceiver(mTimeReceiver);
                }

                mMyOkHttp.cancel(context);
                GlideUtil.glieClear(activity, mAppLogo);
                GlideUtil.glieClear(activity, mCoverView);
                GlideUtil.glieClear(activity, mZxing);
                GlideUtil.glieClear(activity, mImage);
                GlideUtil.glieClear(activity, mRedPacket);
                GlideUtil.glieClear(activity, mGifImage);

                context = null;
            } catch (Exception e) {
                e("异常原因：" + e.toString());
            }
        }
        System.exit(0);
        android.os.Process.killProcess(Process.myPid());
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            if (mAction != null) {
                if (MY_BROADCAST_TAG.equals(mAction)) {
                    String msg = intent.getStringExtra(MY_BROADCAST_TAG);
                    e("传过来的值：" + msg);
                    Message message = Message.obtain();
                    assert msg != null;
                    message.what = Integer.parseInt(msg);
                    message.obj = msg;
                    myHandler.sendMessage(message);
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        private WeakReference<AdvertistingView> Advertisting;

        private MyHandler(AdvertistingView activity) {
            Advertisting = new WeakReference<>(activity);
        }

        /**
         * @param msg msg.what的值分别表示：0：暂停1：停止2：播放3：远程控制实时播放4：清单控制播放、上一个、下一个
         *            5：字幕控制6:退出程序7:开关机控制8：音量控制
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            AdvertistingView activity = Advertisting.get();
            List<Parameter> parameters = LitePal.findAll(Parameter.class);
            for (Parameter parameter : parameters) {
                String id = parameter.getDeviceId();
                if (activity != null) {
                    command[0] = (byte) 0x81;
                    number[0] = (byte) 0xA0;
                    int msgWhat = msg.what;
                    if (msgWhat < 10) {
                        handleMessage2(msg, parameter, id);
                    } else {
                        handleMessage3(msg, id);
                    }
                }
            }
        }

        private void handleMessage2(Message msg, Parameter parameter, String id) {
            switch (msg.what) {
                case 0:
                    e("远程控制暂停");
                    controlPause(parameter, id);
                    break;
                case 1:
                    e("远程控制停止");
                    stopPlay();
                    controlPlayAnswer(id, parameter.getrCode(), command, number, 1, 1);
                    break;
                case 2:
                    e("远程控制播放");
                    controlPlay(parameter, id);
                    break;
                case 3:
                    assignPlay(parameter, id);
                    break;
                case 4:
                    int playType = SharedPreferencesUtils.readInt(activity, Const.SPLIT_SCREEN);
                    e("播放类型：" + playType);
                    if (playType == 1) {
                        String controlNumber = readString(activity, Const.CONTROL_NUMBER);
                        String ad = "AD";
                        if (ad.equals(controlNumber)) {
                            updateShowList();
                        }
                        showOperation(parameter, id);
                    }
                    break;
                case 5:
                    startAdvertisting();
                    break;
                case 6:
                    activity.finish();
                    break;
                case 8:
                    int volume = SharedPreferencesUtils.readInt(activity, Const.VOLUME);
                    volumeControl(mAudio, volume);
                    break;
                case 9:
                    int qrSize = SharedPreferencesUtils.readInt(activity, QR_SIZE);
                    qrCodeShow2(id, qrSize);
                    appLogoInitialize();
                    break;
                case 10:
                    appLogoInitialize();
                    break;
                default:
                    break;
            }
        }

        private void showOperation(Parameter parameter, String id) {
            command[0] = (byte) 0x85;
            String controlNumber = readString(activity, Const.CONTROL_NUMBER);
            if (showSumSize != 0) {
                switch (controlNumber) {
                    case "AD":
                        imgeCount = 0;
                        number[0] = (byte) 0xAD;
                        // 播放监听
                        boolean isShowPlay = SharedPreferencesUtils.readBoolean(activity, Const.SHOW_PLAY);
                        e("是否播放：" + isShowPlay);
                        String fileName = parameter.getPlayUrl();
                        List<PlayList> querShow = fileNameQuery("playId", fileName);
                        for (PlayList querShows : querShow) {
                            currentPlayId = Integer.toString(querShows.getPlayId());
                            closeAssetMusics();
                            if (isShowPlay) {
                                showPlay(fileName, false);
                            } else if (mIsNewPlay) {
                                mIsNewPlay = false;
                                showPlay(fileName, false);
                                SharedPreferencesUtils.writeBoolean(activity, Const.SHOW_PLAY, false);
                            }
                        }
                        break;
                    case "B0":
                        previousShow();
                        number[0] = (byte) 0xB0;
                        break;
                    case "B1":
                        nextShow();
                        number[0] = (byte) 0xB1;
                        break;
                    default:
                        break;
                }
                controlPlayAnswer(id, parameter.getrCode(), command, number, 0, 1);
            } else {
                controlPlayAnswer(id, parameter.getrCode(), command, number, 0, 0);
            }
        }

        private void assignPlay(Parameter parameter, String id) {
            command[0] = (byte) 0x82;
            number[0] = (byte) 0xA1;
            stopPlay();
            playInitialize();
            String playurl = parameter.getPlayUrl();
            e("直播地址：" + playurl);
            switch (parameter.getPlayType()) {
                case 1:
                    setIntentData("playUrl", playurl);
                    localPlay(playurl);
                    controlPlayAnswer(id, parameter.getrCode(), command, number, 1, 1);
                    break;
                case 2:
                    setIntentData("playUrl", playurl);
                    localPlay(playurl);
                    controlPlayAnswer(id, parameter.getrCode(), command, number, 2, 1);
                    break;
                case 3:
                    setIntentData("playUrl", playurl);
                    localPlay(playurl);
                    controlPlayAnswer(id, parameter.getrCode(), command, number, 3, 1);
                    break;
                case 4:
                    String path = parameter.getFileUrl() + "/" + playurl;
                    e("本地地址：" + path);
                    if (fileIsExists(path)) {
                        setIntentData("playUrl", playurl);
                        localPlay(path);
                        controlPlayAnswer(id, parameter.getrCode(), command, number, 4, 1);
                    } else {
                        controlPlayAnswer(id, parameter.getrCode(), command, number, 4, 0);
                    }
                    break;
                default:
                    break;
            }
        }

        private void controlPlay(Parameter parameter, String id) {
            String videoName = ".mp4", videoName2 = ".mov", videoName3 = ".MP4", videoName4 = ".MOV";
            if (mIsStopped) {
                String videoPath;
                String playUrl = parameter.getPlayUrl();
                if (playUrl == null) {
                    e("获取开机路径播放");
                    assignLoop();
                } else {
                    e("获取实时路径播放：");
                    if (playUrl.endsWith(videoName) || playUrl.endsWith(videoName2) || playUrl.endsWith(videoName3) || playUrl.endsWith(videoName4)) {
                        videoPath = FILE_SAVE_URL + playUrl;
                        if (fileIsExists(videoPath)) {
                            localPlay(videoPath);
                        } else {
                            controlPlayAnswer(id, parameter.getrCode(), command, number, 2, 0);
                        }
                    } else {
                        localPlay(playUrl);
                    }
                }
            } else {
                e("执行直接播放");
                mVideoView.start();
            }
            controlPlayAnswer(id, parameter.getrCode(), command, number, 2, 1);
        }

        private void controlPause(Parameter parameter, String id) {
            if (mVideoView.isPlaying()) {
                mVideoView.pause();
                mIsStopped = false;
                if (!mVideoView.isPlaying()) {
                    controlPlayAnswer(id, parameter.getrCode(), command, number, 0, 1);
                } else {
                    controlPlayAnswer(id, parameter.getrCode(), command, number, 0, 0);
                }
            }
        }

        private void controlPlayAnswer(final String id, final String rCode, final byte[] command, final byte[] number, final int playType, final int isResult) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    String controlNumber = readString(activity, Const.CONTROL_TYPE);
                    switch (controlNumber) {
                        case "LAN":
                            e("配置软件控制回应");
                            ProtocolManager.getInstance().controlAnswer(id, rCode, command, number, playType, "0000", isResult, "client");
                            break;
                        case "INTERNET":
                            ProtocolManager.getInstance().controlAnswer(id, rCode, command, number, playType, "0000", isResult, "");
                            break;
                        default:
                            break;
                    }
                }
            });
        }

    }

    /**
     * handleMessage 3分支
     */
    private void handleMessage3(Message msg, final String id) {
        switch (msg.what) {
            case 13:
                isRequestBaiDu = true;
                baiDuPare(id);
                break;
            case 14:
                if (isNetConnected(context)) {
                    int qrSize = SharedPreferencesUtils.readInt(activity, QR_SIZE);
                    qrCodeShow2(id, qrSize);
                }
                break;
            case 19:
                updateShowList();
                break;
            case 20:
                showIntervalTimeList = readIntervalTimeShow();
                break;
            case 21:
                int logoSize = SharedPreferencesUtils.readInt(activity, LOGO_SIZE);
                int qrSize = SharedPreferencesUtils.readInt(activity, QR_SIZE);
                String url = readString(activity, Const.LOGO_URL);
                setLogoGlideSize(logoSize);
                logoShow(url);
                qrCodeShow2(id, qrSize);
                break;
//            case 22:
//                voiceControl(id, rCode, true, parameter.getPlayUrl());
//                break;
            case 23:
                isRequestJd = SharedPreferencesUtils.readBoolean(activity, Const.IS_START_JINGDONG);
                getAdDemo(id);
                break;
            case 25:
                appLogoInitialize();
                break;
            case 103:
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast(R.string.qrCode_failure);
                    }
                });
            default:
        }
    }

    /**
     * 广播绑定
     */
    private void bindReceiver() {
        filter = new IntentFilter(MY_BROADCAST_TAG);
        filter.addAction(MY_BROADCAST_TAG);
    }

    /**
     * 设置屏参获取、设定
     */
    private void getScreenWidthHeigth() {
        int screenHeightReaPacket, screenWidthReaPacket;
        Display display1 = activity.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display1.getSize(outSize);
        deviceScreenWidth = outSize.x;
        deviceScreenHeight = outSize.y;
//        Plog.e("屏的分辨率：："+ outSize.x + "*" + outSize.y);
        String model = Build.MODEL;
        SharedPreferencesUtils.writeString(activity, Const.DEVICE_MODEL, model);
        BuglyLog.e("设备型号", model);
        SharedPreferencesUtils.writeInt(activity, Const.SPLIT_WIDTH, deviceScreenHeight);
        SharedPreferencesUtils.writeInt(activity, Const.SPLIT_HEIGTH, deviceScreenHeight);
        e("屏的分辨率：：" + deviceScreenWidth + "*" + deviceScreenHeight);
        if (deviceScreenWidth > deviceScreenHeight) {
            //大二维码比例为5，正常比例为6，横屏
            screenWidthReaPacket = deviceScreenHeight / 5;
            screenHeightReaPacket = deviceScreenHeight / 5;
            screenRatio = deviceScreenHeight;
            e("横屏：" + screenRatio);
            SharedPreferencesUtils.writeInt(activity, Const.DEVICEID_TYPE, 1);
            SharedPreferencesUtils.writeInt(activity, Const.SCREENOR_ORIENT, 1);
        } else {
            //门禁比例为8,正常比例为8，竖屏
            screenRatio = deviceScreenWidth;
            e("竖屏：" + screenRatio);
            screenWidthReaPacket = deviceScreenWidth / 6;
            screenHeightReaPacket = deviceScreenWidth / 6;
            SharedPreferencesUtils.writeInt(activity, Const.DEVICEID_TYPE, 2);
        }

        int logoSize = SharedPreferencesUtils.readInt(activity, LOGO_SIZE);
        setLogoGlideSize(logoSize);

        optionsRedPacket = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(screenWidthReaPacket, screenHeightReaPacket);
    }

    /**
     * 设备数据查询
     */
    public void queryLitepal() {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                List<Parameter> query = LitePal.findAll(Parameter.class);
                for (Parameter parameter : query) {
                    String deviceId = parameter.getDeviceId();
                    e("查询区号：" + parameter.getrCode());
                    final String ipAddress = parameter.getIp();
                    int port = parameter.getPort();
                    int startPlayType = parameter.getStartPlayType();
                    e("查询IP：" + ipAddress);
                    e("查询端口：" + port);
                    e("查询协议类型：" + parameter.getProtocolType());
                    e("查询开始播放类型：" + startPlayType);
                    e("查询开始播放路径：" + parameter.getStartPlayUrl());
                    e("查询实时播放类型：" + parameter.getPlayType());
                    e("查询实时播放路径：" + parameter.getPlayUrl());
                    e("开关机是否有效：" + parameter.getIsResult());
                    e("开机时间：" + parameter.getStartHour() + ":" + parameter.getStartMinute());
                    e("关机时间：" + parameter.getEndHour() + ":" + parameter.getEndMinute());
                    e("心跳周期：" + parameter.getHeartBeat());
                    e("屏尺寸：" + parameter.getScreenSize());
                    e("解码方式：" + parameter.getDecodingWay());
                    e("应用类型：" + parameter.getApplicationType());
                    BuglyLog.e("设备ID", deviceId);
                    BuglyLog.e("查询IP", ipAddress);
                    BuglyLog.e("查询端口", Integer.toString(port));
                    BuglyLog.e("查询开始播放类型", Integer.toString(startPlayType));
                    BuglyLog.e("查询开始播放路径", parameter.getStartPlayUrl());
                    BuglyLog.e("查询实时播放路径", parameter.getPlayUrl());
                    BuglyLog.e("应用类型", Integer.toString(parameter.getApplicationType()));

                    UdpView.getInstance().connectUdp(ipAddress);

                    if (deviceId == null) {
                        if (isNetConnected(context)) {
                            int result = LitePal.deleteAll(Parameter.class);
                            e("删除Parameter表结果：" + result);
                            if (result > 0) {
                                String mac = MacUtil.getMac(context);
                                if (!TextUtils.isEmpty(mac)) {
                                    String id = mac.substring(1, 12);
                                    addParameterData(id);
                                }
                            }
                        }
                    }

                    deviceDataReport(parameter, ipAddress, port);

                    try {
                        if (deleteFailure.size() > 0) {
                            for (int i = 0; i < deleteFailure.size(); i++) {
                                programmeControl(deleteFailure.get(i), deviceId);
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    /**
     * 给web上传设备参数
     */
    private void deviceDataReport(final Parameter parameter, final String ipAddress, final int port) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject userJson = setJsonUser();
                    JSONObject deviceJson = new JSONObject();
                    deviceJson.put("deviceId", parameter.getDeviceId());
                    deviceJson.put("appVersion", APK_CHECK);
                    deviceJson.put("screenWidth", deviceScreenWidth);
                    deviceJson.put("screenHeight", deviceScreenHeight);
                    deviceJson.put("ipAddress", ipAddress);
                    deviceJson.put("port", port);

                    final JSONObject postJson = new JSONObject();
                    postJson.put("user", userJson);
                    postJson.put("param", deviceJson);

                    Base.webPostReport(postJson, SET_DEVICE_PARAM);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设备特殊参数获取
     */
    private void getStartParam() {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                List<Parameter> parameterList = LitePal.findAll(Parameter.class);
                for (Parameter parameterLists : parameterList) {
                    final String deviceId = parameterLists.getDeviceId();
                    startParamUrl = GET_START_PARAM + deviceId;
                    e("开机获取数据路径：" + startParamUrl);
                    mMyOkHttp.get()
                            .url(startParamUrl)
                            .tag(this)
                            .enqueue(new RawResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, String response) {
                                    startParamParser(response, deviceId);
                                }

                                @Override
                                public void onFailure(int statusCode, String errorMsg) {
                                    e("设备初始数据获取失败原因:" + errorMsg);
                                    startCount++;
                                    if (startCount < 3) {
                                        getStartParam();
                                    } else {
                                        try {
                                            deviceAppType = SharedPreferencesUtils.readInt(activity, Const.DEVICE_APP_TYPE);
                                            logoShow = SharedPreferencesUtils.readBoolean(activity, Const.LOGO_SHOW);
                                            qrCodeShow = SharedPreferencesUtils.readBoolean(activity, Const.QR_CODE_SHOW);
                                            hasRedPacket = SharedPreferencesUtils.readBoolean(activity, Const.HAS_READPECKET);
                                            logoUrl = readString(activity, Const.LOGO_URL);
                                            e("加载二维码显示");
                                            showOperation(deviceId);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                }
            }


        });
    }

    /**
     * 开机参数解析
     */
    private void startParamParser(final String response, final String deviceId) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    StartParam startParam = StartParam.objectFromData(response);
                    e("获取数据结果：" + response + "\n数据长度" + response.length());
                    boolean requestResult = startParam.isSuccess();
                    e("访问结果：" + requestResult);
                    if (requestResult || response.length() > CONSTANT_FORTY) {
                        String qrCodeUrl = startParam.getQrCodeUrl();
                        e("二维码路径：" + qrCodeUrl);
                        logoUrl = (String) startParam.getLogoUrl();
                        e("logo路径：" + logoUrl);
                        String landscapeUrl = (String) startParam.getLandscapeUrl();
                        e("横屏图片路径：" + landscapeUrl);
                        String portraitUrl = (String) startParam.getPortraitUrl();
                        e("竖屏图片路径：" + portraitUrl);
                        deviceAppType = startParam.getDeviceAppType();
                        e("设备类型：" + deviceAppType);
                        logoShow = startParam.isLogoShow();
                        e("是否显示logo：" + logoShow);
                        qrCodeShow = startParam.isQrCodeShow();
                        e("是否显示二维码：" + qrCodeShow);
                        hasRedPacket = startParam.isHasRedPacket();
                        e("整点红包路径：" + hasRedPacket);

                        SharedPreferencesUtils.writeString(activity, Const.LOGO_URL, logoUrl);
                        SharedPreferencesUtils.writeInt(activity, Const.DEVICE_APP_TYPE, deviceAppType);
                        SharedPreferencesUtils.writeBoolean(activity, Const.LOGO_SHOW, logoShow);
                        SharedPreferencesUtils.writeBoolean(activity, Const.QR_CODE_SHOW, qrCodeShow);
                        SharedPreferencesUtils.writeBoolean(activity, Const.HAS_READPECKET, hasRedPacket);
                        if (landscapeUrl != null) {
                            SharedPreferencesUtils.writeString(activity, Const.LANDSCAPE_URL, landscapeUrl);
                            e("横存储");
                        }
                        if (portraitUrl != null) {
                            e("竖存储");
                            SharedPreferencesUtils.writeString(activity, Const.PORTRAIT_URL, portraitUrl);
                        }

                        if (showSumSize == 0 && isVisible) {
                            standbyVisibility(landscapeUrl, portraitUrl);
                        }
                        showOperation(deviceId);
                    } else {
                        deviceAppType = SharedPreferencesUtils.readInt(activity, Const.DEVICE_APP_TYPE);
                        logoShow = SharedPreferencesUtils.readBoolean(activity, Const.LOGO_SHOW);
                        qrCodeShow = SharedPreferencesUtils.readBoolean(activity, Const.QR_CODE_SHOW);
                        hasRedPacket = SharedPreferencesUtils.readBoolean(activity, Const.HAS_READPECKET);
                        logoUrl = readString(activity, Const.LOGO_URL);
                        String lUrl = readString(activity, Const.LANDSCAPE_URL);
                        String pUrl = readString(activity, Const.PORTRAIT_URL);

                        if (showSumSize == 0 && isVisible) {
                            standbyVisibility(lUrl, pUrl);
                        }
                        showOperation(deviceId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    e("异常原因：" + e.toString());
                }
            }
        });
    }

    @SuppressLint("ShowToast")
    public void showToast(int msg) {
        if (toast != null) {
            toast.setText(msg);
        } else {
            toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /**
     * 节目播放
     */
    private void showPlay(@NonNull final String videoPath, boolean isSpeakText) {
        final String videoPaths = FILE_SAVE_URL + videoPath;
        String fileType = videoPath.substring(videoPath.lastIndexOf(".") + 1);
        //红包广告操作
        redPacketOperation(videoPath);

        if (fileIsExists(videoPaths)) {
            videoPlay(videoPath, videoPaths, fileType, isSpeakText, true, true);
        } else {
            List<PlayList> playType = getQuery("playType", "fileName", videoPath);
            e("数组：" + playType.size());
            for (PlayList playTypes : playType) {
                final int isLive = playTypes.getPlayType();
                e("文件类型：" + isLive);
                if (1 == isLive || 2 == isLive || 3 == isLive) {
                    e("直播地址和类型：" + videoPath + ", " + isLive);
                    count = 0;
                    setImageGone();
                    imageGone();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            mIsNewPlay = true;
                            mVideoView.setVisibility(View.VISIBLE);
                            mVideoView.setVideoPath(videoPath);
                        }
                    });
                } else {
                    e("重新下载文件");
                    if (showSumSize > 0) {
                        if (!downloadList.contains(videoPath)) {
                            downloadList.add(videoPath);
                            fileMissingDownload(videoPath);
                        }
                        isJson = false;
                        nextShow();
                    }
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            List<Parameter> idList = LitePal.findAll(Parameter.class);
                            for (Parameter idLists : idList) {
                                command[0] = (byte) 0x85;
                                number[0] = (byte) 0xAD;
                                ProtocolManager.getInstance().controlAnswer(idLists.getDeviceId(), idLists.getrCode(),
                                        command, number, isLive, showId, 0, "");
                            }
                        }
                    });
                }
            }
            if (playType.size() == 0 && showSumSize > 0) {
                e("文件错误！");
                nextShow();
            }
            if (showSumSize == 0) {
                assignLoop();
            }
        }
    }

    /**
     * 本地播放
     */
    public void localPlay(String videoPath) {
        setImageGone();
        imageGone();
        mVideoView.setVideoPath(videoPath);
        mVideoView.start();
    }

    /**
     * 循环播放
     */
    private void assignLoop() {
        List<Parameter> startPlayFile = LitePal.findAll(Parameter.class);
        for (Parameter parameter : startPlayFile) {
            String startUrl = parameter.getStartPlayUrl();
            final String videoPaths = FILE_SAVE_URL + startUrl;
            if (fileIsExists(videoPaths)) {
                imageGone();
                String fileType = startUrl.substring(startUrl.lastIndexOf(".") + 1);
                String enableVoice = readString(activity, VOICE_ENABLE);
                boolean isSpeakText = false;
                switch (enableVoice) {
                    case "00":
                        isSpeakText = false;
                        break;
                    case "01":
                        isSpeakText = true;
                        break;
                    default:
                }
                isVisible = false;
                videoPlay(startUrl, videoPaths, fileType, isSpeakText, false, false);
            } else {
                setImageGone();
                e("无视频或者图片资源");
                e("是否正在播放：" + mVideoView.isPlaying());
                if (!mVideoView.isPlaying()) {
                    stopPlay();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            RequestOptions options = new RequestOptions()
                                    .centerCrop()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE);

                            int scrren = SharedPreferencesUtils.readInt(activity, Const.DEVICEID_TYPE);
                            if (scrren == 1) {
                                GlideUtil.load(activity, R.drawable.landscape, options, mCoverView);
                                mCoverView.setVisibility(View.VISIBLE);
                            } else {
                                GlideUtil.load(activity, R.drawable.portrait, options, mCoverView);
                                mCoverView.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    // 2019-5-21 去掉待机图获取
                    post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(R.string.play_remind);
                        }
                    });
                }
            }

            getJingDongAd();
        }
    }

    /**
     * 节目播放
     */
    private void videoPlay() {
        setIntentData("playType", 4);
        e("当前清单数量：" + showSumSize);
        if (showSumSize > 0) {
            e("下标值：" + index);
            getJingDongAd();
            playVideo();
        } else {
            assignLoop();
        }
    }

    /**
     * 节目播放
     */
    private void videoPlay(@NonNull String videoPath, final String videoPaths, String
            fileType, boolean isSpeakText, boolean isRedPacket, boolean isStart) {
        String videoType = "MP4";
        if (isRedPacket) {
            e("播放是的currentPlayId：" + currentPlayId);
            if (currentPlayId != null && isJdStart) {
                List<PlayList> advType = dataQuery("isBaiDu", currentPlayId);
                for (PlayList advTypes : advType) {
                    int type = advTypes.getIsBaiDu();
                    e("广告类型：" + type);
                    jdType = type;
                    if (type == 2) {
                        playControl(videoPath, videoPaths, fileType, isSpeakText, videoType, 1, isStart);
                        List<PlayList> requestIdList = dataQuery("showName", currentPlayId);
                        for (PlayList requestIdLists : requestIdList) {
                            List<PlayList> startJdUrl = dataQuery("jdStartUrl", currentPlayId);
                            for (PlayList startJdUrls : startJdUrl) {
                                List<PlayList> stopJdUrl = dataQuery("jdStopUrl", currentPlayId);
                                for (PlayList stopJdUrls : stopJdUrl) {
                                    List<PlayList> jdUrlList = dataQuery("jdUrlList", currentPlayId);
                                    for (PlayList jdUrlLists : jdUrlList) {
                                        endJdPlay = stopJdUrls.getJdStopUrl();
                                        currentJdPlay = jdUrlLists.getJdUrlList();
                                    }
                                }
                                setCount(currentPlayId, 1);
                                String requestId = requestIdLists.getShowName();
                                e("获取到的mediaID start：" + requestId);
                                stopRequestId = requestId;
                                String url = START_JINGDONG + "?requestId=" + requestId + "&ticks=" + System.currentTimeMillis() / 1000;
                                e("上报路径 tart：" + url);
                                e("上报路径 京东 tart：" + startJdUrls.getJdStartUrl());
                                startJingDong(url);
                                startJingDong(startJdUrls.getJdStartUrl());
                            }
                        }
                    } else if (type == 0) {
                        List<PlayList> redPacket1 = dataQuery("redPacket", currentPlayId);
                        for (PlayList redPackets : redPacket1) {
                            final int redPacket = redPackets.getRedPacket();
                            playControl(videoPath, videoPaths, fileType, isSpeakText, videoType, redPacket, isStart);
                        }
                    } else if (type == 1) {
                        playControl(videoPath, videoPaths, fileType, isSpeakText, videoType, 1, isStart);
                    }
                }
                if (advType.size() == 0) {
                    e("获取类型失败");
                    playControl(videoPath, videoPaths, fileType, isSpeakText, videoType, 1, isStart);
                }
            } else if (!isJdStart) {
                e("软件启动播放");
                playControl(videoPath, videoPaths, fileType, isSpeakText, videoType, 1, isStart);
            }
        } else {
            playControl(videoPath, videoPaths, fileType, isSpeakText, videoType, 1, isStart);
        }
    }

    /**
     * 节目播放控制
     */
    private void playControl(@NonNull String videoPath, final String videoPaths, String
            fileType, boolean isSpeakText, String videoType, final int redPacket, boolean isStart) {
        String videoType2 = "MOV";
        if (videoType.contains(fileType.toUpperCase()) || videoType2.contains(fileType.toUpperCase())) {
            e("本地视频");
            setImageGone();
            imageGone();
            count = 0;
            mVideoView.post(new Runnable() {
                @Override
                public void run() {
                    imgeCount = 0;
                    if (redPacket != CONSTANT_TWO || redPacketMoney == 0) {
                        closeAssetMusics();
                    }
                    playInitialize();
//                    KLog.e("线程：");
                    startPlayVideo(videoPaths);
                }
            });
        } else if (names.contains(fileType.toUpperCase())) {
            e("图片");
            count = 0;
            imagePlay(videoPath, fileType, isStart);
            if (isSpeakText) {
                String text = readString(activity, VOICE_CONTENT);
                e("获取的text：" + text + ", " + isRedPacket);
                if (!TextUtils.isEmpty(text)) {
                    e("文件：" + videoPath);
                    if (redPacket != CONSTANT_TWO || redPacketMoney == 0) {
                        imgeCount++;
                        e("imgeCount：" + imgeCount);
                        if (imgeCount == 1) {
                            e("开始");
                            openAssetMusics("hongbao-tts.mp3");
                        } else if (imgeCount > 1 && imgeCount != CONSTANT_TWO) {
                            e("开始");
                            imgeCount = 0;
                            openAssetMusics("hongbao-tts.mp3");
                        }
                    } else {
                        closeAssetMusics();
                    }
                }
            } else {
                closeAssetMusics();
            }

        } else {
            deletePlayFailure(videoPath);
            nextShow();
        }
    }

    private void startPlayVideo(String videoPaths) {
//        KLog.e("线程2：" + mVideoView);
        mVideoView.setVideoPath(videoPaths);
        mVideoView.start();
        if (mVideoView.getVisibility() == View.GONE) {
            mVideoView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 无节目图片展示
     */
    private void standbyVisibility(String landscapeUrl, String portraitUrl) {
        if (showSumSize == 0) {
            int scrren = SharedPreferencesUtils.readInt(activity, Const.DEVICEID_TYPE);
            if (scrren == 1) {
                if (landscapeUrl != null) {
                    imagePath = DOMAIN_NAME2 + landscapeUrl;
                    optionsStandby = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .override(1920, 1080);
                }
            } else {
                if (portraitUrl != null) {
                    imagePath = DOMAIN_NAME2 + portraitUrl;
                    optionsStandby = new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .override(1080, 1920);
                }
            }
            if (imagePath != null) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        e("最终路径：" + imagePath);
                        GlideUtil.load(activity, imagePath, optionsStandby, mCoverView);
                        mCoverView.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            e("广告不为空");
        }
    }

    /**
     * 显示控制
     */
    private void showOperation(String deviceId) {
        if (!logoShow) {
            final String mLogoUrl = DOMAIN_NAME2 + logoUrl;
            if (!mLogoUrl.equals(DOMAIN_NAME2) && !mLogoUrl.equals(ILLEGAL_LOGO_URR)) {
                String logoName = logoUrl.substring(logoUrl.lastIndexOf("/") + 1);
                final String saveDir = FILE_SAVE_URL + logoName;
                downloadLogo(saveDir, mLogoUrl);

                ExecutorServiceManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                e("最终logo路径：" + mLogoUrl);
                                GlideUtil.qrLoad(activity, saveDir, optionsLogo, mAppLogo);
                                mAppLogo.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }, 5, TimeUnit.SECONDS);
            } else {
                post(new Runnable() {
                    @Override
                    public void run() {
                        GlideUtil.load(activity, R.drawable.app_name, optionsLogo, mAppLogo);
                        mAppLogo.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    mAppLogo.setVisibility(View.GONE);
                }
            });
        }
        if (!qrCodeShow) {
            int qrSize = SharedPreferencesUtils.readInt(activity, QR_SIZE);
            qrCodeShow2(deviceId, qrSize);
        } else {
            mText.setTextSize(8);
            textZxingVisible();
        }

        if (hasRedPacket) {
            String pictureFileName = "redpacket" + deviceId + ".jpg";
            SharedPreferencesUtils.writeString(activity, Const.HAS_REDPACKET_NAME, pictureFileName);
            e("文件名：" + pictureFileName);
            String picture = DOMAIN_NAME + "QRCode/" + pictureFileName;
            String videoPaths = FILE_SAVE_URL + pictureFileName;
            if (fileIsExists(videoPaths)) {
                initTimePrompt();
            } else {
                Base.pictureDownload(context, picture, videoPaths);
                initTimePrompt();
            }
        }
    }

    /**
     * 设备音量控制，最大级别15，最小级别0
     */
    private void volumeControl(AudioManager mAudio, int volume) {
        int current = mAudio.getStreamVolume(AudioManager.STREAM_MUSIC);
        e("当前的音量值：" + current);
        e("设置的音量值：" + volume);
        mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        volumeControlAnswer();
    }

    /**
     * 设备音量控制回应
     */
    private void volumeControlAnswer() {
        command[0] = (byte) 0x81;
        number[0] = (byte) 0xB4;
        String controlNumber = readString(activity, Const.CONTROL_TYPE);
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (Parameter parameters : parameter) {
            final String id = parameters.getDeviceId();
            final String rcode = parameters.getrCode();
            switch (controlNumber) {
                case "LAN":
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            ProtocolManager.getInstance().writeAnswer(id, rcode, command, number, true, "client");
                        }
                    });
                    break;
                case "INTERNET":
                    ThreadManager.getInstance().doExecute(new Runnable() {
                        @Override
                        public void run() {
                            ProtocolManager.getInstance().writeAnswer(id, rcode, command, number, true, "");
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 播放控件初始化
     */
    public void playInitialize() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            e("先释放再初始化！");
        }

        assert mVideoView != null;
        mVideoView.setOnCompletionListener(onCompletionListener);
        mVideoView.setOnErrorListener(onErrorListener);
        mVideoView.setOnPreparedListener(onPreparedListener);

    }


    /**
     * 打开assets下的mp3文件
     */
    private void openAssetMusics(final String mp3FileName) {
        //播放 assets/a2.mp3 音乐文件
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    mMediaPlayer = new MediaPlayer();
                    AssetFileDescriptor fd = getContext().getAssets().openFd(mp3FileName);
                    mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                    mMediaPlayer.prepare();
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取节目ID
     */
    private String getShowId() {
        try {
            if (mIsVip) {
                showId = vipShowSum().get(index).toString();
            } else {
                showId = showSumList.get(index).toString();
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return showId;
    }

    /**
     * 上一个节目
     */
    private void previousShow() {
        int vipSize = vipShowSum().size();
        int listSize = 2;
        e("播放上一个：" + index);
        e("当前清单大小：" + showSumSize);

        if (index == 0) {
            if (showSumSize == listSize || vipSize == listSize) {
                index = 1;
            } else {
                if (mIsVip) {
                    index = vipSize - 3;
                } else {
                    index = showSumSize - 3;
                }
            }
        } else if (index == 1) {
            if (showSumSize == listSize || vipSize == listSize) {
                index = 0;
            } else {
                if (mIsVip) {
                    index = vipSize - 2;
                } else {
                    index = showSumSize - 2;
                }
            }
        } else if (index == listSize) {
            if (mIsVip) {
                index = vipSize - 1;
            } else {
                index = showSumSize - 1;
            }
        } else if (index > listSize) {
            index = index - 3;
        }
        if (index < 0) {
            index = 0;
        }
        String playListId = getShowId();
        String fileName = getFileName(playListId);
        String saveDir = FILE_SAVE_URL + fileName;
        if (fileIsExists(saveDir)) {
            localShowPlay(playListId);
            playVideo();
        } else {
            e("文件不存在跳转上一个");
            previousShow();
        }
    }

    /**
     * 下一个节目
     */
    private void nextShow() {
        if (showSumSize != 0) {
            e("播放下一个");
            indexReset();
            index = index - 1;
            if (index < 0) {
                if (mIsVip) {
                    index = vipShowSum().size() - 1;
                } else {
                    index = showSumSize - 1;
                }
            }
            String playListId = getShowId();
            String fileName = getFileName(playListId);
            String saveDir = FILE_SAVE_URL + fileName;
            if (fileIsExists(saveDir)) {
                localShowPlay(playListId);
                mUri = fileName;
                playVideo();
                isJson = true;
            } else {
                if (showSumSize != 1) {
                    e("文件不存在跳转下一个");
                    nextShow();
                } else {
                    e("只有一个清单，检查清单是否过期！");
                    localShowPlay(playListId);
                }
            }
        } else {
            assignLoop();
        }
    }

    /**
     * 图片播放
     */
    private void imagePlay(final String picPath, final String fileType,
                           final boolean isStart) {
        post(new Runnable() {
            @Override
            public void run() {
                int deviceScreen;
                try {
//                    playInitialize();
                    mVideoView.setVisibility(View.INVISIBLE);
                    mVideoView.setVisibility(View.GONE);
                    if (mVideoView.isPlaying()) {
                        mVideoView.stopPlayback();
                    }
                    String imageType = "GIF";
                    //本地文件
                    File file = new File(ROOT_DIRECTORY_URL + PACKAGE_NAME, picPath);
                    if (!isStart) {
                        setImageGone();
                        e("加载启动画面");
                        GlideUtil.loadPlay3(activity, file, optionsImage2, mCoverView);
                        mCoverView.setVisibility(View.VISIBLE);
                    } else {
                        if (imageType.contains(fileType.toUpperCase())) {
                            e("动态图");
                            setImageGone();
                            imageGone();
                            gifFromAssets = new GifDrawable(file);
                            mGifImage.setImageDrawable(gifFromAssets);
                            gifFromAssets.start();
                            mGifImage.setVisibility(View.VISIBLE);
                        } else {
                            imageGone();
                            int deviceType = SharedPreferencesUtils.readInt(activity, Const.DEVICEID_TYPE);
                            if (deviceType == 1) {
                                deviceScreen = deviceScreenWidth;
                            } else {
                                deviceScreen = deviceScreenHeight;
                            }
                            FrameLayout.LayoutParams pImage = (FrameLayout.LayoutParams) mImage.getLayoutParams();
                            if (deviceType == 1) {
                                pImage.width = deviceScreen;
                                pImage.height = deviceScreenHeight;
                                e("横屏");
                                GlideUtil.loadPlay3(activity, file, optionsImage2, mImage);
                            } else {
                                pImage.width = deviceScreenWidth;
                                pImage.height = deviceScreen;
                                GlideUtil.loadPlay3(activity, file, optionsImage2, mImage);
                            }
                            mImage.setLayoutParams(pImage);
                            mImage.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    e("图片异常情况：" + e.toString());
                }
            }
        });
        //创建并执行在给定延迟后启用的一次性操作。
        if (isStart) {
            cdt.start();
        }
    }

    /**
     * 图片定时
     */
    CountDownTimer cdt = new CountDownTimer(15 * 1000, 15 * 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            mIsNewPlay = true;
            boolean isPlay = true;
            e("定时完成，视频播放状态：" + mVideoView.isPlaying());

            jingDongReport();

            if (mVideoView.isPlaying()) {
                isPlay = false;
            }
            e("是否能够跳转：" + isPlay);
            if (isPlay) {
                if (showSumSize > 0) {
                    e("播放清单文件");
                    playVideo();
                } else {
                    e("清单为空");
                    assignLoop();
                }
            }
        }
    };

    /**
     * 图片控件隐藏
     */
    private void imageGone() {
        if (mGifImage.getVisibility() == View.VISIBLE) {
            post(new Runnable() {
                @Override
                public void run() {
                    e("mGifImage动态图隐藏");
                    mGifImage.setVisibility(View.GONE);
                    if (gifFromAssets.isRunning()) {
                        gifFromAssets.stop();
                    }
                }
            });
        }
        if (mCoverView.getVisibility() == View.VISIBLE) {
            e("隐藏待机画面");
            post(new Runnable() {
                @Override
                public void run() {
                    mCoverView.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * 图片播放控件隐藏
     */
    private void setImageGone() {
        if (mImage.getVisibility() == View.VISIBLE) {
            e("mImage静态图隐藏");
            post(new Runnable() {
                @Override
                public void run() {
                    //新增2018-6-4-16:55
                    mImage.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * 启动后检查京东广告
     */
    private void getJingDongAd() {
        int adSize = readJingDongShow().size();
        if (adSize == 0) {
            List<Parameter> parameterList = LitePal.findAll(Parameter.class);
            for (Parameter parameter : parameterList) {
                isRequestJd = SharedPreferencesUtils.readBoolean(activity, Const.IS_START_JINGDONG);
                getAdDemo(parameter.getDeviceId());
            }
        }
    }

    /**
     * 二维码显示
     */
    private void qrCodeShow2(String deviceId, int qrSize) {
        String isId = "000", qrDeviceId;
        qrDeviceId = deviceId.substring(0, 3);
        if (isId.equals(qrDeviceId) && deviceId.length() == CONSTANT_ELEVEN) {
            final String fileName = "qr" + deviceId + ".jpg";
            final String mZxingPath = DOMAIN_NAME + "qrcode/" + fileName;
            final String saveDir = FILE_SAVE_URL + fileName;
            Base.pictureDownload(context, mZxingPath, saveDir);
            final int imageSize;
            int deviceType = SharedPreferencesUtils.readInt(activity, Const.DEVICEID_TYPE);
            if (qrSize == 0) {
                qrSize = 8;
            }
            if (deviceType == 1) {
                imageSize = screenRatio / qrSize;
            } else {
                imageSize = screenRatio / (qrSize + 1);
            }

            ExecutorServiceManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            RequestOptions options = new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .fitCenter()
                                    .override(imageSize, imageSize);
                            try {
                                Bitmap bitmap = BitmapFactory.decodeFile(saveDir);
                                AreaAveragingScale averagingScale = new AreaAveragingScale(bitmap);
                                GlideUtil.qrLoad2(activity, averagingScale.getScaledBitmap
                                        (imageSize, imageSize), options, mZxing);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            mZxing.setVisibility(View.VISIBLE);
                            if (mText.getVisibility() == View.VISIBLE) {
                                mZxing.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }, 5, TimeUnit.SECONDS);
        }

        ExecutorServiceManager.getInstance().schedule(deviceIdTask, 8, TimeUnit.SECONDS);
    }

    /**
     * logo显示大小控制
     */
    private void setLogoGlideSize(int scalingRatio) {
        int imageSize;
        if (scalingRatio == 0) {
            scalingRatio = 8;
        }
        int deviceType = SharedPreferencesUtils.readInt(activity, Const.DEVICEID_TYPE);
        if (deviceType == 1) {
            imageSize = screenRatio / scalingRatio;
        } else {
            imageSize = screenRatio / (scalingRatio + 1);
        }

        optionsLogo = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(imageSize, imageSize);
    }

    /**
     * 节目删除失败统计
     *
     * @param showId 节目ID
     */
    private void noDeleteShowId(String showId) {
        for (int i = deleteFailure.size() - 1; i >= 0; i--) {
            if (showId.equals(deleteFailure.get(i))) {
                deleteFailure.remove(i);
            }
        }
    }

    /**
     * 获取当前节目播放次数
     *
     * @param playListId 节目ID
     */
    private int getCurrentCount(String playListId) {
        int count = 0;
        qery = dataQuery("count", playListId);
        for (PlayList qerys : qery) {
            count = qerys.getCount();
        }
        return count;
    }

    /**
     * 删除播放失败处理
     */
    private void deletePlayFailure(String videoPath) {
        int maximum = 5;
        count++;
        if (count > maximum) {
            qery = getQuery("playId", "fileName", videoPath);
            for (PlayList showId : qery) {
                int deleteCount = LitePal.deleteAll(PlayList.class, "playId = ?", Integer.toString(showId.getShowId()));
                e("被删除数 " + deleteCount);
                if (deleteCount > 0) {
                    updateShowList();
                }
            }
        }
    }

    /**
     * 开启节目播放
     */
    private void playVideo() {
        List<Parameter> parameter = LitePal.findAll(Parameter.class);
        for (final Parameter parameters : parameter) {
            e("要播放的文件：" + mUri);
            if (mUri == null) {
                mUri = parameters.getPlayUrl();
                e("获取到的的文件名" + mUri);
                isJdStart = false;
            }
            try {
                showPlay(mUri, true);
                setIntentData("playUrl", mUri);
                e("isJson结果: " + isJson);
                if (showSumSize > 0 && isJson) {
                    String peakPath = DEVICE_TIME + parameters.getDeviceId();
                    if (isNetConnected(activity)) {
                        webPeakTime(peakPath, parameters);
                    } else {
                        String peakData = readString(activity, Const.VIP_SHOW_DATA);
                        e("没有网络：" + peakData);
                        jsonToObj(peakData, parameters);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * VIP节目时间获取
     */
    public void webPeakTime(final String url, final Parameter parameter) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                mMyOkHttp.get()
                        .url(url)
                        .tag(this)
                        .enqueue(new RawResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, String response) {
                                e("获取VIP节目时间数据:" + response);
                                try {
                                    JSONObject json = new JSONObject(response);
                                    if (json.getBoolean(SUCCESS)) {
                                        SharedPreferencesUtils.writeString(activity, Const.VIP_SHOW_DATA, response);
                                        jsonToObj(response, parameter);
                                    } else {
                                        e("获取数据失败");
                                        String vipShowData = readString(activity, Const.VIP_SHOW_DATA);
                                        jsonToObj(vipShowData, parameter);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, String errorMsg) {
                                e("VIP参数获取失败原因:" + errorMsg);
                                String vipShowData = readString(activity, Const.VIP_SHOW_DATA);
                                jsonToObj(vipShowData, parameter);
                            }
                        });
            }
        });
    }

    /**
     * VIP时间解析
     */
    int intervalCount = 0;

    public void jsonToObj(final String jsonString, final Parameter parameter) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                boolean isPeak = false;
                String byTime = "byTime";
                try {
                    JSONObject json = new JSONObject(jsonString);
                    e("VIP时间解析的结果：" + json.getBoolean(byTime));
                    if (json.getBoolean(byTime)) {
                        setIntentData("isPeakTimes", true);
                        JSONArray deviceTimes = json.getJSONArray("deviceTimes");
                        for (int i = 0; i < deviceTimes.length(); i++) {
                            PeakParameter deviceTime = new PeakParameter();
                            deviceTime.startTime = deviceTimes.getJSONObject(i).getString("startTime").substring(11);
                            deviceTime.endTime = deviceTimes.getJSONObject(i).getString("endTime").substring(11);
                            isPeak = timeTrigger(deviceTime.startTime, deviceTime.endTime);
                            if (isPeak) {
                                break;
                            }
                        }
                        e("是否开启VIP播放：" + isPeak);
                        if (isPeak && parameter.getIsPeakTimes() && vipShowSum().size() > 0) {
                            int vipLength = vipShowSum().size() - 1;
                            if (index > vipLength) {
                                index = 0;
                            } else if (index < 0) {
                                index = 0;
                            }
                            mIsVip = true;
                            e("开启VIP播放以及当前下标：" + index);
//                            e("查询到的VIP节目："+ vipShowSum());
                            String playListId = vipShowSum().get(index).toString();
                            localShowPlay(playListId);
                        } else {
                            e("获取非VIP时段播放的节目");
                            nonTimeAdvertising();
                        }
                    } else {
                        e("isPeakTimes获取非VIP时段播放的节目");
                        nonTimeAdvertising();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取时段时间
     */
    private void nonTimeAdvertising() {
        String playListIds;
        indexReset();
        playListIds = getShowPlayId();
        e("第一次获取的播放ID：" + playListIds + ", " + showSumSize);
        int size = showIntervalTimeList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String playId2 = Integer.toString(showIntervalTimeList.get(i));
                List<PlayList> interval = dataQuery("intervalTimeList", playId2);
                for (PlayList intervals : interval) {
                    String fileName = getFileName(playId2);
                    String saveDir = FILE_SAVE_URL + fileName;
                    if (fileIsExists(saveDir)) {
                        time = intervals.getIntervalTimeList();
                    } else {
                        fileInspect(fileName);
                    }
                }

                inspectIntervalTime2();
                e("比较结果：" + isPlayIntervalTimeShow);
                if (isPlayIntervalTimeShow) {
                    e("时段广告,跳出循环");
                    intervalCount++;
                    if (intervalCount == 1) {
                        playListIds = playId2;
                    }
                    break;
                }
            }
            if (!isPlayIntervalTimeShow) {
                e("时段比较失败");
                intervalCount = 0;
            }
        }

        e("最后确定的广告ID：" + playListIds);
        if (playListIds == null && showSumSize > 0) {
            nonTimeAdvertising();
        } else {
            localShowPlay(playListIds);
        }
        mIsVip = false;
    }

    private void fileInspect(String fileName) {
        if (!downloadList.contains(fileName)) {
            downloadList.add(fileName);
            e("任务未经存在");
            fileMissingDownload(fileName);
        }
    }

    /**
     * 广告文件重新下载
     */
    private void fileMissingDownload(String videoPath) {
        if (isNetConnected(activity)) {

            removeFile(videoPath);

            List<PlayList> countList = fileNameQuery("downloadCount", videoPath);
            for (PlayList countLists : countList) {
                final int count = countLists.getDownloadCount();
                e("下载次数：" + count);
                if (count > CONSTANT_TEN) {
                    int delete = LitePal.deleteAll(PlayList.class, "fileName = ?", videoPath);
                    e("删除结果：" + delete);
                } else {
                    String url = DOMAIN_NAME + "Upload/" + videoPath;
                    String saveDir = FILE_SAVE_URL + videoPath;
                    mMyOkHttp.download()
                            .url(url)
                            .filePath(saveDir)
                            .tag(this)
                            .enqueue(new DownloadResponseHandler() {
                                @Override
                                public void onFinish(File downloadFile) {
                                    String fileName = downloadFile.toString().substring(downloadFile.toString().lastIndexOf("/") + 1);
                                    e("下载成功：" + fileName);
                                    downloadList.remove(fileName);
                                    if (checkMd5(fileName)) {
                                        updateDownloadState(fileName);
                                        updateDownloadCount(count + 1, fileName);
                                    } else {
                                        e("文件不完整移除");
                                        removeFile(fileName);
                                    }
                                }

                                @Override
                                public void onProgress(long currentBytes, long totalBytes) {
                                }

                                @Override
                                public void onFailure(String errorMsg) {
                                    e("失败原因：" + errorMsg);
                                }
                            });
                }
            }
        } else {
            e("当前网络不可用！");
        }
    }

    /**
     * 获取播放ID
     */
    private String getShowPlayId() {
        String playListIds = null;
        indexReset();
        String playId = showSumList.get(index).toString();
        String fileName = getFileName(playId);
        String saveDir = FILE_SAVE_URL + fileName;
        if (fileIsExists(saveDir) && checkMd5(fileName)) {
            playListIds = playId;
        } else {
            addSelf();
            getShowPlayId();
            if (fileName != null) {
                fileInspect(fileName);
            }
        }
        return playListIds;
    }

    /**
     * 获取下载状态
     */
    private String getFileName(String playId) {
        String fileName = null;
        try {
            List<PlayList> queryFileName = dataQuery("fileName", playId);
            for (PlayList queryFileNames : queryFileName) {
                fileName = queryFileNames.getFileName();
                e("获取文件名：" + fileName);
            }
        } catch (StackOverflowError e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 红包广告控制
     *
     * @param videoPath 路径
     */
    private void redPacketOperation(@NonNull final String videoPath) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                List<PlayList> redPacket1 = fileNameQuery("redPacket", videoPath);
                for (PlayList redPackets : redPacket1) {
                    isRedPacket = redPackets.getRedPacket();
                    e("当前是否是红包广告：" + isRedPacket);
                    List<PlayList> pictureName2 = fileNameQuery("pictureName", videoPath);
                    for (PlayList pictureName2s : pictureName2) {
                        final String pictureName = pictureName2s.getPictureName();
                        if (isRedPacket == CONSTANT_TWO) {
                            List<PlayList> showId = fileNameQuery("showId", videoPath);
                            for (PlayList showIds : showId) {
                                String url = REDPACKET_AMOUNT + showIds.getShowId();
                                mMyOkHttp.get()
                                        .url(url)
                                        .tag(this)
                                        .enqueue(new RawResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, String response) {
                                                e("获取的数据:" + response);
                                                try {
                                                    String urlDecoder = java.net.URLDecoder.decode(response, "UTF-8");
                                                    JSONObject json = new JSONObject(urlDecoder);
                                                    if (json.getBoolean("success")) {
                                                        redPacket = json.getBoolean("hasRedPacket");
                                                        redPacketMoney = json.getInt("amount");
                                                        e("是否还有红包：" + redPacket);
                                                        e("红包金额：" + redPacketMoney);
                                                        if (redPacketMoney == 0 || !redPacket) {
                                                            String videoPaths = FILE_SAVE_URL + pictureName;
                                                            mRedPacket.setVisibility(View.GONE);
                                                            if (fileIsExists(videoPaths)) {
                                                                e("删除红包二维码");
                                                                Base.deleteFile(pictureName, "");
                                                            }
                                                            closeAssetMusics();
                                                        } else {
                                                            rePacketVisibility(pictureName);
                                                        }
                                                    }
                                                } catch (JSONException | UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, String errorMsg) {
                                                e("红包数据失败原因:" + errorMsg);
                                                redPacketMoney = 0;
                                                rePacketVisibility(pictureName);
                                            }
                                        });
                            }
                        } else {
                            if ("00".equals(readString(activity, VOICE_ENABLE))) {
                                closeAssetMusics();
                            }
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    mRedPacket.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * 红包二维码展示
     */
    private void rePacketVisibility(String pictureName) {
        e("红包图和金额：" + pictureName + ", " + redPacketMoney);
        if (pictureName != null && redPacketMoney != 0) {
            final File file = new File(ROOT_DIRECTORY_URL + PACKAGE_NAME, pictureName);
            if (fileIsExists(FILE_SAVE_URL + pictureName)) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        GlideUtil.loadPlay3(activity, file, optionsRedPacket, mRedPacket);
                        mRedPacket.setVisibility(View.VISIBLE);
                    }
                });

                openAssetMusics("hongbao-tts.mp3");
            } else {
                String picture = DOMAIN_NAME + "RedPacket/" + pictureName;
                final String saveDir = FILE_SAVE_URL + pictureName;
                MonitorService monitorService = new MonitorService();
                monitorService.pictureDownload(picture, saveDir);
            }
        } else {
            e("当前红包金额：" + redPacketMoney);
            if (!redPacket) {
                closeAssetMusics();
                post(new Runnable() {
                    @Override
                    public void run() {
                        mRedPacket.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    /**
     * 播放类型比对
     */
    private void localShowPlay(String playListId) {
        try {
            qery = dataQuery("condition", playListId);
            for (PlayList conditions : qery) {
                String playType = conditions.getCondition();
                e("播放类型：" + playType);
                switch (playType) {
                    case "00":
                        qery = dataQuery("duration", playListId);
                        for (PlayList duration : qery) {
                            int queryDuration = duration.getDuration();
                            e("设定播放次数：" + queryDuration);
                            int count = getCurrentCount(playListId);
                            e("已播放次数：" + count);
                            if (count >= queryDuration) {
                                deleteShow(playListId);
                            } else if (count < duration.getDuration()) {
                                videoPath(playListId);
                            }
                        }
                        break;
                    case "01":
                        timePlay(playListId);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 时段广告比对处理
     */
    private void intervalTimeInspect() {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                showIntervalTimeList = readIntervalTimeShow();
                int size = showIntervalTimeList.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        String playId = Integer.toString(showIntervalTimeList.get(i));
                        qery = dataQuery("condition", playId);
                        for (PlayList conditions : qery) {
                            String playType = conditions.getCondition();
                            switch (playType) {
                                case "00":
                                    qery = dataQuery("duration", playId);
                                    for (PlayList duration : qery) {
                                        int queryDuration = duration.getDuration();
                                        e("设定播放次数：" + queryDuration);
                                        int count = getCurrentCount(playId);
                                        e("已播放次数：" + count);
                                        if (count >= queryDuration) {
                                            deleteAnswer(playId);
                                        }
                                    }
                                    break;
                                case "01":
                                    String startTime = null, endTime = null;
                                    Date startDate, endDate;
                                    try {
                                        Date currentDate = new Date();
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

                                        qery = dataQuery("startTime", playId);
                                        for (PlayList start : qery) {
                                            startTime = start.getStartTime();
                                        }
                                        qery = dataQuery("endTime", playId);
                                        for (PlayList end : qery) {
                                            endTime = end.getEndTime();
                                        }
                                        assert startTime != null;
                                        startDate = format.parse(startTime);
                                        assert endTime != null;
                                        endDate = format.parse(endTime);
                                        assert startDate != null;
                                        assert endDate != null;
                                        if (startDate.before(currentDate) && endDate.after(currentDate)) {
                                            e("当前时段广告可以播放");
                                        } else if (currentDate.before(startDate)) {
                                            e("当前时段广告时间未到，不处理");
                                        } else {
                                            deleteAnswer(playId);
                                        }
                                    } catch (ParseException e) {
                                        e("异常", e.toString());
                                        e.printStackTrace();
                                    } catch (IndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 时间播放比对
     */
    private void timePlay(final String playListId) {
        String startTime = null, endTime = null;
        Date startDate, endDate;
        try {
            Date currentDate = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

            qery = dataQuery("startTime", playListId);
            for (PlayList start : qery) {
                startTime = start.getStartTime();
            }
            qery = dataQuery("endTime", playListId);
            for (PlayList end : qery) {
                endTime = end.getEndTime();
            }
            assert startTime != null;
            startDate = format.parse(startTime);
            assert endTime != null;
            endDate = format.parse(endTime);
            e("起始时间：" + startTime);
            e("现在时间：" + format.format(currentDate));
            e("终止时间：" + endTime);
            assert startDate != null;
            assert endDate != null;
            if (startDate.before(currentDate) && endDate.after(currentDate)) {
                e("可以播放");
                videoPath(playListId);
            } else if (currentDate.before(startDate)) {
                e("时间未到，播放下一个");
                addSelf();
                String playListIds = showSumList.get(index).toString();
                localShowPlay(playListIds);
            } else {
                if (isNetConnected(activity)) {
                    deleteShow(playListId);
                } else {
                    e("网络异常继续播放");
                    videoPath(playListId);
                }
            }
        } catch (ParseException e) {
            e("异常：" + e.toString());
            e.printStackTrace();
        } catch (IndexOutOfBoundsException | StackOverflowError | NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取节目播放文件
     */
    private void videoPath(final String playShowId) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    e("获取播放地址方式和下标：" + mIsVip + "，" + index);
                    List<PlayList> baiDu = dataQuery("isBaiDu", playShowId);
                    for (PlayList baiDus : baiDu) {
                        List<PlayList> intervalTime = dataQuery("intervalTime", playShowId);
                        for (PlayList intervalTimes : intervalTime) {
                            List<Parameter> parameter = LitePal.findAll(Parameter.class);
                            for (Parameter parameters : parameter) {
                                int isBaiDu = baiDus.getIsBaiDu();
                                int timeInterval = intervalTimes.getIntervalTime();
                                final String deviceId = parameters.getDeviceId();
                                rCode = parameters.getrCode();
                                e("协议类型：" + parameters.getProtocolType());
                                currentPlayId = playShowId;
                                e("当前播放ID：" + playShowId);
                                isJdStart = true;
                                if (parameters.getProtocolType() == 2) {
                                    SharedPreferencesUtils.writeString(activity, Const.NET_TYPE, "udp");
                                }
                                if (isBaiDu == 1) {
                                    e("百度广告");
                                    baiDuPare(deviceId);
                                } else if (timeInterval == 1) {
                                    e("时段广告");
                                    showUpdate(deviceId, playShowId, rCode);
                                } else if (isBaiDu == CONSTANT_TWO) {
                                    e("京东广告");
                                    getAdDemo(deviceId);
                                } else {
                                    e("普通广告");
                                    showUpdate(deviceId, playShowId, rCode);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    e("异常原因：" + e.toString());
                }
            }
        });
    }

    /**
     * 时段广告时间检查
     */
    private void inspectIntervalTime2() {
        try {
            ArrayList<Boolean> timeList = new ArrayList<>();
            int timeSize = time.size();
            if (timeSize > 0) {
                for (int i = 0; i < timeSize; i++) {
                    String getTime = time.get(i);
                    String startTime = getTime.substring(0, getTime.lastIndexOf("/"));
                    String endTime = getTime.substring(getTime.lastIndexOf("/") + 1);
                    boolean result = timeTrigger(startTime, endTime);
                    timeList.add(result);
                }
                int resultCountSize = timeList.size();
                for (int i = 0; i < resultCountSize; i++) {
                    if (timeList.get(i)) {
                        isPlayIntervalTimeShow = true;
                        e("成功");
                        break;
                    } else {
                        isPlayIntervalTimeShow = false;
                    }
                }
            } else {
                isPlayIntervalTimeShow = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 节目更新
     */
    private void showUpdate(String deviceId, String playId, String rCode) {
        int playCount = 0;
        List<PlayList> fileName = dataQuery("fileName", playId);
        for (PlayList filenames : fileName) {
            playCount = getCurrentCount(playId);
            e("设置前已播放的次数：" + playId + " ," + playCount);
            playCount = playCount + 1;
            mUri = filenames.getFileName();
            setCount(playId, playCount);

            e("即将播放文件：" + mUri);
        }
        addSelf();
        showReport(deviceId, rCode, playId, playCount);
    }

    /**
     * 节目集合更新
     */
    private void updateShowList() {
        showSumList = showSum();
        showSumSize = showSumList.size();
    }

    /**
     * 删除清单
     */
    private void deleteShow(String playListId) {

        deleteAnswer(playListId);

        updateShowList();
        e("当前集合大小：" + showSumSize);
        e("当前下标：" + index);
        if (showSumSize > 0) {
            if (index > showSumSize || index == showSumSize) {
                e("下标等于集合大小,或者大于");
                index = 0;
            } else if (showSumSize == 1) {
                index = 0;
            }
            try {
                String showIdNew = showSumList.get(index).toString();
                localShowPlay(showIdNew);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (showSumSize == 0) {
            e("没有可播放清单");
            assignLoop();
        }
    }

    /**
     * 删除回应
     */
    private void deleteAnswer(final String playListId) {
        List<PlayList> qery = dataQuery("fileName", playListId);
        for (PlayList filename : qery) {
            String deleteFileName = filename.getFileName();
            e("要删除本地文件,和播放ID：" + deleteFileName, playListId);
            List<PlayList> baiDu = dataQuery("isBaiDu", playListId);
            for (PlayList baiDus : baiDu) {
                List<Parameter> parameter = LitePal.findAll(Parameter.class);
                for (Parameter parameters : parameter) {
                    String deviceId = parameters.getDeviceId();
                    int adType = baiDus.getIsBaiDu();
                    if (adType == CONSTANT_ONE) {
                        int isDelete = LitePal.deleteAll(PlayList.class, "playId = ?：" + playListId);
                        e("百度广告清单删除结果：" + isDelete);
                        if (isDelete > 0) {
                            updateShowList();
                            e("当前广告数量：" + showSumSize);
                            if (showSumSize == 0) {
                                baiDuPare(deviceId);
                            }
                        }
                    } else if (adType == CONSTANT_TWO) {
                        int isDelete = LitePal.deleteAll(PlayList.class, "playId = ?", playListId);
                        e("" + "京东广告清单删除结果：" + isDelete);
                        if (isDelete > 0) {
                            updateShowList();
                        }
                    } else {
                        List<PlayList> jiTouAdv = dataQuery("showId", playListId);
                        for (PlayList jiTouAdvs : jiTouAdv) {
                            String showId = Integer.toString(jiTouAdvs.getShowId());
                            int isDelete = LitePal.deleteAll(PlayList.class, "playId = ?", playListId);
                            e("即投云媒广告清单删除结果：" + isDelete);
                            if (isDelete > 0) {
                                updateShowList();
                                String server = readString(activity, Const.CONTROL_TYPE);
                                Base.deleteFile(deleteFileName, server);

                                programmeControl(showId, deviceId);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除云端节目数据
     */
    private void programmeControl(final String showId, final String deviceId) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                String url = DELETE_PLAYED_MEDIA + deviceId + "&mediaId=" + showId;
                mMyOkHttp.get()
                        .url(url)
                        .tag(this)
                        .enqueue(new RawResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, String response) {
                                e("获取删除节目返回数据:" + response);
                                try {
                                    JSONObject jsonString = new JSONObject(response);
                                    if (jsonString.getBoolean(SUCCESS)) {
                                        if (deleteFailure.size() > 0) {
                                            noDeleteShowId(deleteFailure.get(0));
                                        }
                                    } else {
                                        e("删除云数据库失败");
                                        deleteFailure.add(showId);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, String errorMsg) {
                                e("失败原因:" + errorMsg);
                                deleteFailure.add(showId);
                            }
                        });
            }
        });
    }

    /**
     * 停止视频播放
     */
    public void stopPlay() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mIsStopped = true;
        }
    }

    /**
     * 百度广告参数请求
     */
    private void baiDuPare(final String deviceId) {
        if (isRequestBaiDu) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    String url = GET_BAIDU_AD + deviceId;
                    mMyOkHttp.get()
                            .url(url)
                            .tag(this)
                            .enqueue(new RawResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, String response) {
                                    e("获取百度数据:" + response);
                                    baiDuDataParser(deviceId, response);
                                }

                                @Override
                                public void onFailure(int statusCode, String errorMsg) {
                                    e("获取百度数据失败原因:" + errorMsg);
                                    baiDuPare(deviceId);
                                }
                            });
                }
            });
        }
    }

    /**
     * 百度广告上报信息解析，上报播放次数
     */
    private void baiDuDataParser(String deviceId, String response) {
        int lastPlayId;
        try {
            BaiDuAdvParam baiduAdvParam = BaiDuAdvParam.objectFromData(response);
            boolean baiDuRequestResult = baiduAdvParam.isSuccess();
            e("数据获取结果：" + baiDuRequestResult);
            if (baiDuRequestResult || response.length() > CONSTANT_FORTY) {
                isRequestBaiDu = true;
                int baiDuId = baiduAdvParam.getAdvInfo().getId();
                e("百度广告ID：" + baiDuId);
                String imageUrl = baiduAdvParam.getAdvInfo().getImageUrl().get(0);
                String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                e("文件路径：" + imageUrl);
                e("播放图片名字：" + imageName);

                PlayList playList = new PlayList();
                List<PlayList> mpList = LitePal.findAll(PlayList.class);
                if (mpList.size() > 0) {
                    PlayList lastNews = LitePal.findLast(PlayList.class);
                    lastPlayId = lastNews.getPlayId();
                } else {
                    lastPlayId = 0;
                }

                addBaiDuShow(lastPlayId, baiDuId, imageName, playList);

                String localFile = FILE_SAVE_URL + imageName;
                if (!fileIsExists(localFile)) {
                    e("本地没有文件下载");

                    baiDuFileDownload(imageUrl, imageName, lastPlayId + 1);
                } else {
                    e("文件已经存在，跳过文件下载");
                    updateDownloadState(imageName);
                    int count = showSum().size();
                    if (count == 1) {
                        setCount(Integer.toString(lastPlayId + 1), 1);
                        showPlay(imageName, true);
                    }
                }

                for (int i = 0; i < baiduAdvParam.getAdvInfo().getWinNoticeUrl().size(); i++) {
                    String winNoticeUrl = baiduAdvParam.getAdvInfo().getWinNoticeUrl().get(i);
                    e("循环到的上报路径：" + winNoticeUrl);
                    Base.webInterface(winNoticeUrl);
                }

                String baiDuIsPlayedPath = BAIDU_IS_PLAYED + baiDuId;
                Base.webInterface(baiDuIsPlayedPath);

                int thirdMonitorUrlSize = baiduAdvParam.getAdvInfo().getThirdMonitorUrl().size();
                for (int i = 0; i < thirdMonitorUrlSize; i++) {
                    String thirdMonitorUrl = baiduAdvParam.getAdvInfo().getThirdMonitorUrl().get(i);
                    e("循环到的第三方监控地址：" + thirdMonitorUrl);
                    Base.webInterface(thirdMonitorUrl);
                }
            } else {
                String error = "-1";
                String errorCode = Integer.toString(baiduAdvParam.getErrorCode());
                e("获取百度数据失败！,错误码是 =：" + errorCode);
                if (error.equals(errorCode)) {
                    e("继续申请百度广告");
                    baiDuPare(deviceId);
                } else {
                    isRequestBaiDu = false;
                    webSaveBaiDuLog(deviceId, errorCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            baiDuPare(deviceId);
            e("百度数据异常原因：" + e.toString());
        }
    }

    /**
     * 添加百度广告清单
     */
    private void addBaiDuShow(int lastPlayId, int baiDuId, String imageName, PlayList
            playList) {
        e("当前数据库最后一条数据播放ID：" + lastPlayId);
        int baiDuShow = readBaiDuShow().size();
        e("百度广告清单大小：" + baiDuShow);
        if (baiDuShow <= 1) {
            e("添加");
            Base.setShowData(lastPlayId + 1, "00", "2018-12-14 13:48:55", "2018-12-14 13:48:55", 1, 6, imageName,
                    1111, "百度广告", 0, 3, 2, 1, 1, "没有", 1, 0,
                    baiDuId, null, null, playList);
            playList.save();
        }
    }

    /**
     * 百度广告获取失败返回
     */
    private void webSaveBaiDuLog(String deviceId, String errorCode) {
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());

            JSONObject userJson = setJsonUser();

            JSONObject logJson = new JSONObject();
            logJson.put("deviceId", deviceId);
            logJson.put("errorCode", errorCode);
            logJson.put("requestDate", simpleDateFormat.format(date));

            JSONObject postJson = new JSONObject();
            postJson.put("user", userJson);
            postJson.put("log", logJson);

            e("百度失败日志数据：" + postJson.toString());
            //与params不共存 以jsonParams优先
            mMyOkHttp.post()
                    .url(SAVE_BAIDU_LOG)
                    .jsonParams(postJson.toString())
                    .tag(this)
                    .enqueue(new JsonResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, JSONObject response) {
                            e("doPostJSON onSuccess JSONObject:" + response);
                        }

                        @Override
                        public void onSuccess(int statusCode, JSONArray response) {
                            e("doPostJSON onSuccess JSONArray:" + response);
                        }

                        @Override
                        public void onFailure(int statusCode, String errorMsg) {
                            e("doPostJSON onFailure:" + errorMsg);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 百度文件下载
     */
    private void baiDuFileDownload(final String imageUrlPath, final String imageName,
                                   final int playId) {
        e("图片下载路径：" + imageUrlPath);
        String saveDir = FILE_SAVE_URL + imageName;
        mMyOkHttp.download()
                .url(imageUrlPath)
                .filePath(saveDir)
                .tag(this).enqueue(new DownloadResponseHandler() {
            @Override
            public void onFinish(File downloadFile) {
                e("百度广告下载完成,加播放ID：" + imageName + "; " + playId);

                updateDownloadState(imageName);
                e("清单大小：" + showSumSize);
                jingDongPaly(imageName, playId, showSumSize);
            }

            @Override
            public void onProgress(long currentBytes, long totalBytes) {
            }

            @Override
            public void onFailure(String errorMsg) {
                e("百度广告下载完成失败，原因：" + errorMsg);
                baiDuFileDownload(imageUrlPath, imageName, playId);
            }
        });
    }

    @NonNull
    private JSONObject setJsonUser() throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("userName", USER_NAME);
        userJson.put("appId", APPID);
        userJson.put("appKey", APPKEY);
        return userJson;
    }

    /**
     * 获取广告-demo
     */
    private void getAdDemo(final String deviceId) {
        if (isRequestJd) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    KuaiFaClient client = new KuaiFaClient(JINGDONG_APP_ID, JINGDONG_APP_KEY, JINGDONG_APP_HOST, true);
                    ScreenSizeEntry size = new ScreenSizeEntry(1920, 1080);
                    String mac = readString(activity, Const.MAC);
                    e("mac地址：" + mac);
                    //获取当前时间
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
                    Date date = new Date(System.currentTimeMillis());
                    DeviceUDIDEntry udid = new DeviceUDIDEntry("9774d56d682e549c",
                            mac, simpleDateFormat.format(date), deviceId);
                    DeviceEntry device = new DeviceEntry(udid, size);

                    DeviceNetworkEntry network = new
                            DeviceNetworkEntry(NetUtil.getNetworkState(context),
                            NetworkOperatorType.MOBILE);
                    String requestId = UUID.randomUUID().toString().replace("-", "");
                    GetAdResult result = client.getAd(device, network, 15, MaterialType.ALL, requestId);
                    int code = result.getCode();
                    String msg = result.getMsg();
                    e("code返回值：" + code);
                    e("msg返回值：" + msg);
                    if (code == 0 && SUCCESS2.equals(msg)) {
                        e("获取广告成功");
                        jdRequestSuccess(result, requestId, deviceId);
                    } else if (code == JINGDONG_CODE1 || code == JINGDONG_CODE2) {
                        e("错误信息：" + code);
                        jdCount++;
                        if (jdCount < CONSTANT_THREE) {
                            getAdDemo(deviceId);
                        } else {
                            if (jdTimeTest()) {
                                jdCount++;
                                if (jdCount <= CONSTANT_FORTY) {
                                    scheduleGetJd(deviceId, 15);
                                } else if (jdCount <= CONSTANT_ONE_THOUSAND) {
                                    scheduleGetJd(deviceId, 60);
                                }
                            }
                        }
                    } else if (code == JINGDONG_CODE3) {
                        if (jdTimeTest()) {
                            jdCount++;
                            if (jdCount <= CONSTANT_FORTY) {
                                scheduleGetJd(deviceId, 15);
                            } else if (jdCount <= CONSTANT_ONE_THOUSAND) {
                                scheduleGetJd(deviceId, 60);
                            }
                        }
                    } else if (!msg.equals(SUCCESS2)) {
                        getAdDemo(deviceId);
                    }
                }
            });
        }
    }

    /**
     * 解析获取到的京东广告
     */
    private void jdRequestSuccess(GetAdResult result, String requestId, String deviceId) {
        String startJdPlay = null, endJdPlay = null;
        ArrayList<String> currentJdPlay = null;
        int lastPlayId;
        try {
            jdCount = 0;
            int adKey = result.getData().ad_key;
            int showTime = result.getData().material.show_time;
            String title = result.getData().material.title;
            String md5 = result.getData().material.md5;
            int height = result.getData().material.height;
            int width = result.getData().material.width;
            String url = result.getData().material.url;
            String type = result.getData().material.type;
            ArrayList<ArrayList<String>> adTracking = result.getData().ad_tracking;
            if (adTracking.size() > 0) {
                startJdPlay = result.getData().ad_tracking.get(0).get(0);
                endJdPlay = result.getData().ad_tracking.get(1).get(0);
                currentJdPlay = result.getData().ad_tracking.get(2);
            }
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            e("京东文件名：" + fileName);

            PlayList playList = new PlayList();
            List<PlayList> mpList = LitePal.findAll(PlayList.class);
            if (mpList.size() > 0) {
                PlayList lastNews = LitePal.findLast(PlayList.class);
                lastPlayId = lastNews.getPlayId();
            } else {
                lastPlayId = 0;
            }
            addJingDongShow(adKey, url, lastPlayId, requestId, fileName, md5, startJdPlay, endJdPlay, currentJdPlay, playList);
            addJingDongWeb(adKey, showTime, title, md5, height, width, url, type, requestId, deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 京东广告请求时间校验
     */
    private boolean jdTimeTest() {
        boolean isTime;
        long jdQuestTime2 = System.currentTimeMillis() / 1000;
        long jdTime = jdQuestTime2 - jdQuestTime;
        e("每次请求时间差：" + jdTime);
        if (jdTime >= CONSTANT_FIFTEEN) {
            jdQuestTime = System.currentTimeMillis() / 1000;
            isTime = true;
        } else {
            isTime = false;
        }
        return isTime;
    }

    /**
     * 延时请求京东广告
     */
    private void scheduleGetJd(final String deviceId, int time) {
        ExecutorServiceManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                getAdDemo(deviceId);
            }
        }, time, TimeUnit.SECONDS);
    }

    /**
     * 删除京东广告
     */
    private void jdDelete(String playListId) {
        qery = dataQuery("duration", playListId);
        for (PlayList duration : qery) {
            int queryDuration = duration.getDuration();
            e("设定播放次数：" + queryDuration);
            int count = getCurrentCount(playListId);
            e("已播放次数：" + count);
            if (count >= queryDuration) {
                deleteAnswer(playListId);
            }
        }
    }

    /**
     * 京东广告信息上传即投云媒数据库
     */
    private void addJingDongWeb(final int adKey, final int showTime, final String title,
                                final String md5, final int height,
                                final int width, final String url, final String type, final String requestId,
                                final String deviceId) {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                AddJingDongAdv add = new AddJingDongAdv();
                add.setRequestId(requestId);
                add.setAdKey(adKey);
                add.setShowTime(showTime);
                add.setTitle(title);
                add.setMd5(md5);
                add.setHeight(height);
                add.setWidth(width);
                add.setUrl(url);
                add.setType(type);
                add.setDeviceId(deviceId);
                e("上传web数据：" + JSON.toJSONString(add));
                mMyOkHttp.post()
                        .url(ADD_JINGDONG)
                        .jsonParams(JSON.toJSONString(add))
                        .tag(this)
                        .enqueue(new JsonResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, JSONObject response) {
                                e("京东广告数据上报web成功1:" + response);
                            }

                            @Override
                            public void onSuccess(int statusCode, JSONArray response) {
                                e("京东广告数据上报web成功2" + response);
                            }

                            @Override
                            public void onFailure(int statusCode, String errorMsg) {
                                e("doPostJSON onFailure:" + errorMsg);
                            }
                        });
            }
        });
    }

    /**
     * 添加京东广告清单
     */
    private void addJingDongShow(int adKey, String url, int lastPlayId, String
            requestId, String imageName, String md5, String startUrl,
                                 String stopUrl, ArrayList<String> jdUrl, PlayList playList) {
        e("当前数据库最后一条数据播放ID：" + lastPlayId);
        int jingDongShow = readJingDongShow().size();
        e("京东广告清单大小：" + jingDongShow);
        Base.setJdShowData(lastPlayId + 1, "00", "2018-12-14 13:48:55", "2018-12-14 13:48:55", 1, 6, imageName,
                1111, requestId, 0, 3, 2, 1, 1, "没有", 2, 1,
                0, null, md5.toUpperCase(), startUrl, stopUrl, jdUrl, playList);
        playList.save();
        e("添加");

        updateShowList();

        String saveDir = FILE_SAVE_URL + imageName;
        int adKeyDownload = SharedPreferencesUtils.readInt(activity, AD_MATERIAL_ID);
        if (adKey != adKeyDownload) {
            SharedPreferencesUtils.writeInt(activity, AD_MATERIAL_ID, adKey);
            fileDownload(url, imageName, lastPlayId + 1);
        } else {
            if (fileIsExists(saveDir)) {
                e("新增后清单大小" + jingDongShow + "和播放状态" + mVideoView.isPlaying());
                if (showSumSize == 1 && (!mVideoView.isPlaying())) {
                    e("新增已存在立即播放");
                    setCount(Integer.toString(lastPlayId + 1), 1);
                    currentPlayId = Integer.toString(lastPlayId + 1);
                    isJdStart = true;
                    showPlay(imageName, true);
                }
            } else {
                SharedPreferencesUtils.writeInt(activity, AD_MATERIAL_ID, adKey);
                fileDownload(url, imageName, lastPlayId + 1);
            }
        }
        if (jingDongShow > CONSTANT_TWO) {
            e("多余删除");
            jdDelete(Integer.toString(readJingDongShow().get(0)));
        }
    }

    /**
     * 京东开始播放上报
     */
    private void startJingDong(final String url) {
        if (isRequestJd && url != null) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    mMyOkHttp.get()
                            .url(url)
                            .tag(this)
                            .enqueue(new RawResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, String response) {
                                    e("开始播放上报成功:" + response);
                                }

                                @Override
                                public void onFailure(int statusCode, String errorMsg) {
                                    e("开始播放上报成功失败原因:" + errorMsg);
                                    if (statusCode < JINGDONG_CODE) {
                                        e("成功");
                                    } else if (statusCode == JINGDONG_REPORT) {
                                        e("超过规定上报时间");
                                    } else {
                                        startJingDong(url);
                                    }
                                }
                            });
                }
            });
        }
    }

    /**
     * 京东结束播放上报
     */
    private void endJingDong(final String url) {
        e("上报路径 stop：" + url);
        if (isRequestJd && url != null) {
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    mMyOkHttp.get()
                            .url(url)
                            .tag(this)
                            .enqueue(new RawResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, String response) {
                                    e("结束播放上报成功:" + statusCode);
                                }

                                @Override
                                public void onFailure(int statusCode, String errorMsg) {
                                    e("结束播放上报失败原因:" + errorMsg);
                                    if (statusCode < JINGDONG_CODE) {
                                        e("成功");
                                    } else if (statusCode == JINGDONG_REPORT) {
                                        e("超过规定上报时间");
                                    } else {
                                        endJingDong(url);
                                    }
                                }
                            });
                }
            });
        }
    }

    /**
     * 京东监播上报
     */
    private void playJingDong(final String url) {
        if (isRequestJd) {
            e("京东播放上报：" + url);
            ThreadManager.getInstance().doExecute(new Runnable() {
                @Override
                public void run() {
                    mMyOkHttp.get()
                            .url(url)
                            .tag(this)
                            .enqueue(new RawResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, String response) {
                                    e("京东监播上报成功:" + response);
                                }

                                @Override
                                public void onFailure(int statusCode, String errorMsg) {
                                    e("京东监播上报失败原因:" + errorMsg);
                                    e("京东监播上报失败状态:" + statusCode);
                                    if (statusCode < JINGDONG_CODE) {
                                        e("成功");
                                    } else if (statusCode == JINGDONG_REPORT) {
                                        e("超过规定上报时间");
                                    } else {
                                        playJingDong(url);
                                    }
                                }
                            });
                }
            });
        }
    }

    /**
     * 京东播放结束上报
     */
    private void jingDongReport() {
        try {
            if (jdType == CONSTANT_TWO) {
                String url = END_JINGDONG + "?requestId=" + stopRequestId + "&ticks=" + System.currentTimeMillis() / 1000;
                endJingDong(url);
                endJingDong(endJdPlay);
                for (int i = 0; i < currentJdPlay.size(); i++) {
                    playJingDong(currentJdPlay.get(i));
                }

                if (readJingDongShow().size() > 1) {
                    int isDelete = LitePal.deleteAll(PlayList.class, "jdStopUrl = ?", endJdPlay);
                    e("删除京东广告清单结果：" + isDelete);
                    if (isDelete > 0) {
                        updateShowList();
                    }
                } else if (!isRequestJd) {
                    int isDelete = LitePal.deleteAll(PlayList.class, "jdStopUrl = ?", endJdPlay);
                    if (isDelete > 0) {
                        updateShowList();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 京东文件下载
     */
    private void fileDownload(final String imageUrlPath, final String imageName,
                              final int playId) {
        String saveDir = FILE_SAVE_URL + imageName;
        mMyOkHttp.download()
                .url(imageUrlPath)
                .filePath(saveDir)
                .tag(this).enqueue(new DownloadResponseHandler() {
            @Override
            public void onFinish(File downloadFile) {
                e("京东广告下载完成,加播放ID：" + imageName + "; " + playId);
                if (checkMd5(imageName)) {
                    updateDownloadState2(imageName);
                    e("京东加入后清单大小：" + showSumSize);
                    jingDongPaly(imageName, playId, showSumSize);
                }
            }

            @Override
            public void onProgress(long currentBytes, long totalBytes) {
            }

            @Override
            public void onFailure(String errorMsg) {
                e("京东广告下载完成失败，原因：" + errorMsg);
                fileDownload(imageUrlPath, imageName, playId);
            }
        });
    }

    /**
     * 京东广告立即播放
     */
    private void jingDongPaly(final String imageName, final int playId, int showSumSize) {
        if (showSumSize == 1) {
            Parameter playUrl = new Parameter();
            playUrl.setPlayUrl(imageName);
            playUrl.updateAll("uniqueness = ?", "Pan");
            this.post(new Runnable() {
                @Override
                public void run() {
                    e("立即播放京东广告");
                    setCount(Integer.toString(playId), 1);
                    currentPlayId = Integer.toString(playId);
                    isJdStart = true;
                    showPlay(imageName, true);
                }
            });
        }
    }

    /**
     * logo加载
     */
    private void appLogoInitialize() {
        ThreadManager.getInstance().doExecute(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = readString(activity, Const.LOGO_SHOW_DATA);
                    e("logo路径：" + url);
                    logoShow(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * logo显示
     */
    private void logoShow(String logoUrl) {
        if (!"".equals(logoUrl) && logoUrl != null) {
            final String mLogoUrl = DOMAIN_NAME2 + logoUrl;
            if (!mLogoUrl.equals(DOMAIN_NAME2) && !mLogoUrl.equals(ILLEGAL_LOGO_URR)) {
                e("最终logo路径：" + mLogoUrl);
                String logoName = logoUrl.substring(logoUrl.lastIndexOf("/") + 1);
                final String saveDir = FILE_SAVE_URL + logoName;
                downloadLogo(saveDir, mLogoUrl);

                ExecutorServiceManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                e("显示路径：");
                                GlideUtil.qrLoad(activity, saveDir, optionsLogo, mAppLogo);
                                mAppLogo.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }, 5, TimeUnit.SECONDS);
            }
        } else {
            this.post(new Runnable() {
                @Override
                public void run() {
                    e("即投logo显示");
                    GlideUtil.load(activity, R.drawable.app_name, optionsLogo, mAppLogo);
                    mAppLogo.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * 下载logo到本地
     */
    private void downloadLogo(String saveDir, String mLogoUrl) {
        if (!fileIsExists(saveDir)) {
            e("logo不存在开启下载");
            Base.pictureDownload(context, mLogoUrl, saveDir);
        }
    }

    /**
     * 停止语音播放
     */
    private void closeAssetMusics() {
        try {
            this.post(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null) {
                        if (mMediaPlayer.isPlaying()) {
                            mMediaPlayer.stop();
                            try {
                                mMediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * ID号显示控制
     */
    private void textZxingVisible() {
        post(new Runnable() {
            @Override
            public void run() {
                List<Parameter> parameter = LitePal.findAll(Parameter.class);
                for (Parameter parameters : parameter) {
                    mZxing.setVisibility(View.GONE);
                    mText.setText(parameters.getDeviceId());
                    mText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 整点报时
     */
    private void initTimePrompt() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        activity.registerReceiver(mTimeReceiver, timeFilter);
        isTimeReceiver = true;
        e("开启整点红包");
    }

    boolean isTimeReceiver = false;
    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final int minNumber = 59;
            final Calendar cal = Calendar.getInstance();
            final int min = cal.get(Calendar.MINUTE);
            if (min == 0) {
                e("是否还有整点红包：" + hasRedPacket);
                if (hasRedPacket) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            String hasRedPacketName = readString(activity, Const.HAS_REDPACKET_NAME);
                            String hasRedPacketNamePath = FILE_SAVE_URL + hasRedPacketName;
                            e("整点红包图片路径：" + hasRedPacketNamePath);
                            if (fileIsExists(hasRedPacketNamePath)) {
                                imagePlay(hasRedPacketName, "JPG", true);
                                openAssetMusics("zhengdian-tts");
                                if (mVideoView.isPlaying()) {
                                    mVideoView.stopPlayback();
                                }
                                ExecutorServiceManager.getInstance().schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        closeAssetMusics();
                                    }
                                }, 15, TimeUnit.SECONDS);
                            }
                        }
                    });
                }
            } else if (min == minNumber) {
                redPacketPrepare(cal);
            }
        }

        /**
         * 整点红包准备
         */
        private void redPacketPrepare(final Calendar cal) {
            List<Parameter> deviceId = LitePal.findAll(Parameter.class);
            for (Parameter deviceIds : deviceId) {
                final String url = GET_REDPACKET_AMOUNT + deviceIds.getDeviceId();
                ThreadManager.getInstance().doExecute(new Runnable() {
                    @Override
                    public void run() {
                        mMyOkHttp.get()
                                .url(url)
                                .tag(this)
                                .enqueue(new RawResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, String response) {
                                        e("获取整点红包的数据:" + response);
                                        try {
                                            String urlDecoder = URLDecoder.decode(response, "UTF-8");
                                            JSONObject json = new JSONObject(urlDecoder);
                                            if (json.getBoolean("success")) {
                                                hasRedPacket = json.getBoolean("hasRedPacket");
                                                hasRedPacketMoney = json.getInt("amount");
                                                SharedPreferencesUtils.writeBoolean(activity, Const.HAS_READPECKET, hasRedPacket);
                                                SharedPreferencesUtils.writeInt(activity, Const.HAS_REDPACKET_MONEY, hasRedPacketMoney);
                                                int second = cal.get(Calendar.SECOND);
                                                hasRedPacketTips(second);
                                            }
                                        } catch (JSONException | UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, String errorMsg) {
                                        e("失败原因:" + errorMsg);
                                        hasRedPacket = SharedPreferencesUtils.readBoolean(activity, Const.HAS_READPECKET);
                                        hasRedPacketMoney = SharedPreferencesUtils.readInt(activity, Const.HAS_REDPACKET_MONEY);
                                        int second = cal.get(Calendar.SECOND);
                                        hasRedPacketTips(second);
                                    }
                                });
                    }
                });
            }
        }
    };

    /**
     * 整点红包提示
     */
    private void hasRedPacketTips(int second) {
        if (hasRedPacketMoney != 0 && hasRedPacket) {
            e("第几秒：" + second);
            String hasRedPacketName = readString(activity, Const.HAS_REDPACKET_NAME);
            String hasRedPacketNamePath = FILE_SAVE_URL + hasRedPacketName;
            e("整点红包图片路径：" + hasRedPacketNamePath);
            if (fileIsExists(hasRedPacketNamePath)) {
                ExecutorServiceManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        e("开始整点红包播放");
                        openAssetMusics("zhengdian2-tts.mp3");
                    }
                }, 30, TimeUnit.SECONDS);
            }
        }
    }

    TimerTask deviceIdTask = new TimerTask() {
        @Override
        public void run() {
            boolean isDeviceId = SharedPreferencesUtils.readBoolean(activity, Const.ISSUCCEED);
            String buttonName = readString(activity, Const.ZXING_BUTTON);
            String button = "ZXing";
            e("二维码加载结果和控件名：" + isDeviceId + "__" + buttonName);
            if (!isDeviceId && button.equals(buttonName)) {
                textZxingVisible();
            }
        }
    };

    /**
     * 内部匿名时段播放时间类
     */
    class PeakParameter {
        String startTime;
        String endTime;
    }

    /**
     * 下标处理
     */
    private void addSelf() {
        index = index + 1;
        if (mIsVip) {
            if (vipShowSum().size() == index || index > vipShowSum().size()) {
                index = 0;
            }
        } else {
            if (showSumSize == index || index > showSumSize) {
                index = 0;
            }
        }
    }

    /**
     * 下标复位
     */
    private void indexReset() {
        if (index > showSumSize || index == showSumSize) {
            e("下标复位");
            index = 0;
        }
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            monitorService = ((MonitorService.ServiceBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            monitorService = null;
        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            jingDongReport();
            e("Play Completed !");

            mIsNewPlay = true;
            if (showSumSize == 0) {
                System.gc();
                setIntentData("playType", 4);
                assignLoop();
            } else {
                System.gc();
                playVideo();
            }
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            KLog.e("播放异常：" + mp.toString());
            KLog.e("播放异常信息：" + what);
            KLog.e("播放异常信息2：" + extra);
            if (showSumSize > 1) {
                nextShow();
            }
            return true;
        }
    };

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mVideoView.start();
        }
    };

}

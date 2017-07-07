package com.zebdar.tom.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zebdar.tom.AppBaseActivity;
import com.zebdar.tom.Const;
import com.zebdar.tom.DropdownListView;
import com.zebdar.tom.ExpressionUtil;
import com.zebdar.tom.FaceVPAdapter;
import com.zebdar.tom.PreferencesUtils;
import com.zebdar.tom.R;
import com.zebdar.tom.SysUtils;
import com.zebdar.tom.ToastUtil;
import com.zebdar.tom.chat.callback.OnMessageChangedListener;
import com.zebdar.tom.chat.model.IMMsg;
import com.zebdar.tom.chat.model.IMessageDao;
import com.zebdar.tom.chat.model.memdb.MessageCache;
import com.zebdar.tom.music.MusicPlayManager;
import com.zebdar.tom.sdk.Lang;
import com.zebdar.tom.speech.SpeechCallback;
import com.zebdar.tom.speech.SpeechInitCallback;
import com.zebdar.tom.speech.SpeechRecognizerUtil;
import com.zebdar.tom.speech.SpeechSynthesizerUtil;
import com.zebdar.tom.utils.ActionSheetBottomDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 聊天界面
 *
 * @author 白玉梁
 * @blog http://blog.csdn.net/baiyuliang2013
 * @weibo http://weibo.com/2611894214/profile?topnav=1&wvr=6&is_all=1
 */
@SuppressLint("SimpleDateFormat")
public class ChatActivity extends AppBaseActivity implements DropdownListView.OnRefreshListenerHeader,
        ChatAdapter.OnClickMsgListener {
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private EditText input;
    private TextView send;
    private DropdownListView mListView;
    private ChatAdapter mLvAdapter;
    private IMessageDao msgDao;

    private LinearLayout chat_face_container, chat_add_container;
    private ImageView image_face;//表情图标
    private ImageView image_add;//更多图标
    private ImageView image_voice;//语音
    private TextView tv_weather,//图片
            tv_xingzuo,//拍照
            tv_joke,//笑话
            tv_loc,//位置
            tv_gg,//帅哥
            tv_mm,//美女
            tv_music;//歌曲

    private LinearLayout ll_playing;//顶部正在播放布局
    private TextView tv_playing;

    //表情图标每页6列4行
    private int columns = 6;
    private int rows = 4;
    //每页显示的表情view
    private List<View> views = new ArrayList<View>();
    //表情列表
    private List<String> staticFacesList;
    //消息
    private List<IMMsg> listMsg;
    private SimpleDateFormat sd;
    private LayoutInflater inflater;
    private int offset;

    //发送者和接收者固定为小Q和自己
    private final String from = "xiaoq";//来自小Q
    private final String to = "master";//发送者为自己

    FinalHttp fh;
    AjaxParams ajaxParams;

    //在线音乐播放工具类
    MusicPlayManager musicPlayManager;
    // 语音听写工具
    SpeechRecognizerUtil speechRecognizerUtil;
    // 语音合成工具
    SpeechSynthesizerUtil speechSynthesizerUtil;

    String voice_type;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mLvAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_chat);
        initTitleBar("消息", "小Q", "", this);
        musicPlayManager = new MusicPlayManager();
        fh = new FinalHttp();
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sd = new SimpleDateFormat("MM-dd HH:mm");
        msgDao = new MessageCache(); //new ChatMsgDao(this);
        staticFacesList = ExpressionUtil.initStaticFaces(this);
        voice_type = PreferencesUtils.getSharePreStr(this, Const.IM_VOICE_TPPE);
        //初始化控件
        initViews();
        //初始化表情
        initViewPager();
        //初始化更多选项（即表情图标右侧"+"号内容）
        initAdd();
        //初始化数据
        initData();
        //初始化语音听写及合成部分
        initSpeech();


        MessageCenter.getDefault().addOnMessageRemoteListener(new OnMessageChangedListener() {
            @Override
            public void onAdd(IMMsg msg) {
                listMsg.add(msg);
                offset = listMsg.size();
                mLvAdapter.notifyDataSetChanged();
                input.setText("");
            }

            @Override
            public void onDelete(IMMsg m) {
                listMsg.remove(m);
                offset = listMsg.size();
                mLvAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUpdate(IMMsg m) {

            }

            @Override
            public void onLoading(IMMsg m, boolean isFinish, int progress) {

            }
        });
    }

    private void initSpeech() {
        speechRecognizerUtil = new SpeechRecognizerUtil(this, new SpeechInitCallback() {
            @Override
            public void onInitOk() {
            }

            @Override
            public void onInitFail(int code) {
                ToastUtil.showToast(ChatActivity.this, "初始化失败，错误码：" + code);
            }
        });
        speechSynthesizerUtil = new SpeechSynthesizerUtil(this);
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        ll_playing = (LinearLayout) findViewById(R.id.ll_playing);
        tv_playing = (TextView) findViewById(R.id.tv_playing);

        mListView = (DropdownListView) findViewById(R.id.message_chat_listview);
        SysUtils.setOverScrollMode(mListView);

        image_face = (ImageView) findViewById(R.id.image_face); //表情图标
        image_add = (ImageView) findViewById(R.id.image_add);//更多图标
        image_voice = (ImageView) findViewById(R.id.image_voice);//语音
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);//表情布局
        chat_add_container = (LinearLayout) findViewById(R.id.chat_add_container);//更多

        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        //表情下小圆点
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
        input = (EditText) findViewById(R.id.input_sms);
        send = (TextView) findViewById(R.id.send_sms);
        input.setOnClickListener(this);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    send.setVisibility(View.VISIBLE);
                    image_voice.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    image_voice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        image_face.setOnClickListener(this);//表情按钮
        image_add.setOnClickListener(this);//更多按钮
        image_voice.setOnClickListener(this);//语音按钮
        send.setOnClickListener(this); // 发送

        mListView.setOnRefreshListenerHead(this);
        mListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    if (chat_face_container.getVisibility() == View.VISIBLE) {
                        chat_face_container.setVisibility(View.GONE);
                    }
                    if (chat_add_container.getVisibility() == View.VISIBLE) {
                        chat_add_container.setVisibility(View.GONE);
                    }
                    hideSoftInputView();
                }
                return false;
            }
        });
    }

    public void initAdd() {
        tv_weather = (TextView) findViewById(R.id.tv_weather);
        tv_xingzuo = (TextView) findViewById(R.id.tv_xingzuo);
        tv_joke = (TextView) findViewById(R.id.tv_joke);
        tv_loc = (TextView) findViewById(R.id.tv_loc);
        tv_gg = (TextView) findViewById(R.id.tv_gg);
        tv_mm = (TextView) findViewById(R.id.tv_mm);
        tv_music = (TextView) findViewById(R.id.tv_music);

        tv_weather.setOnClickListener(this);
        tv_xingzuo.setOnClickListener(this);
        tv_joke.setOnClickListener(this);
        tv_loc.setOnClickListener(this);
        tv_gg.setOnClickListener(this);
        tv_mm.setOnClickListener(this);
        tv_music.setOnClickListener(this);
    }

    public void initData() {
        offset = 0;
        listMsg = msgDao.queryMsg(from, to, offset);
        offset = listMsg.size();
        mLvAdapter = new ChatAdapter(this, listMsg, this);
        mListView.setAdapter(mLvAdapter);
        mListView.setSelection(listMsg.size());
    }

    /**
     * 初始化表情
     */
    private void initViewPager() {
        int pagesize = ExpressionUtil.getPagerCount(staticFacesList.size(), columns, rows);
        // 获取页数
        for (int i = 0; i < pagesize; i++) {
            views.add(ExpressionUtil.viewPagerItem(this, i, staticFacesList, columns, rows, input));
            LayoutParams params = new LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    /**
     * 表情页切换时，底部小圆点
     *
     * @param position
     * @return
     */
    private ImageView dotsItem(int position) {
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }


    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        IMMsg msg = null;
        switch (arg0.getId()) {
            case R.id.send_sms://发送
                String content = input.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                //sendMsgText(content, true);
                msg = MessageHelper.createTextMessage(content, from, to, true);
                MessageCenter.getDefault().send(msg);
                break;
            case R.id.input_sms://点击输入框
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face://点击表情按钮
                hideSoftInputView();//隐藏软键盘
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_add://点击加号按钮
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.GONE) {
                    chat_add_container.setVisibility(View.VISIBLE);
                } else {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_voice://点击语音按钮

                speechRecognizerUtil.recordAndTranslate(new SpeechCallback() {
                    @Override
                    public void onRecognizedOk(String textResult, String audioPath, boolean isLast) {
                        IMMsg msg = MessageHelper.createVoiceMessage(audioPath + Const.SPILT + textResult, from, to, true);
                        MessageCenter.getDefault().send(msg);

                        msg = MessageHelper.createTextMessage(textResult, from, to, true);
                        MessageCenter.getDefault().send(msg);

                        if(Lang.isEquals(textResult, "四哥")){
                            speechSynthesizerUtil.speech("干啥");
                        }

                    }

                    @Override
                    public void onRecognizedFail(String reason) {
                        ToastUtil.showToast(getApplication(), reason);
                    }
                });

                break;
            case R.id.tv_weather:
                msg = MessageHelper.createTextMessage(PreferencesUtils.getSharePreStr(this, Const.CITY) + "天气", from, to, true);
                MessageCenter.getDefault().send(msg);
                break;
            case R.id.tv_xingzuo:
                input.setText("星座#");
                input.setSelection(input.getText().toString().length());//光标移至最后

                chat_add_container.setVisibility(View.GONE);
                showSoftInputView(input);
                break;
            case R.id.tv_joke:
                msg = MessageHelper.createTextMessage("笑话", from, to, true);
                MessageCenter.getDefault().send(msg);
                break;
            case R.id.tv_loc:
                msg = MessageHelper.createTextMessage("位置", from, to, true);
                MessageCenter.getDefault().send(msg);

                String lat = PreferencesUtils.getSharePreStr(this, Const.LOCTION);//经纬度
                if (TextUtils.isEmpty(lat)) {
                    lat = "116.404,39.915";//北京
                }

                String imgUrl = Const.LOCATION_URL_S + lat + "&markers=|" + lat + "&markerStyles=l,A,0xFF0000";
                msg = MessageHelper.createLocationMessage(imgUrl, from, to, false);
                MessageCenter.getDefault().onReceive(msg);
                break;
            case R.id.tv_gg:

                break;
            case R.id.tv_mm:

                break;
            case R.id.tv_music:
                input.setText("歌曲##");
                input.setSelection(input.getText().toString().length() - 1);
                chat_add_container.setVisibility(View.GONE);
                showSoftInputView(input);
                break;
        }
    }


    @Override
    public void click(int position) {//点击
        IMMsg msg = listMsg.get(position);
        switch (msg.getType()) {
            case MessageTypes.MSG_TYPE_TEXT://文本
                break;
            case MessageTypes.MSG_TYPE_IMG://图片
                break;
            case MessageTypes.MSG_TYPE_LOCATION://位置
                ToastUtil.showToast(this, "--MSG_TYPE_LOCATION--");
//                Intent intent = new Intent(this, ImgPreviewActivity.class);
//                intent.putExtra("url", msg.getContent());
//                startActivity(intent);
                break;
            case MessageTypes.MSG_TYPE_VOICE://语音

                break;
        }
    }

    @Override
    public void longClick(int position) {//长按
        IMMsg msg = listMsg.get(position);
        switch (msg.getType()) {
            case MessageTypes.MSG_TYPE_TEXT://文本
                clip(msg, position);
                break;
            case MessageTypes.MSG_TYPE_IMG://图片
                break;
            case MessageTypes.MSG_TYPE_LOCATION://位置
            case MessageTypes.MSG_TYPE_VOICE://语音
                delonly(msg, position);
                break;
        }
    }

    /**
     * 带复制文本的操作
     */
    void clip(final IMMsg msg, final int position) {
        ToastUtil.showToast(this, "带复制文本");
        new ActionSheetBottomDialog(this)
                .builder()
                .addSheetItem("复制", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        Lang.copyToClipboard(msg.getContent());
                        ToastUtil.showToast(ChatActivity.this, "已复制到剪切板");
                    }
                })
                .addSheetItem("朗读", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        speechSynthesizerUtil.speech(msg.getContent());
                    }
                })
                .addSheetItem("删除", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        MessageCenter.getDefault().delete(msg);
                    }
                })
                .show();
    }

    /**
     * 仅有删除操作
     */
    void delonly(final IMMsg msg, final int position) {

        ToastUtil.showToast(this, "仅有删除操作");
        new ActionSheetBottomDialog(this)
                .builder()
                .addSheetItem("删除", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        MessageCenter.getDefault().delete(msg);
//                        msgDao.deleteMsgById(msg.getMsgId());
                    }
                })
                .show();
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }
    }

    /**
     * 下拉加载更多
     */
    @Override
    public void onRefresh() {
        List<IMMsg> list = msgDao.queryMsg(from, to, offset);
        if (list.size() <= 0) {
            mListView.setSelection(0);
            mListView.onRefreshCompleteHeader();
            return;
        }
        listMsg.addAll(0, list);
        offset = listMsg.size();
        mListView.onRefreshCompleteHeader();
        mLvAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size());
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //让输入框获取焦点
                input.requestFocus();
                if (chat_face_container.getVisibility() == View.VISIBLE || chat_add_container.getVisibility() == View.VISIBLE) {
                    hideSoftInputView();
                }
            }
        }, 100);

    }

    /**
     * 监听返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideSoftInputView();
            if (chat_face_container.getVisibility() == View.VISIBLE) {
                chat_face_container.setVisibility(View.GONE);
            } else if (chat_add_container.getVisibility() == View.VISIBLE) {
                chat_add_container.setVisibility(View.GONE);
            } else {
                if (musicPlayManager != null && musicPlayManager.isPlaying()) {
                    musicPlayManager.stop();
                }
                if (speechSynthesizerUtil != null) {
                    speechSynthesizerUtil.stopSpeech();
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

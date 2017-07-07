package com.zebdar.tom.chat;

import android.content.Context;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zebdar.tom.BaseListAdapter;
import com.zebdar.tom.CircleImageView;
import com.zebdar.tom.Const;
import com.zebdar.tom.ExpressionUtil;
import com.zebdar.tom.R;
import com.zebdar.tom.RecordPlayClickListener;
import com.zebdar.tom.ViewHolder;
import com.zebdar.tom.chat.model.IMMsg;

import net.tsz.afinal.FinalBitmap;

import java.util.List;


/**
 * 聊天适配器
 *
 * @author baiyuliang
 * @ClassName: MessageChatAdapter
 */
public class ChatAdapter extends BaseListAdapter<IMMsg> {

    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    //private final int TYPE_SEND_TXT = 1;
    //图片
    //private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 1;
    //位置
    //private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 2;
    //语音
    //private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 3;

    private FinalBitmap finalImageLoader;
    OnClickMsgListener onClickMsgListener;

    public ChatAdapter(Context context, List<IMMsg> msgList, OnClickMsgListener onClickMsgListener) {
        super(context, msgList);
        mContext = context;
        finalImageLoader = FinalBitmap.create(context);
        this.onClickMsgListener = onClickMsgListener;
    }

    //获取item类型
    @Override
    public int getItemViewType(int position) {
        IMMsg msg = list.get(position);
        switch (msg.getType()) {
            case MessageTypes.MSG_TYPE_TEXT:
                return TYPE_RECEIVER_TXT; //msg.getIsComing() == 0 ? TYPE_RECEIVER_TXT : TYPE_SEND_TXT;
            case MessageTypes.MSG_TYPE_IMG:
                return TYPE_RECEIVER_IMAGE; //msg.getIsComing() == 0 ? TYPE_RECEIVER_IMAGE : TYPE_SEND_IMAGE;
            case MessageTypes.MSG_TYPE_LOCATION:
                return TYPE_RECEIVER_LOCATION; //msg.getIsComing() == 0 ? TYPE_RECEIVER_LOCATION : TYPE_SEND_LOCATION;
            case MessageTypes.MSG_TYPE_VOICE:
                return TYPE_RECEIVER_VOICE; //msg.getIsComing() == 0 ? TYPE_RECEIVER_VOICE : TYPE_SEND_VOICE;
            default:
                return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 10;
    }

    /**
     * 根据消息类型，使用对应布局
     *
     * @param msg
     * @param position
     * @return
     */
    private View createViewByType(IMMsg msg, int position) {
        switch (msg.getType()) {
            case MessageTypes.MSG_TYPE_TEXT://文本
                return createView(R.layout.item_chat_text_rece);
            case MessageTypes.MSG_TYPE_IMG://图片
                return createView(R.layout.item_chat_image_rece);
            case MessageTypes.MSG_TYPE_LOCATION://位置
                return createView(R.layout.item_chat_location_rece);
            case MessageTypes.MSG_TYPE_VOICE://语音
                return createView(R.layout.item_chat_voice_rece);
            default:
                return null;
        }
    }

    private View createView(int id) {
        return mInflater.inflate(id, null);
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        final IMMsg msg = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(msg, position);
        }


        ImageView iv_image = ViewHolder.get(convertView, R.id.iv_image);//图片
        ImageView iv_location = ViewHolder.get(convertView, R.id.iv_location);//位置

        LinearLayout layout_voice = ViewHolder.get(convertView, R.id.layout_voice);//语音 语音播放按钮父控件
        ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);//动画
        ImageView iv_fy = ViewHolder.get(convertView, R.id.iv_fy);//翻译按钮
        final TextView tv_fy = ViewHolder.get(convertView, R.id.tv_fy);//翻译内容



        /** 公共区域 */
        CircleImageView head_view = ViewHolder.get(convertView, R.id.head_view);//头像
        TextView chat_time = ViewHolder.get(convertView, R.id.chat_time);//时间
        chat_time.setText(msg.getDate());//时间
        if(msg.getIsComing() == 0){
            head_view.setImageResource(R.drawable.head_default);
        }else{
            head_view.setImageResource(R.drawable.miniq_logo);
        }

        switch (msg.getType()) {
            case MessageTypes.MSG_TYPE_TEXT://文本
                TextView tv_text = ViewHolder.get(convertView, R.id.tv_text);//文本
                View body_container = ViewHolder.get(convertView, R.id.body_container);//文本
                tv_text.setText(ExpressionUtil.prase(mContext, tv_text, msg.getContent()));
                Linkify.addLinks(tv_text, Linkify.ALL);
                tv_text.setOnClickListener(new onClick(position));
                tv_text.setOnLongClickListener(new onLongCilck(position));

                if(msg.getIsComing() == 0){
                    body_container.setBackgroundResource(R.drawable.sel_chat_item_bg_me);
                }else{
                    body_container.setBackgroundResource(R.drawable.sel_chat_item_bg_others);
                }
                break;
            case MessageTypes.MSG_TYPE_IMG://图片
                finalImageLoader.display(iv_image, msg.getContent());
                iv_image.setOnClickListener(new onClick(position));
                iv_image.setOnLongClickListener(new onLongCilck(position));
                break;
            case MessageTypes.MSG_TYPE_LOCATION://位置
                finalImageLoader.display(iv_location, msg.getContent());
                iv_location.setOnClickListener(new onClick(position));
                iv_location.setOnLongClickListener(new onLongCilck(position));
                break;
            case MessageTypes.MSG_TYPE_VOICE://语音
                final String[] _content = msg.getContent().split(Const.SPILT);
                tv_fy.setText(_content[1]);
                tv_fy.setVisibility(View.GONE);
                layout_voice.setOnClickListener(new RecordPlayClickListener(mContext, iv_voice, _content[0]));
                iv_fy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tv_fy.getVisibility() == View.GONE) {
                            tv_fy.setVisibility(View.VISIBLE);
                        } else {
                            tv_fy.setVisibility(View.GONE);
                        }
                    }
                });
                layout_voice.setOnLongClickListener(new onLongCilck(position));
                break;
        }

        return convertView;
    }



    /**
     * 屏蔽listitem的所有事件
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }


    /**
     * 点击监听
     *
     * @author 白玉梁
     */
    class onClick implements View.OnClickListener {
        int position;

        public onClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            onClickMsgListener.click(position);
        }

    }

    /**
     * 长按监听
     *
     * @author 白玉梁
     */
    class onLongCilck implements View.OnLongClickListener {
        int position;

        public onLongCilck(int position) {
            this.position = position;
        }

        @Override
        public boolean onLongClick(View arg0) {
            onClickMsgListener.longClick(position);
            return true;
        }
    }

    public interface OnClickMsgListener {
        void click(int position);

        void longClick(int position);
    }

}

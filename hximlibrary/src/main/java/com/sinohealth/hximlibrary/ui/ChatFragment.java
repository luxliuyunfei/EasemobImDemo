package com.sinohealth.hximlibrary.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMChatRoomChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.sinohealth.hximlibrary.R;
import com.sinohealth.hximlibrary.adapter.MessageAdapter;
import com.sinohealth.hximlibrary.view.PasteEditText;

import java.io.File;
import java.util.List;

/**
 * Created by JJfly on 2015/11/12.
 * 聊天界面fragment
 */
public class ChatFragment extends Fragment implements View.OnClickListener{

    private String toChatUsername = "123654";

    public static final String COPY_IMAGE = "EASEMOBIMG";
    public static final int CHATTYPE_SINGLE = 1;//单聊


    private int chatType;
    private EMConversation conversation;
    private final int pagesize = 20;// 一页消息数量


    private MessageAdapter adapter;
    private InputMethodManager manager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.chat_activity,null);
        initView(contentView);
        setUpView();
        return contentView;
    }
    private View recordingContainer;
    private View buttonSetModeKeyboard;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private View more;
    private ImageView micImage;
    private TextView recordingHint;
    private ListView listView;
    private PasteEditText mEditTextContent;
    private RelativeLayout edittext_layout;
    private ViewPager expressionViewpager;
    private LinearLayout emojiIconContainer;
    private LinearLayout btnContainer;
    private ImageView locationImgview;
    private ImageView voiceCallBtn;
    private ImageView videoCallBtn;
    private ImageView iv_emoticons_normal;
    private ImageView iv_emoticons_checked;
    private ProgressBar loadmorePB;
    private Button btnMore;

    private void initView(View view){
        recordingContainer = view.findViewById(R.id.recording_container);
        micImage = (ImageView) view.findViewById(R.id.mic_image);
        recordingHint = (TextView) view.findViewById(R.id.recording_hint);
        listView = (ListView) view.findViewById(R.id.list);
        mEditTextContent = (PasteEditText) view.findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = view.findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) view.findViewById(R.id.edittext_layout);
        buttonSetModeVoice = view.findViewById(R.id.btn_set_mode_voice);
        buttonSend = view.findViewById(R.id.btn_send);
        buttonPressToSpeak = view.findViewById(R.id.btn_press_to_speak);
        expressionViewpager = (ViewPager) view.findViewById(R.id.vPager);
        emojiIconContainer = (LinearLayout) view.findViewById(R.id.ll_face_container);
        btnContainer = (LinearLayout) view.findViewById(R.id.ll_btn_container);
        locationImgview = (ImageView) view.findViewById(R.id.btn_location);
        iv_emoticons_normal = (ImageView) view.findViewById(R.id.iv_emoticons_normal);
        iv_emoticons_checked = (ImageView) view.findViewById(R.id.iv_emoticons_checked);
        loadmorePB = (ProgressBar) view.findViewById(R.id.pb_load_more);
        btnMore = (Button) view.findViewById(R.id.btn_more);
        iv_emoticons_normal.setVisibility(View.VISIBLE);
        iv_emoticons_checked.setVisibility(View.INVISIBLE);
        more = view.findViewById(R.id.more);
        edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
        voiceCallBtn = (ImageView) view.findViewById(R.id.btn_voice_call);
        videoCallBtn = (ImageView) view.findViewById(R.id.btn_video_call);

        mEditTextContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_normal);
                }

            }
        });

        mEditTextContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edittext_layout.setBackgroundResource(R.drawable.input_bar_bg_active);
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
            }
        });

        //监听文字
        mEditTextContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    btnMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    btnMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buttonSend.setOnClickListener(this);
    }
    private void setUpView(){
        chatType = CHATTYPE_SINGLE;
        if (chatType == CHATTYPE_SINGLE) { // 单聊
              //设置聊天头像，名字之类之类的
        }

        onConversationInit();

        onListViewCreation();
    }
    protected void onConversationInit() {
        if (chatType == CHATTYPE_SINGLE) {
            conversation = EMChatManager.getInstance().getConversationByType(toChatUsername, EMConversation.EMConversationType.Chat);
        }
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();

        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            if (chatType == CHATTYPE_SINGLE) {
                conversation.loadMoreMsgFromDB(msgId, pagesize);
            } else {
                conversation.loadMoreGroupMsgFromDB(msgId, pagesize);
            }
        }

        EMChatManager.getInstance().addChatRoomChangeListener(new EMChatRoomChangeListener() {

            @Override
            public void onChatRoomDestroyed(String roomId, String roomName) {
                if (roomId.equals(toChatUsername)) {

                }
            }

            @Override
            public void onMemberJoined(String roomId, String participant) {
            }

            @Override
            public void onMemberExited(String roomId, String roomName,
                                       String participant) {

            }

            @Override
            public void onMemberKicked(String roomId, String roomName,
                                       String participant) {
                if (roomId.equals(toChatUsername)) {
                    String curUser = EMChatManager.getInstance().getCurrentUser();
                    if (curUser.equals(participant)) {
                        EMChatManager.getInstance().leaveChatRoom(toChatUsername);
                    }
                }
            }

        });
    }


    protected void onListViewCreation(){
        adapter = new MessageAdapter(getActivity(), toChatUsername, chatType);
        // 显示消息
        listView.setAdapter(adapter);

        listView.setOnScrollListener(new ListScrollListener());
        adapter.refreshSelectLast();

        listView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                more.setVisibility(View.GONE);
                iv_emoticons_normal.setVisibility(View.VISIBLE);
                iv_emoticons_checked.setVisibility(View.INVISIBLE);
                emojiIconContainer.setVisibility(View.GONE);
                btnContainer.setVisibility(View.GONE);
                return false;
            }
        });
    }

    /**
     * 隐藏软键盘
     */
    private void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send) {// 点击发送按钮(发文字和表情)
            String s = mEditTextContent.getText().toString();
            sendText(s);
        }
    }

    private class ListScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				/*if (view.getFirstVisiblePosition() == 0 && !isloading && haveMoreData && conversation.getAllMessages().size() != 0) {
					isloading = true;
					loadmorePB.setVisibility(View.VISIBLE);
					// sdk初始化加载的聊天记录为20条，到顶时去db里获取更多
					List<EMMessage> messages;
					EMMessage firstMsg = conversation.getAllMessages().get(0);
					try {
						// 获取更多messges，调用此方法的时候从db获取的messages
						// sdk会自动存入到此conversation中
						if (chatType == CHATTYPE_SINGLE)
							messages = conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), pagesize);
						else
							messages = conversation.loadMoreGroupMsgFromDB(firstMsg.getMsgId(), pagesize);
					} catch (Exception e1) {
						loadmorePB.setVisibility(View.GONE);
						return;
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					if (messages.size() != 0) {
						// 刷新ui
						if (messages.size() > 0) {
							adapter.refreshSeekTo(messages.size() - 1);
						}

						if (messages.size() != pagesize)
							haveMoreData = false;
					} else {
						haveMoreData = false;
					}
					loadmorePB.setVisibility(View.GONE);
					isloading = false;

				}*/
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }

    }


    /**
     * 发送消息
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg = EMChatManager.getInstance().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                // 获取消息内容，发送消息
                String content = ((TextMessageBody) forward_msg.getBody()).getMessage();
                sendText(content);
                break;
            case IMAGE:
                // 发送图片
                String filePath = ((ImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // 不存在大图发送缩略图
                       // filePath = ImageUtils.getThumbnailImagePath(filePath);
                    }
                  //  sendPicture(filePath);
                }
                break;
            default:
                break;
        }

        if(forward_msg.getChatType() == EMMessage.ChatType.ChatRoom){
            EMChatManager.getInstance().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * 发送文本消息
     *
     * @param content
     *            message content
     * @param isResend
     *            boolean resend
     */
    public void sendText(String content) {

        if (content.length() > 0) {
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            // 如果是群聊，设置chattype,默认是单聊
//            if (chatType == CHATTYPE_GROUP){
//                message.setChatType(EMMessage.ChatType.GroupChat);
//            }else if(chatType == CHATTYPE_CHATROOM){
//                message.setChatType(EMMessage.ChatType.ChatRoom);
//            }
//            if(isRobot){
//                message.setAttribute("em_robot_message", true);
//            }
            TextMessageBody txtBody = new TextMessageBody(content);
            // 设置消息body
            message.addBody(txtBody);
            // 设置要发给谁,用户username或者群聊groupid
            message.setReceipt(toChatUsername);
            // 把messgage加到conversation中
            conversation.addMessage(message);
            // 通知adapter有消息变动，adapter会根据加入的这条message显示消息和调用sdk的发送方法
            adapter.refreshSelectLast();
            mEditTextContent.setText("");
            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(),"发送成功",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
            //setResult(RESULT_OK);

        }
    }
}

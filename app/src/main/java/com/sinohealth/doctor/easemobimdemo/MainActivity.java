package com.sinohealth.doctor.easemobimdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.sinohealth.hximlibrary.LibApplication;
import com.sinohealth.hximlibrary.controller.ImLibHelper;
import com.sinohealth.hximlibrary.ui.ChatActivity;

/**
 * Created by JJfly on 2015/11/3.
 */
public class MainActivity  extends Activity implements View.OnClickListener{
    private Button loginBtn,registerBtn;
    private EditText idEt,pwdEt;

    public static String ID="";
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById();
    }

    private void findViewById(){
        loginBtn = (Button)this.findViewById(R.id.loginBtn);
        registerBtn = (Button)this.findViewById(R.id.registerBtn);

        idEt = (EditText)this.findViewById(R.id.idEt);
        pwdEt = (EditText)this.findViewById(R.id.pwdEt);
        pwdEt = (EditText)this.findViewById(R.id.pwdEt);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        flag = getIntent().getBooleanExtra("xx",false);
        if (flag){
            loginBtn.setVisibility(View.GONE);
            registerBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Toast.makeText(this,"onSaveInstanceState",Toast.LENGTH_SHORT).show();
        String id =idEt.getText().toString();
        outState.putString("id",id);
        super.onSaveInstanceState(outState);
    }

    private String username,pwd;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginBtn:
                username = idEt.getText().toString();
                pwd = pwdEt.getText().toString();
                login();
                break;
            case  R.id.registerBtn:
                username = idEt.getText().toString();
                pwd = pwdEt.getText().toString();
                register();
                break;
        }
    }

    private void login(){
        EMChatManager.getInstance().login(username, pwd, new EMCallBack() {

            @Override
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onSuccess", 1).show();
                    }
                });

                // 登陆成功，保存用户名密码
                LibApplication.getInstance().setUserName(username);
                LibApplication.getInstance().setPassword(pwd);

                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    // 处理好友和群组
                   // initializeContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ImLibHelper.getInstance().logout(true,null);
                            Toast.makeText(getApplicationContext(), "登录失败", 1).show();
                        }
                    });
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        LibApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                // 进入主页面
                Intent intent = new Intent(MainActivity.this,
                        ChatActivity.class);
                startActivity(intent);

            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "login failed" + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void register(){
        new Thread(new Runnable() {
            public void run() {
                try {
                    Looper.prepare();
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(username, pwd);
                    Looper.loop();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // 保存用户名
                            LibApplication.getInstance().setUserName(username);
                            Toast.makeText(getApplicationContext(), "Register OK", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode=e.getErrorCode();
                            e.printStackTrace();
                            if(errorCode== EMError.NONETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), "NONETWORK_ERROR", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXISTS){
                                Toast.makeText(getApplicationContext(), "USER_ALREADY_EXISTS", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.UNAUTHORIZED){
                                Toast.makeText(getApplicationContext(), "UNAUTHORIZED", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.ILLEGAL_USER_NAME){
                                Toast.makeText(getApplicationContext(), "ILLEGAL_USER_NAME",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "" +  e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }



    private void showToast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}

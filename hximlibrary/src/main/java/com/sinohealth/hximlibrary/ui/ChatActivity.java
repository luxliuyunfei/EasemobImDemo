package com.sinohealth.hximlibrary.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.sinohealth.hximlibrary.R;

/**
 * Created by JJfly on 2015/11/12.
 */
public class ChatActivity extends FragmentActivity {
    private ChatFragment chatFragment;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_chat);
        chatFragment = new ChatFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.frameLayout, chatFragment, "chat");
        transaction.show(chatFragment);
        transaction.commit();
    }

}

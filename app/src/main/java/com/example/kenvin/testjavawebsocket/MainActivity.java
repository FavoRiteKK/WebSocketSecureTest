package com.example.kenvin.testjavawebsocket;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kenvin.testjavawebsocket.chat.AndroidAsyncWebSocketClient;
import com.example.kenvin.testjavawebsocket.chat.ChatClient;

import org.java_websocket.WebSocketImpl;

public class MainActivity extends AppCompatActivity implements ChatClient.TestWebSocketListener {
    private LinearLayout mMessageLos;
    private ChatClient mChatClient;
    private EditText mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        mMessageLos = (LinearLayout) findViewById(R.id.messages_log);
        mInput = (EditText) findViewById(R.id.input);

        WebSocketImpl.DEBUG = true;
        mChatClient = new ChatClient(this);
        mChatClient.setSocketListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatClient != null) {
            mChatClient.close();
        }
    }

    public void clearMessageLog(View view) {
        mMessageLos.removeAllViews();
    }

    public void sendMessage(View view) {
        mChatClient.sendMessage(mInput.getText().toString());
//        onMessage("Test");
    }

    @Override
    public void onMessage(String message) {
        final TextView textView = new TextView(this);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageLos.addView(textView);
                mMessageLos.invalidate();
            }
        });
    }

    public void connectToChatServer(View view) {
        mChatClient.connect();
    }
}

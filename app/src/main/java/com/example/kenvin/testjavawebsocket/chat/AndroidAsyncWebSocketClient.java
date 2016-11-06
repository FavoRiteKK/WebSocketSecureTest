package com.example.kenvin.testjavawebsocket.chat;

import android.app.ProgressDialog;

import com.example.kenvin.testjavawebsocket.MainActivity;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

/**
 * Created by kenvin on 05/11/2016.
 */
public class AndroidAsyncWebSocketClient extends ChatClient {

    public static final String URL_WEBSOCKET = "wss://echo.websocket.org";
//    public static final String URL_WEBSOCKET = "wss://192.168.0.106:8887";
    private final MainActivity mActivity;

    public AndroidAsyncWebSocketClient(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void connect() {

        final ProgressDialog dialog = ProgressDialog.show(mActivity, null, "Connecting...");

        AsyncHttpClient.getDefaultInstance().websocket(URL_WEBSOCKET, "my-protocol", new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (ex != null) {
                    ex.printStackTrace();
                    return;
                }
                webSocket.send("a string");
                webSocket.send(new byte[10]);
                webSocket.setStringCallback(new WebSocket.StringCallback() {
                    public void onStringAvailable(String s) {
                        if (socketListener != null) {
                            socketListener.onMessage("I got a string: " + s);
                        }
                    }
                });
                webSocket.setDataCallback(new DataCallback() {
                    public void onDataAvailable(DataEmitter emitter, ByteBufferList byteBufferList) {
                        if (socketListener != null) {
                            socketListener.onMessage("I got some bytes!");
                        }
                        // note that this data has been read
                        byteBufferList.recycle();
                    }
                });
            }
        });
    }

}

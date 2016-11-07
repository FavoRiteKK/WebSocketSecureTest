package com.example.kenvin.testjavawebsocket.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

public class ChatClient {
//    public static final String URL_WEBSOCKET = "wss://sc.livede55.com:443/socket/member";
    public static final String URL_WEBSOCKET = "wss://192.168.11.72:8887";
//    public static final String URL_WEBSOCKET = "wss://echo.websocket.org";
    public static final String TAG = "IDK-ChatClient";

    private Context mContext;
    private MyWebSocketClient webSocketClient;
    protected TestWebSocketListener socketListener;
    private ProgressDialog mDialog;

    public ChatClient(Context context) {
        mContext = context;
	}

    public void setSocketListener(TestWebSocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public void sendMessage(String message) {
        if( webSocketClient != null ) {
            webSocketClient.send( message );
        }
    }

    public void connect() {
        try {
            Log.d(TAG, "connecting...");

            // webSocketClient = new ChatClient(new URI(uriField.getText()), area, ( Draft ) draft.getSelectedItem() );
            webSocketClient = new MyWebSocketClient(URI.create(URL_WEBSOCKET));
            SSLSocketFactory factory = TrustAllSSLContext.getSocketFactory();
            if (factory != null) {
                webSocketClient.setSocket(factory.createSocket());

                mDialog = ProgressDialog.show(mContext, null, "Connecting...");
                webSocketClient.connect();
            }

        } catch ( Exception ex ) {
            Log.e(TAG, "connection exception: ", ex);
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }
    }

	public void close() {
        Log.d(TAG, "close connection...");
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public interface TestWebSocketListener {
        void onMessage(String message);
    }

    class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI, new Draft_17(), null, 400);  //timeout = 10s
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "connection opened");
            if (mDialog != null) {
                mDialog.dismiss();
            }
            onMessage("You are connected to Chat server " + getURI());
        }

        @Override
        public void onMessage(String message) {
            Log.d(TAG, "onMessage " + message);
            if (socketListener != null) {
                socketListener.onMessage(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d(TAG, "onClose");
            if (mDialog != null) {
                mDialog.dismiss();
            }
            onMessage("You are disconnected from Chat server " + getURI());
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "event onError: ", ex);
            if (mDialog != null) {
                mDialog.dismiss();
            }
            onMessage("Exception in Chat server " + getURI());
            onMessage(ex.getMessage());
        }
    }
}
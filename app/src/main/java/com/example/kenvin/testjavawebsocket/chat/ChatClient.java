package com.example.kenvin.testjavawebsocket.chat;

import android.content.Context;
import android.util.Log;


import com.example.kenvin.testjavawebsocket.R;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class ChatClient {
    public static final String URL_WEBSOCKET = "wss://192.168.0.105:8887";
//    public static final String URL_WEBSOCKET = "wss://echo.websocket.org";
    public static final String TAG = "IDK-ChatClient";

    private Context mContext;
    private MyWebSocketClient webSocketClient;
    protected TestWebSocketListener socketListener;

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

            // load up the key store
            String STORETYPE = "BKS";
            String KEYSTORE = "mykeystore.bks";
            String STOREPASSWORD = "qweasd123";
            String KEYPASSWORD = "qweasd123";

            KeyStore ks = KeyStore.getInstance( STORETYPE );
            InputStream in = mContext.getResources().openRawResource(R.raw.mykeystore);
            try {
                // Initialize the keystore with the provided trusted certificates
                // Also provide the password of the keystore
                ks.load(in, STOREPASSWORD.toCharArray());
            } finally {
                in.close();
            }
//            File kf = new File( KEYSTORE );
//            ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

//            KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
            KeyManagerFactory kmf = KeyManagerFactory.getInstance( "X509" );
            kmf.init( ks, KEYPASSWORD.toCharArray() );
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
            TrustManagerFactory tmf = TrustManagerFactory.getInstance( "X509" );
            tmf.init( ks );

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance( "TLS" );
            sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
            // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//            try {
//                sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, null, null); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//            } catch (NoSuchAlgorithmException | KeyManagementException e) {
//                e.printStackTrace();
//            }

            WebSocketClient.WebSocketClientFactory factory = new DefaultSSLWebSocketClientFactory(sslContext);
//            WebSocketClient.WebSocketClientFactory factory = new DefaultSSLWebSocketClientFactory(MySSLContext.getSSLContext());

            webSocketClient.setWebSocketFactory(factory);

            webSocketClient.connectBlocking();

        } catch ( Exception ex ) {
            Log.e(TAG, "connection exception: ", ex);
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
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "connection opened");
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
            onMessage("You are disconnected from Chat server " + getURI());
        }

        @Override
        public void onError(Exception ex) {
            Log.e(TAG, "event onError: ", ex);
            onMessage("Exception in Chat server " + getURI());
            onMessage(ex.getMessage());
        }
    };
}
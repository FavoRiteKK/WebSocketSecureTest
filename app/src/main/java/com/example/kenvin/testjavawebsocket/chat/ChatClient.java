package com.example.kenvin.testjavawebsocket.chat;

import android.util.Log;


import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;


public class ChatClient {
    public static final String URL_WEBSOCKET = "wss://192.168.0.106:8887";
//    public static final String URL_WEBSOCKET = "wss://echo.websocket.org";
    public static final String TAG = "IDK-ChatClient";

    private MyWebSocketClient webSocketClient;
    protected TestWebSocketListener socketListener;

	public ChatClient() {
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
//            String STORETYPE = "JKS";
//            String KEYSTORE = "keystore.jks";
//            String STOREPASSWORD = "qweasd123";
//            String KEYPASSWORD = "qweasd123";

//            KeyStore ks = KeyStore.getInstance( STORETYPE );
//            File kf = new File( KEYSTORE );
//            ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

//            KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
//            kmf.init( ks, KEYPASSWORD.toCharArray() );
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
//            tmf.init( ks );

            SSLContext sslContext = null;
//            sslContext = SSLContext.getInstance( "TLS" );
//            sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
            // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
            try {
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }

            WebSocketClient.WebSocketClientFactory factory = new DefaultSSLWebSocketClientFactory(MySSLContext.getSSLContext());

            webSocketClient.setWebSocketFactory(factory);

            webSocketClient.connectBlocking();

        } catch ( Exception ex ) {
            Log.e(TAG, "connection exception: ", ex);
        }
    }

	public void close() {
        Log.d(TAG, "close connection...");
        webSocketClient.close();
    }

    public interface TestWebSocketListener {
        void onMessage(String message);
    }

    class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI, new Draft_17());
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
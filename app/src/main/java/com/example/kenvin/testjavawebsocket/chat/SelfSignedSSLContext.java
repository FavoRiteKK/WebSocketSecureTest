package com.example.kenvin.testjavawebsocket.chat;

import android.content.Context;
import android.util.Log;

import com.example.kenvin.testjavawebsocket.R;

import java.io.InputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author Favo
 *         Created on 07/11/2016.
 */

public class SelfSignedSSLContext {
    private static SSLContext sc;

    private static void initSSLContext(Context context) {
        try {
            // load up the key store
            String STORETYPE = "BKS";
            String KEYSTORE = "mykeystore.bks";
            String STOREPASSWORD = "qweasd123";
            String KEYPASSWORD = "qweasd123";

            KeyStore ks = KeyStore.getInstance( STORETYPE );
            InputStream in = context.getResources().openRawResource(R.raw.mykeystore);
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

            sc = SSLContext.getInstance( "TLS" );
            sc.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );
            // sslContext.init( null, null, null ); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//            try {
//                sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, null, null); // will use java's default key and trust store which is sufficient unless you deal with self-signed certificates
//            } catch (NoSuchAlgorithmException | KeyManagementException e) {
//                e.printStackTrace();
//            }
        } catch (Exception ex) {
            Log.e("IDK-WSS", "Exception:", ex);
        }
    }

    public static SSLContext getSSLContext(Context context) {
        initSSLContext(context);
        return sc;
    }
}

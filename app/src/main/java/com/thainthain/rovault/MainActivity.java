package com.thainthain.rovault;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private KeyStore keyStore;
    private static final String KEY_NAME = "RoVault";
    private Cipher cipher;
    private TextView textView;
    private Socket socket;
    {
        try {
            socket = IO.socket("http://9b0f71f6.ngrok.io");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("aryan","aaaaa");

        socket.connect();
        socket.on("authenticate",onAuth);
        socket.emit("success",true);


    }


    private Emitter.Listener onAuth = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("abc","aaa");
                    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                    textView = findViewById(R.id.errorText);

                    if(!fingerprintManager.isHardwareDetected()){
                        textView.setText("Your device does not support fingerprint Sensor");
                    }
                    else{
                        if(!fingerprintManager.hasEnrolledFingerprints()){
                            textView.setText("Register at least one fingerprint");
                        }
                        else {
                            if(!keyguardManager.isKeyguardSecure()) {
                                textView.setText("Lock screen security not enabled");
                            }
                            else{

                                generateKey();

                                if(cipherInit()) {
                                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                    FingerprintHandler helper = new FingerprintHandler(MainActivity.this);
                                    helper.startAuth(fingerprintManager, cryptoObject, socket);
                                }
                            }
                        }
                    }
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();

    }

    protected void generateKey() {
    try {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
    }
    catch (Exception e) {
        e.printStackTrace();
    }
    KeyGenerator keyGenerator;

    try {
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
    }
    catch (NoSuchAlgorithmException | NoSuchProviderException e) {
        throw new RuntimeException("Failed to get KeyGenerator instance", e);
    }

    try{
       keyStore.load(null);
       keyGenerator.init( new KeyGenParameterSpec.Builder(KEY_NAME,
               KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
               .setUserAuthenticationRequired(true)
               .setEncryptionPaddings(
                       KeyProperties.ENCRYPTION_PADDING_PKCS7
               ).build());
       keyGenerator.generateKey();
    }
    catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
        throw new RuntimeException(e);
    }
}

public boolean cipherInit() {
        try{
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher " + e);
        }

        try{
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        }
        catch (KeyPermanentlyInvalidatedException e) {
            return false;
        }
        catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

}


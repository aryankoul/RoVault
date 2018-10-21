package com.thainthain.rovault;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;
    private Socket socket;

    public FingerprintHandler(Context mcontext) {
        context = mcontext;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject, Socket mSocket) {
        CancellationSignal cancellationSignal = new CancellationSignal();

        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        socket = mSocket;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        socket.emit("success",false);
        socket.connect();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
       // this.update("Fingerprint Authentication help\n" + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Log.i("aryan","ttt");
        socket.emit("success",true);
        socket.connect();
    }

    @Override
    public void onAuthenticationFailed() {
        socket.emit("success",false);
        socket.connect();
    }

}

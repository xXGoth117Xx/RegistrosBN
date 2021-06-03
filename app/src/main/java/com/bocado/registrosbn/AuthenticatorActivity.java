package com.bocado.registrosbn;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.bocado.registrosbn.R;

/**
 * Clase de autentificación del usuario y conexión con los servicios de AWS
 */
public class AuthenticatorActivity extends AppCompatActivity {

    private final String TAG = (AuthenticatorActivity.class.getSimpleName());

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_authenticator );
        Intent i = new Intent(this, AuthenticatorActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        /**
         * Se revisa el estado del usuario y en caso de tener una sesión activa
         * se cierra la sesión de forma automatica
         */
        AWSMobileClient.getInstance().initialize( getApplicationContext(), new Callback<UserStateDetails>() {
            public void onResult(UserStateDetails userStateDetails) {
                //Log.i(TAG, userStateDetails.getUserState().toString() );
                switch ((userStateDetails.getUserState())) {
                    case SIGNED_IN:
                        AWSMobileClient.getInstance().signOut(SignOutOptions.builder().signOutGlobally(true).build(), new Callback<Void>() {
                            @Override
                            public void onResult(final Void result) {
                                Log.d(TAG, "signed-out");
                            }
                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "sign-out error", e);
                            }
                        });
                        AWSMobileClient.getInstance().signOut();
                        showSignIn();
                        break;
                    case SIGNED_OUT:
                        showSignIn();
                        break;
                    default:
                        AWSMobileClient.getInstance().signOut();
                        showSignIn();
                        break;
                }
            }
            public void onError(Exception e) {
                Log.e(TAG, e.toString() );
            }
        } );
    }

    private void showSignIn() {
        try {
            AWSMobileClient.getInstance().showSignIn( this,
                    SignInUIOptions.builder()
                            .nextActivity(MainActivity.class)
                            .logo(R.drawable.logo)
                            .backgroundColor( Color.parseColor("#C1FFEB3B"))
                            .canCancel(true)
                            .build(),
                    new Callback<UserStateDetails>() {
                        public void onResult(UserStateDetails result) {
                            //Log.d(TAG, "onResult: " + result.getUserState());
                        }
                        public void onError(Exception e) {
                            Log.e(TAG, "onError: ", e);
                        }
                    });
        } catch (Exception e) {
            //Log.e(TAG, e.toString());
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (action == KeyEvent.ACTION_DOWN) {
                    System.exit(0);
                    finish();
                    return true;
                }
            default:
                return false;
        }
    }
}

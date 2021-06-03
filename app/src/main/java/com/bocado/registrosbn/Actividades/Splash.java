package com.bocado.registrosbn.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.bocado.registrosbn.AuthenticatorActivity;
import com.bocado.registrosbn.R;

/**
 * Clase de pantalla splash
 */
public class Splash extends AppCompatActivity {

    private final String TAG = (Splash.class.getSimpleName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash );
        /**
         * Pantalla de carga por 1.5 segundos
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent( Splash.this, AuthenticatorActivity.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent );
            }
        }, 1500);
    }
}

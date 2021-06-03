package com.bocado.registrosbn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignOutOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.AnalyticsException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.bocado.registrosbn.Clientes.ClientFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad principal, aqui se crean los fragmentos y se establece la conexión
 * con los servicios de aws para poder realizar las consultas a los registros
 * tambien se programa el boton de cierre total de sesión
 */
/**
 * Actividad principal, aqui se crean los fragmentos y se establece la conexión
 * con los servicios de aws para poder realizar las consultas a los registros
 * tambien se programa el boton de cierre total de sesión
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    String estado= "";
    private AppBarConfiguration mAppBarConfiguration;
    public static PinpointManager pinpointManager;

    protected void onCreate(Bundle savedInstanceState) {
        androidx.fragment.app.FragmentManager fm = getSupportFragmentManager();
        super.onCreate( savedInstanceState );
        FirebaseApp.initializeApp(this);
        setContentView( R.layout.activity_main );
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            //Log.e("Tutorial", "Could not initialize Amplify", e);
        }

        ClientFactory.init(this);
        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        //Log.i("servicio", "onResult en Main: " + userStateDetails.getUserState());
                        estado = userStateDetails.getUserState().toString();
                    }
                    @Override
                    public void onError(Exception e) {
                        //Log.e("servicio", "Initialization error.", e);
                    }
                }
        );

        FloatingActionButton fab = findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CerrarSesion();
                } catch (AnalyticsException e) {
                    e.printStackTrace();
                }
            }
        } );

        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        NavigationView navigationView = findViewById( R.id.nav_view );
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_codigo, R.id.nav_corte)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
        NavigationUI.setupWithNavController( navigationView, navController );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController( this, R.id.nav_host_fragment );
        return NavigationUI.navigateUp( navController, mAppBarConfiguration )
                || super.onSupportNavigateUp();
    }

    public void CerrarSesion () throws AnalyticsException {
        AWSMobileClient.getInstance().signOut(SignOutOptions.builder().signOutGlobally(true).build(), new Callback<Void>() {
            @Override
            public void onResult(final Void result) {
                Log.i("servicio", "signed-out en main: ");
            }

            @Override
            public void onError(Exception e) {
                Log.i("servicio", "Main error");
            }
        });
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

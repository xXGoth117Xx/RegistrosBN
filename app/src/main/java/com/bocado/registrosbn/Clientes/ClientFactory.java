package com.bocado.registrosbn.Clientes;

import android.content.Context;
import android.util.Log;
import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.auth.userpools.CognitoUserPoolsSignInProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.UserStateListener;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.sigv4.BasicCognitoUserPoolsAuthProvider;

/**
 * Esta clase proporciona un cliente AppSync con la cual podemos realizar las
 * consultas de datos
 */
public class ClientFactory {

    private static volatile AWSAppSyncClient client;
    public static synchronized void init(final Context context) {
        /**
         * Se verifica el estado de conexión del usuario
         */
        AWSMobileClient.getInstance().addUserStateListener(new UserStateListener() {
            @Override
            public void onUserStateChanged(UserStateDetails userStateDetails) {
                switch (userStateDetails.getUserState()){
                    case GUEST:
                        //Log.i("userState", "user is in guest mode");
                        break;
                    case SIGNED_OUT:
                        //Log.i("userState", "user is signed out");
                        break;
                    case SIGNED_IN:
                        //Log.i("userState", "user is signed in");
                        break;
                    case SIGNED_OUT_USER_POOLS_TOKENS_INVALID:
                        //Log.i("userState", "need to login again");
                        break;
                    case SIGNED_OUT_FEDERATED_TOKENS_INVALID:
                        //Log.i("userState", "user logged in via federation, but currently needs new tokens");
                        break;
                    default:
                        //Log.e("userState", "unsupported");
                }
            }
        });


        /**
         * Obtenemos los datos de cognito para verificar el control del acceso
         */
        if (client == null) {
            final AWSConfiguration awsConfiguration = new AWSConfiguration(context);

            CognitoUserPoolsSignInProvider cognitoUserPoolsSignInProvider =
                    (CognitoUserPoolsSignInProvider) IdentityManager.getDefaultIdentityManager().getCurrentIdentityProvider();
            BasicCognitoUserPoolsAuthProvider basicCognitoUserPoolsAuthProvider =
                    new BasicCognitoUserPoolsAuthProvider(cognitoUserPoolsSignInProvider.getCognitoUserPool());

            client = AWSAppSyncClient.builder()
                    .context(context)
                    .awsConfiguration(awsConfiguration)
                    .cognitoUserPoolsAuthProvider(basicCognitoUserPoolsAuthProvider)
                    .build();
        }
    }

    public static synchronized AWSAppSyncClient appSyncClient() {
        return client;
    }
}

package com.bocado.registrosbn.ui.codigo;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.amazonaws.amplify.generated.graphql.CreateCodigoMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.bocado.registrosbn.Clientes.ClientFactory;
import com.bocado.registrosbn.Utilidades.Utilidades;
import com.bocado.registrosbn.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.UUID;

import javax.annotation.Nonnull;

import type.CreateCodigoInput;

import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;

/**
 * Fragmeto de menu correspondiente a la solicitud de código de cancelación.
 */

public class CodigoFragment extends Fragment {

    private String randomUUIDString;
    private EditText EdtIdOrden;
    private final Calendar calendar = Calendar.getInstance();
    private final String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format( calendar.getTime() );

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_codigo, container, false);
        EdtIdOrden = root.findViewById(R.id.EdtIdOrden);
        Button btnCodigo = root.findViewById(R.id.btnCodigo);

        btnCodigo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validar()) {
                    VentanaComentario();
                    Toast.makeText(view.getContext(), "Código generado", Toast.LENGTH_SHORT).show();
                    EdtIdOrden.setText( "" );
                }
                else {
                    Toast.makeText( getContext(), "Por favor llene todos los campos", Toast.LENGTH_SHORT ).show();
                }
            }
        } );
        return root;
    }

    /**
     * Función que abre la ventana de comentarios
     */
    private void VentanaComentario() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialog_codigo, null);
        final TextView TxtCodigo = mView.findViewById(R.id.TxtCodigo);
        final Button botoncerrar = mView.findViewById(R.id.BtnCerrar);
        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        UUID uuid = UUID.randomUUID();
        randomUUIDString = uuid.toString();
        Utilidades.CODIGO=randomUUIDString;
        TxtCodigo.setText(randomUUIDString);
        RegistrarCodigo();
        botoncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void RegistrarCodigo () {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        CreateCodigoInput input = CreateCodigoInput.builder()
                .id(randomUUIDString)
                .codigo(Utilidades.CODIGO)
                .idorden(EdtIdOrden.getText().toString())
                .fecha(currentDate)
                .nombre(AWSMobileClient.getInstance().getUsername())
                .build();
        CreateCodigoMutation addCodigoMutation = CreateCodigoMutation.builder()
                .input(input)
                .build();
        ClientFactory.appSyncClient().mutate(addCodigoMutation).enqueue(mutateCallback);
        Utilidades.RETIRO="";
        Utilidades.CAJA="";
        Utilidades.NOMBRE="";
        Utilidades.FECHA="";
        Utilidades.HORA="";
    }

    public boolean validar () {
        boolean retorno = true;
        String idcodigo=EdtIdOrden.getText().toString();
        if (idcodigo.isEmpty()) {
            EdtIdOrden.setError( "Por favor indica ID de orden" );
            retorno=false;
        }
        return retorno;
    }

    private final GraphQLCall.Callback<CreateCodigoMutation.Data> mutateCallback = new GraphQLCall.Callback<CreateCodigoMutation.Data>() {
        public void onResponse(@Nonnull final Response<CreateCodigoMutation.Data> response) {
            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }
        public void onFailure(@Nonnull final ApolloException e) {
            runOnUiThread(new Runnable() {
                public void run() {
                    //Log.e("", "Failed to perform AddCorteMutation", e);
                }
            });
        }
    };
}
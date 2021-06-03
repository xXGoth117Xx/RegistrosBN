package com.bocado.registrosbn.ui.corte;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amazonaws.amplify.generated.graphql.CreateCorteMutation;
import com.amazonaws.amplify.generated.graphql.ListCortesQuery;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.exception.ApolloException;
import com.bocado.registrosbn.Adaptadores.CortesAdapter;
import com.bocado.registrosbn.Clientes.ClientFactory;
import com.bocado.registrosbn.Utilidades.Utilidades;
import com.bocado.registrosbn.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nonnull;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import type.CreateCorteInput;
import com.apollographql.apollo.api.Response;


/**
 * Fragmeto de menu correspondiente al registro de cortes de caja
 */
public class CorteFragment extends Fragment {

    private CortesAdapter mAdapterCorte;
    private ArrayList mCortes;
    private final String TAG = CorteFragment.class.getSimpleName();
    private final Calendar calendar = Calendar.getInstance();
    private final String currentDate2 = DateFormat.getDateInstance(DateFormat.FULL).format( calendar.getTime() );

    private AWSAppSyncClient mAWSAppSyncClient;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_corte, container, false);
        final RecyclerView recycler_corte = root.findViewById(R.id.recycler_corte);
        recycler_corte.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapterCorte = new CortesAdapter(getContext());
        recycler_corte.setAdapter(mAdapterCorte);
        Button BtnRegistrarCorte = root.findViewById(R.id.BtnRegistrarCorte);

        BtnRegistrarCorte.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CorteDialog();
            }
        } );
        return root;
    }

    public void onResume() {
        super.onResume();
        query();
    }

    public void query(){
        ClientFactory.appSyncClient().query(ListCortesQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    private GraphQLCall.Callback<ListCortesQuery.Data> queryCallback = new GraphQLCall.Callback<ListCortesQuery.Data>() {
        public void onResponse(@Nonnull Response<ListCortesQuery.Data> response) {
            assert response.data() != null;
            mCortes = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(response.data().listCortes()).items()));
            //Log.i("Results", Objects.requireNonNull(response.data().listCortes().items()).toString());
            //Log.i(TAG, "Retrieved list items: " + mCortes.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapterCorte.setItems(mCortes);
                    mAdapterCorte.notifyDataSetChanged();
                }
            });
        }
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("ERROR", e.toString());
        }
    };

    /**
     * Cuadro de dialogo para registrar corte
     */
    private void CorteDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.DialogBasicCustomStyle );
        View mView = getLayoutInflater().inflate(R.layout.dialog_corte, null);
        final EditText EdtRetiro = mView.findViewById(R.id.EdtRetiro);
        final EditText EdtCaja = mView.findViewById(R.id.EdtCaja);
        Button BtnCorte = mView.findViewById(R.id.BtnCorte);

        BtnCorte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.RETIRO= (EdtRetiro.getText().toString());
                Utilidades.CAJA= (EdtCaja.getText().toString());
                if (validar()) {
                    Utilidades.NOMBRE= (AWSMobileClient.getInstance().getUsername());
                    Utilidades.FECHA= (currentDate2);
                    RegistrarCorte();
                    Toast.makeText(getContext(), "Corte de caja registrado", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText( getContext(), "Por favor llene todos los campos", Toast.LENGTH_SHORT ).show();
                }
            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    /**
     * Función de registro de corte
     */
    private void RegistrarCorte () {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        CreateCorteInput input = CreateCorteInput.builder()
                .id(randomUUIDString)
                .dinero(Utilidades.RETIRO)
                .registradora(Utilidades.CAJA)
                .nombre(Utilidades.NOMBRE)
                .fecha(currentDate2)
                .build();
        CreateCorteMutation addCorteMutation = CreateCorteMutation.builder()
                .input(input)
                .build();
        ClientFactory.appSyncClient().mutate(addCorteMutation).enqueue(mutateCallback);
        Utilidades.RETIRO="";
        Utilidades.CAJA="";
        Utilidades.NOMBRE="";
        Utilidades.FECHA="";
        Utilidades.HORA="";
    }

    public boolean validar () {
        boolean retorno = true;
        if (Utilidades.RETIRO.isEmpty() || Utilidades.CAJA.isEmpty()) {
            retorno=false;
        }
        return retorno;
    }

    private final GraphQLCall.Callback<CreateCorteMutation.Data> mutateCallback = new GraphQLCall.Callback<CreateCorteMutation.Data>() {
        public void onResponse(@Nonnull final Response<CreateCorteMutation.Data> response) {
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
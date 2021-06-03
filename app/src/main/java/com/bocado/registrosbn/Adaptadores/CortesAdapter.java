package com.bocado.registrosbn.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.amplify.generated.graphql.ListCortesQuery;
import com.bocado.registrosbn.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Adaptador de counicación
 */
public class CortesAdapter extends RecyclerView.Adapter<CortesAdapter.ViewHolder> {

    private ArrayList<ListCortesQuery.Item> mData = new ArrayList<>();
    private final LayoutInflater mInflater;
    public CortesAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate( R.layout.recyclerview_cortes, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ListCortesQuery.Item item = mData.get(position);
        holder.EdtDineroRetirado.setText(item.dinero());
        holder.EdtDineroCaja.setText(item.registradora());
        holder.EdtQuien.setText(item.nombre());
        holder.EdtFecha.setText(item.fecha());;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setItems(ArrayList<ListCortesQuery.Item> items) {
        mData = items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context context;
        TextView EdtDineroRetirado;
        TextView EdtDineroCaja;
        TextView EdtQuien;
        TextView EdtFecha;
        TextView EdtHora;

        ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            EdtDineroRetirado = itemView.findViewById(R.id.EdtDineroRetirado);
            EdtDineroCaja = itemView.findViewById(R.id.EdtDineroCaja);
            EdtQuien = itemView.findViewById(R.id.EdtQuien);
            EdtFecha = itemView.findViewById(R.id.EdtFecha);
            EdtHora = itemView.findViewById(R.id.EdtHora);
        }

    }

}
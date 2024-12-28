package com.example.currencyconverter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.currencyconverter.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.currencyconverter.model.CryptoItem;

import java.util.List;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder> {

    private List<CryptoItem> cryptoList;

    public CryptoAdapter(List<CryptoItem> cryptoList) {
        this.cryptoList = cryptoList;
    }

    @NonNull
    @Override
    public CryptoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crypto_item, parent, false);
        return new CryptoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CryptoViewHolder holder, int position) {
        CryptoItem crypto = cryptoList.get(position);


        holder.cryptoNameSymbol.setText(crypto.getName() + " (" + crypto.getSymbol() + ")");


        holder.cryptoPrice.setText("LKR " + crypto.getPrice());
    }


    @Override
    public int getItemCount() {
        return cryptoList.size();
    }

    public static class CryptoViewHolder extends RecyclerView.ViewHolder {
        TextView cryptoNameSymbol, cryptoPrice;

        public CryptoViewHolder(@NonNull View itemView) {
            super(itemView);
            cryptoNameSymbol = itemView.findViewById(R.id.crypto_name_symbol);
            cryptoPrice = itemView.findViewById(R.id.crypto_price);
        }
    }

}

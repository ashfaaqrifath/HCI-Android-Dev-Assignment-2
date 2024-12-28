package com.example.currencyconverter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.currencyconverter.R;
import com.example.currencyconverter.model.ConversionModel;
import java.util.List;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder> {

    private List<ConversionModel> conversionList;

    public ConversionAdapter(List<ConversionModel> conversionList) {
        this.conversionList = conversionList;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.convert_item, parent, false);
        return new ConversionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        ConversionModel conversion = conversionList.get(position);


        holder.originalAmount.setText(String.format("%s %.2f  -",
                conversion.getOriginalCurrency(),
                conversion.getOriginalAmount()));


        holder.convertedAmount.setText(String.format("%s %.2f",
                conversion.getTargetCurrency(),
                conversion.getConvertedAmount()));
    }





    @Override
    public int getItemCount() {
        return conversionList.size();
    }

    public static class ConversionViewHolder extends RecyclerView.ViewHolder {
        TextView originalAmount;
        TextView convertedAmount;

        public ConversionViewHolder(@NonNull View itemView) {
            super(itemView);
            originalAmount = itemView.findViewById(R.id.original_amount);
            convertedAmount = itemView.findViewById(R.id.converted_amount);
        }
    }
}

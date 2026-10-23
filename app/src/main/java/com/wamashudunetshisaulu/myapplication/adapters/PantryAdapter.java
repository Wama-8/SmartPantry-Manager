package com.wamashudunetshisaulu.myapplication.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.utils.DateUtils;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Shows pantry items in a RecyclerView; tap a row to edit, tap Delete to remove. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private int warnDays = 3;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<PantryItem> newItems, int warningDays) {
        items.clear();
        items.addAll(newItems);
        warnDays = warningDays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        final PantryItem item = items.get(position);
        h.name.setText(item.getName());
        h.amount.setText(QuantityFormat.amount(item.getQuantity(), item.getUnit()));

        // Expiry line: coloured when expired or inside the warning window from Settings.
        h.expiry.setTextColor(h.defaultExpiryColors);
        Integer days = DateUtils.daysUntil(item.getExpiryDate(), new Date());
        if (item.getExpiryDate() == null || days == null) {
            h.expiry.setText("No expiry date");
        } else if (days < 0) {
            h.expiry.setText("Expired " + item.getExpiryDate());
            h.expiry.setTextColor(Color.parseColor("#B3261E"));
        } else if (days <= warnDays) {
            h.expiry.setText("Expires soon: " + item.getExpiryDate()
                    + (days == 0 ? " (today)" : " (" + days + (days == 1 ? " day)" : " days)")));
            h.expiry.setTextColor(Color.parseColor("#B26A00"));
        } else {
            h.expiry.setText("Expires " + item.getExpiryDate());
        }

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView name, amount, expiry, delete;
        final ColorStateList defaultExpiryColors;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvItemName);
            amount = itemView.findViewById(R.id.tvItemAmount);
            expiry = itemView.findViewById(R.id.tvItemExpiry);
            delete = itemView.findViewById(R.id.btnItemDelete);
            defaultExpiryColors = expiry.getTextColors();
        }
    }
}

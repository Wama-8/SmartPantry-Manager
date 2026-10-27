package com.wamashudunetshisaulu.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wamashudunetshisaulu.myapplication.R;
import com.wamashudunetshisaulu.myapplication.models.PantryItem;
import com.wamashudunetshisaulu.myapplication.utils.DateUtils;
import com.wamashudunetshisaulu.myapplication.utils.IngredientCatalog;
import com.wamashudunetshisaulu.myapplication.utils.QuantityFormat;
import com.wamashudunetshisaulu.myapplication.utils.TextFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Shows pantry items as cards; tap a card to edit, tap the bin (or swipe) to remove. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private IngredientCatalog catalog;
    private int warnDays = 3;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<PantryItem> newItems, int warningDays, IngredientCatalog recipeCatalog) {
        items.clear();
        items.addAll(newItems);
        warnDays = warningDays;
        catalog = recipeCatalog;
        notifyDataSetChanged();
    }

    /** Returns the item at a position, or null when the position is invalid. */
    public PantryItem getItem(int position) {
        return position >= 0 && position < items.size() ? items.get(position) : null;
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
        String shownName = TextFormat.capitalize(item.getName());
        h.initial.setText(TextFormat.initial(item.getName()));
        h.name.setText(shownName);
        h.amount.setText(QuantityFormat.amount(item.getQuantity(), item.getUnit()));
        h.delete.setContentDescription("Delete " + shownName);

        // "Custom" = an ingredient that no recipe in the database uses.
        boolean custom = catalog != null && !catalog.isKnown(item.getName());
        h.custom.setVisibility(custom ? View.VISIBLE : View.GONE);

        Integer days = DateUtils.daysUntil(item.getExpiryDate(), new Date());
        int bg, fg;
        String text;
        if (item.getExpiryDate() == null || days == null) {
            text = "No expiry date";
            bg = R.drawable.bg_pill_neutral;
            fg = R.color.pantry_muted;
        } else if (days < 0) {
            text = "Expired " + item.getExpiryDate();
            bg = R.drawable.bg_pill_expired;
            fg = R.color.pantry_danger_dark;
        } else if (days <= warnDays) {
            text = days == 0 ? "Expires today" : "Expires in " + TextFormat.count(days, "day", "days");
            bg = R.drawable.bg_pill_soon;
            fg = R.color.pantry_honey_dark;
        } else {
            text = "Expires " + item.getExpiryDate();
            bg = R.drawable.bg_pill_ok;
            fg = R.color.pantry_green_dark;
        }
        h.expiry.setText(text);
        h.expiry.setBackgroundResource(bg);
        h.expiry.setTextColor(ContextCompat.getColor(h.itemView.getContext(), fg));

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView initial, name, custom, amount, expiry;
        final ImageButton delete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            initial = itemView.findViewById(R.id.tvItemInitial);
            name = itemView.findViewById(R.id.tvItemName);
            custom = itemView.findViewById(R.id.tvItemCustom);
            amount = itemView.findViewById(R.id.tvItemAmount);
            expiry = itemView.findViewById(R.id.tvItemExpiry);
            delete = itemView.findViewById(R.id.btnItemDelete);
        }
    }
}

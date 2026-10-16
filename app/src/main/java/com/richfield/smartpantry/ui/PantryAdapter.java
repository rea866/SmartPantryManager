package com.richfield.smartpantry.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.logic.ExpiryUtils;
import com.richfield.smartpantry.logic.NameUtils;
import com.richfield.smartpantry.logic.UnitConverter;
import com.richfield.smartpantry.model.Ingredient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter that shows the pantry items (one card per ingredient).
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Callbacks so the Activity (not the adapter) decides what edit/delete actually do. */
    public interface Listener {
        void onEdit(Ingredient item);
        void onDelete(Ingredient item);
    }

    private final List<Ingredient> items = new ArrayList<>();
    private final Listener listener;
    private boolean alertsEnabled = true;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<Ingredient> newItems, boolean alertsEnabled) {
        items.clear();
        items.addAll(newItems);
        this.alertsEnabled = alertsEnabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ingredient item = items.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvName.setText(NameUtils.title(item.getName()));
        holder.tvQuantity.setText(UnitConverter.formatQuantity(item.getQuantity()) + " " + item.getUnit());
        bindExpiry(holder.tvExpiry, ctx, item.getExpiryDate());

        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    /** Shows the expiry line, coloured red when expired and orange when expiring soon. */
    private void bindExpiry(TextView tv, Context ctx, String expiry) {
        if (expiry == null || expiry.isEmpty()) {
            tv.setVisibility(View.GONE);
            return;
        }
        tv.setVisibility(View.VISIBLE);
        Long days = ExpiryUtils.daysUntil(expiry, LocalDate.now());

        if (days != null && days < 0) {
            tv.setText(ctx.getString(R.string.expired_on, expiry));
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.expired_red));
        } else if (alertsEnabled && days != null && days <= ExpiryUtils.SOON_DAYS) {
            tv.setText(days == 0
                    ? ctx.getString(R.string.expires_today, expiry)
                    : ctx.getString(R.string.expires_soon, days.intValue(), expiry));
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.warn_orange));
        } else {
            tv.setText(ctx.getString(R.string.expires_on, expiry));
            tv.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvQuantity;
        final TextView tvExpiry;
        final ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

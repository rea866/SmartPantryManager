package com.richfield.smartpantry.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.logic.NameUtils;
import com.richfield.smartpantry.logic.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter for the Suggested Recipes screen.
 * The same adapter serves both lists; "showMissing" switches the sub-line to
 * "Missing: ..." for the Almost There list.
 */
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface Listener {
        void onRecipeClicked(RecipeMatcher.Result result);
    }

    private final List<RecipeMatcher.Result> results = new ArrayList<>();
    private final Listener listener;
    private boolean showMissing = false;

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<RecipeMatcher.Result> newResults, boolean showMissing) {
        results.clear();
        results.addAll(newResults);
        this.showMissing = showMissing;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RecipeMatcher.Result result = results.get(position);
        holder.tvName.setText(result.getRecipe().getName());

        if (showMissing) {
            StringBuilder names = new StringBuilder();
            for (String m : result.getMissing()) {
                if (names.length() > 0) names.append(", ");
                names.append(NameUtils.title(m));
            }
            holder.tvInfo.setText(holder.itemView.getContext().getString(R.string.info_missing, names));
        } else {
            holder.tvInfo.setText(holder.itemView.getContext()
                    .getString(R.string.info_ingredient_count, result.getRecipe().getIngredients().size()));
        }
        holder.itemView.setOnClickListener(v -> listener.onRecipeClicked(result));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvInfo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRecipeName);
            tvInfo = itemView.findViewById(R.id.tvRecipeInfo);
        }
    }
}

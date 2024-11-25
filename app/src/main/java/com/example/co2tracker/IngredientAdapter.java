package com.example.co2tracker;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private Context context;
    private List<IngredientItem> ingredients;
    private List<Food> foods;

    public IngredientAdapter(Context context, List<IngredientItem> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
        this.foods = FoodDatabase.getFoods(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IngredientItem item = ingredients.get(position);

        // Setup food spinner with custom layout
        ArrayAdapter<Food> foodAdapter = new ArrayAdapter<>(
                context,
                R.layout.item_dropdown_menu,
                foods
        );

        holder.foodSpinner.setAdapter(foodAdapter);

        // If food was previously selected, show it
        if (item.getSelectedFood() != null) {
            holder.foodSpinner.setText(item.getSelectedFood().toString(), false);
            holder.servingInfo.setText(context.getString(R.string.typical_serving,
                    item.getSelectedFood().getServingDescription(),
                    item.getSelectedFood().getServingGrams()));
            holder.servingInfo.setVisibility(View.VISIBLE);
        } else {
            holder.foodSpinner.setText("", false);
            holder.servingInfo.setVisibility(View.GONE);
        }

        // Setup food selection
        holder.foodSpinner.setOnItemClickListener((parent, view, pos, id) -> {
            Food selectedFood = (Food) parent.getItemAtPosition(pos);
            item.setSelectedFood(selectedFood);
            holder.foodSpinner.setText(selectedFood.toString(), false);
            holder.servingInfo.setText(context.getString(R.string.typical_serving,
                    selectedFood.getServingDescription(),
                    selectedFood.getServingGrams()));
            holder.servingInfo.setVisibility(View.VISIBLE);
        });

        // Set input filters for servings
        holder.servingsInput.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(5),
                (source, start, end, dest, dstart, dend) -> {
                    try {
                        String input = dest.toString() + source.toString();
                        if (input.length() > 0) {
                            int value = Integer.parseInt(input);
                            if (value < 1) return "1";
                            if (value > 99999) return "99999";
                        }
                    } catch (NumberFormatException e) {
                        return "";
                    }
                    return null;
                }
        });

        // Setup servings input
        holder.servingsInput.setText(String.valueOf(item.getServings()));
        holder.servingsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    String input = s.toString();
                    if (input.isEmpty()) {
                        item.setServings(1);
                        holder.servingsInput.setText("1");
                    } else {
                        int value = Integer.parseInt(input);
                        if (value < 1) {
                            item.setServings(1);
                            holder.servingsInput.setText("1");
                        } else {
                            item.setServings(Math.min(value, 99999));
                        }
                    }
                } catch (NumberFormatException e) {
                    item.setServings(1);
                    holder.servingsInput.setText("1");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup remove button visibility
        holder.removeButton.setVisibility(ingredients.size() > 1 ? View.VISIBLE : View.GONE);

        // Setup remove button click
        holder.removeButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                ingredients.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, ingredients.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialAutoCompleteTextView foodSpinner;
        TextInputLayout foodInputLayout;
        TextInputEditText servingsInput;
        ImageButton removeButton;
        TextView servingInfo;

        ViewHolder(View itemView) {
            super(itemView);
            foodSpinner = itemView.findViewById(R.id.foodSpinner);
            foodInputLayout = itemView.findViewById(R.id.foodInputLayout);
            servingsInput = itemView.findViewById(R.id.servingsInput);
            removeButton = itemView.findViewById(R.id.removeButton);
            servingInfo = itemView.findViewById(R.id.servingInfo);

            // Prevent keyboard from showing up
            foodSpinner.setInputType(0);
        }
    }
}
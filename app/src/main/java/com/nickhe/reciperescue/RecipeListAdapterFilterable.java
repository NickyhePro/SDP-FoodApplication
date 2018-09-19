
package com.nickhe.reciperescue;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecipeListAdapterFilterable extends RecyclerView.Adapter implements Filterable {

    private List<Recipe> originalRecipes;
    private List<Recipe> filteredRecipes;
    private RecipeNameFilter recipeNameFilter;

    public RecipeListAdapterFilterable(Activity context, List<Recipe> recipes) {
        originalRecipes = recipes;
        filteredRecipes = recipes;
        recipeNameFilter = new RecipeNameFilter();

    }

    public Filter getNameFilter() {
        return recipeNameFilter;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Recipe recipe = filteredRecipes.get(position);
        viewHolder.textView.setText(recipe.getRecipeTitle());
        viewHolder.imageView.setImageBitmap(recipe.getRecipeImage());
    }

    @Override
    public int getItemCount() {
        return filteredRecipes.size();
    }

    @Override
    public Filter getFilter() {
        return getNameFilter();
    }

    private class RecipeNameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String nameFilter = constraint.toString().toLowerCase();

            FilterResults recipeResults = new FilterResults();
            List<Recipe> recipes = originalRecipes;
            ArrayList<Recipe> recipeList = new ArrayList<>();

            Recipe recipe;

            for (int i = 0; i < recipes.size(); ++i) {
                recipe = recipes.get(i);
                if (recipe.getRecipeTitle().toLowerCase().contains(nameFilter)) {
                    recipeList.add(recipe);
                }
            }

            recipeResults.values = recipeList;
            recipeResults.count = recipeList.size();

            return recipeResults;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredRecipes = (ArrayList<Recipe>) results.values;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.recipeTextView);
            imageView = view.findViewById(R.id.recipeImageView);
        }
    }

}


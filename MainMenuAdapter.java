package com.example.reginalin.food_truck_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Austin Luong on 2/18/2018.
 * Adapter to display main menu list items. Each item includes the FoodTruck's name, cuisine type,
 * price point, location, rating and whether or not it is open.
 */

public class MainMenuAdapter extends BaseAdapter implements Filterable {
    private Context mainMenuContext; // Context of MainMenuListing Activity
    private LayoutInflater mainMenuInflater;
    private List<FoodTruck> allFoodTrucks; // Original list of all food trucks
    private List<FoodTruck> filteredFoodTrucks = new ArrayList<>(); // List of all food trucks after filtering
    private User current = MainActivity.currentOnline; // Current online user

    /**
     * Constructor for MainMenuAdapter
     * @param context Context of MainMenuListing Activity
     * @param foodTrucks List of all food trucks
     */
    MainMenuAdapter(Context context, List<FoodTruck> foodTrucks) {
        // Instantiate field variables
        mainMenuContext = context;
        allFoodTrucks = foodTrucks;
        filteredFoodTrucks = foodTrucks;
        mainMenuInflater = (LayoutInflater) mainMenuContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return filteredFoodTrucks.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for one row item of the main menu
        final View mainMenuRowItem = mainMenuInflater.inflate(R.layout.main_menu_list_item, parent, false);

        // Get all of the views in the row item
        final int truckPosition = position;
        final TextView nameTextView = mainMenuRowItem.findViewById(R.id.foodTruckName);
        TextView cuisineTextView = mainMenuRowItem.findViewById(R.id.cuisineText);
        TextView pricePointTextView = mainMenuRowItem.findViewById(R.id.pricePointText);
        TextView statusView = mainMenuRowItem.findViewById(R.id.statusText);
        RatingBar rating = mainMenuRowItem.findViewById(R.id.ratingBar);

        // Set values for all views in the row item
        nameTextView.setText(filteredFoodTrucks.get(position).getName());
        cuisineTextView.setText(filteredFoodTrucks.get(position).getCuisine());
        Double pricePoint = filteredFoodTrucks.get(position).getPricePoint();

        // Display price rating based on price point
        if (pricePoint <= 2.0) {
            pricePointTextView.setText("$");
        } else if (pricePoint <= 4.0) {
            pricePointTextView.setText("$$");
        } else if (pricePoint <= 5.0) {
            pricePointTextView.setText("$$$");
        } else if (pricePoint <= 6.0) {
            pricePointTextView.setText("$$$$");
        } else {
            pricePointTextView.setText("$$$$$");
        }

        // Display Status of food truck (open or closed)
        if (filteredFoodTrucks.get(position).isOpen()) {
            statusView.setText("OPEN");
            statusView.setTextColor(Color.GREEN);
        } else {
            statusView.setText("CLOSED");
            statusView.setTextColor(Color.RED);
        }

        // Display rating as stars
        rating.setRating((float) filteredFoodTrucks.get(position).getRating());

        // if the user is an owner, then the favorites button goes away
        final Button favoritesButton = mainMenuRowItem.findViewById(R.id.favorite);
        if (MainActivity.currentOnline.getIsOwner()) {
            favoritesButton.setVisibility(View.GONE);
        }

        // Set up favorites button for each row item, turning button on or off depending on user's
        // list of favorite food trucks
        if (current.containFavorite(filteredFoodTrucks.get(truckPosition).getName())) {
            // If food truck is in user's list of favorite food trucks, turn star on
            favoritesButton.setBackgroundDrawable(ContextCompat.getDrawable(mainMenuContext,
                    android.R.drawable.star_big_on));
            favoritesButton.setActivated(true);
        } else {
            // If food truck is not in user's list of favorite food trucks, keep star off
            favoritesButton.setBackgroundDrawable(ContextCompat.getDrawable(mainMenuContext,
                    android.R.drawable.star_big_off));
            favoritesButton.setActivated(false);
        }

        // Define onClick action for favorites button
        favoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favoritesButton.isActivated()) {
                    // If user clicks on star that is on, turn star off and remove food truck from
                    // user's list of favorites
                    favoritesButton.setBackgroundDrawable(ContextCompat.getDrawable(mainMenuContext,
                            android.R.drawable.star_big_off));
                    MainActivity.currentOnline.removeFavorite(filteredFoodTrucks.get(truckPosition));
                    System.out.println("~~~~~~~~~~~~~~~~~~~ favorites length " + filteredFoodTrucks.get(truckPosition).getName() + " "
                            + MainActivity.currentOnline.getFavorites().size());
                    Toast.makeText(mainMenuContext,
                            "Removed From Your Favorites!",
                            Toast.LENGTH_SHORT).show();
                    favoritesButton.setActivated(false);}
                else {
                    // If user clicks on star that is off, turn star on and add food truck to user's
                    // list of favorites
                    favoritesButton.setBackgroundDrawable(ContextCompat.getDrawable(mainMenuContext,
                            android.R.drawable.star_big_on));
                    MainActivity.currentOnline.addFavorite(filteredFoodTrucks.get(truckPosition));
                    Toast.makeText(mainMenuContext,
                            "Added To Your Favorites!",
                            Toast.LENGTH_SHORT).show();
                    favoritesButton.setActivated(true);}
            }
        });

        // Go to FoodTruckListing Activity class when you click on a selected food truck
        mainMenuRowItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mainMenuContext, FoodTruckListing.class);
                String foodTruckName = nameTextView.getText().toString();
                Bundle extras = new Bundle();
                extras.putString("FOODTRUCK_NAME",foodTruckName);
                intent.putExtras(extras); // Pass selected food truck's name to FoodTruckListing Activity
                mainMenuContext.startActivity(intent);
            }
        });

        return mainMenuRowItem;
    }

    // Implementing Filterable interface to filter main menu when searching
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint.length() == 0) {
                    // displays all food trucks if there is no search input
                    results.values = allFoodTrucks;
                    results.count = allFoodTrucks.size();
                } else {
                    // if there is a search input, iterate through all food trucks and add matches
                    // to filtered food truck list
                    ArrayList<FoodTruck> tempFilteredList = new ArrayList<>();
                    for (FoodTruck curr : allFoodTrucks) {
                        if (curr.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            tempFilteredList.add(curr);
                        }
                        if (curr.getCuisine().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            if (!(tempFilteredList.contains(curr))) {
                                tempFilteredList.add(curr);
                            }
                        }
                    }
                    results.values = tempFilteredList;
                    results.count = tempFilteredList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                // Notify adapter that filtered fodo truck list has updated
                filteredFoodTrucks = (ArrayList<FoodTruck>) filterResults.values;
                MainMenuAdapter.this.notifyDataSetChanged();
            }
        };
    }

    /**
     * Sorts food truck list using a desired comparator
     * @param c Comparator that specifies sorting behavior
     */
    void update(Comparator<FoodTruck> c) {
        if (c == null) {
            Collections.sort(filteredFoodTrucks);
        } else {
            Collections.sort(filteredFoodTrucks, c);
        }
        MainMenuAdapter.this.notifyDataSetChanged();
    }
}
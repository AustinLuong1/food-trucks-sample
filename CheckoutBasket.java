package com.example.reginalin.food_truck_app;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Austin Luong on 3/18/2018.
 */

class CheckoutBasket {
    private static CheckoutBasket instance; // static instance of Checkout Basket used throughout single session in app
    private LinkedHashMap<MenuItem, Integer> selectedItems; // maps <MenuItem, Quantity>
    private double totalCost; // total cost of all items in the Checkout Basket

    private CheckoutBasket() {
        // creates instance of Checkout Basket
        instance = this;
        selectedItems = new LinkedHashMap<>();
    }

    // retrieves instance of Checkout Basket or creates a new one if not yet created
    static CheckoutBasket getInstance() {
        if (instance == null) {
            instance = new CheckoutBasket();
        }
        return instance;
    }

    // retrieves the selected items in the Checkout Basket
    LinkedHashMap<MenuItem, Integer> getSelectedItems() {
        return selectedItems;
    }

    // adds an item to the Checkout Basket, increasing the quantity by 1
    void addItem(MenuItem item) {
        if (!(selectedItems.containsKey(item))) {
            selectedItems.put(item, 1);
        } else {
            selectedItems.put(item, (selectedItems.get(item) + 1));
        }

        // adds price of item to total cost in basket
        totalCost = totalCost + item.getPrice();
    }

    // removes a single item from the Checkout Basket, decreasing the quantity by 1
    void subtractItem(MenuItem item) {
        if (selectedItems.containsKey(item)) {

            // only removes item from basket if there is at least one of the item in the basket
            if (selectedItems.get(item) - 1 >= 1) {
                selectedItems.put(item, (selectedItems.get(item) - 1));

                // subtracts price of item from total cost in basket
                totalCost = totalCost - item.getPrice();
            }
        }
    }

    // removes item completely from the Checkout Basket, reducing quantity to 0
    void removeItems(MenuItem item) {
        if(selectedItems.containsKey(item)) {

            // subtracts cost of item * quantity of item from total cost
            totalCost = totalCost - (item.getPrice() * selectedItems.get(item));
            selectedItems.remove(item);
        }
    }

    // removes all items from the Checkout Basket, reducing total cost to 0
    void clearBasket() {
        selectedItems.clear();
        totalCost = 0.0;
    }

    // returns the total cost of all items in the Checkout Basket
    double getTotalCost() {
        return totalCost;
    }

    // retrieve menu item by index in linkedHashMap
    MenuItem getItemByIndex(int index) {
        Object[] items = selectedItems.keySet().toArray();
        return (MenuItem) items[index];
    }

    // retrieve quantity of menu item by index in linkedHashMap
    int getQuantityByIndex(int index) {
        ArrayList<Integer> quantities = new ArrayList<>(selectedItems.values());
        return quantities.get(index);
    }
}
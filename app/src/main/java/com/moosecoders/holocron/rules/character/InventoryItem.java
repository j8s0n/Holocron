package com.moosecoders.holocron.rules.character;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InventoryItem {
  private static final String LOG_TAG = InventoryItem.class.getSimpleName();

  private static final String NAME_KEY = "name";
  private static final String ENCUMBRANCE_KEY = "encumbrance";
  private static final String LOCATION_KEY = "location";
  private static final String QUANTITY_KEY = "quantity";
  private static final String COUNT_ENCUMBRANCE_KEY = "count_encumbrance";
  private static final String DESCRIPTION_KEY = "description";

  private String name;
  private int quantity;
  private String location;
  private int encumbrance;
  private String description;
  private boolean countEncumbrance;

  private InventoryItem(@NotNull String name, int quantity, @NotNull String location, int encumbrance,
                        @NotNull String description, boolean countEncumbrance) {
    this.name = name;
    this.quantity = quantity;
    this.location = location;
    this.encumbrance = encumbrance;
    this.description = description;
    this.countEncumbrance = countEncumbrance;
  }

  @NotNull
  public static InventoryItem of(@NotNull String name, int quantity, @NotNull String location, int encumbrance,
                                 @NotNull String description, boolean countEncumbrance) {
    return new InventoryItem(name, quantity, location, encumbrance, description, countEncumbrance);
  }

  @NotNull
  static List<InventoryItem> parseInventory(@NotNull JSONArray jsonArray) {
    List<InventoryItem> inventory = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      InventoryItem item;
      try {
        item = InventoryItem.of(jsonArray.getJSONObject(i));
      }
      catch (JSONException e) {
        Log.e(LOG_TAG, String.format("Error reading inventory item at index %d.", i), e);
        continue;
      }
      inventory.add(item);
    }

    return inventory;
  }

  @NotNull
  private JSONObject toJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put(NAME_KEY, name);
    o.put(QUANTITY_KEY, quantity);
    o.put(LOCATION_KEY, location);
    o.put(ENCUMBRANCE_KEY, encumbrance);
    o.put(DESCRIPTION_KEY, description);
    o.put(COUNT_ENCUMBRANCE_KEY, countEncumbrance);

    return o;
  }

  @NotNull
  public static InventoryItem of(@NotNull JSONObject o) throws JSONException {
    String name = o.getString(NAME_KEY);
    int quantity = o.getInt(QUANTITY_KEY);
    String location = o.getString(LOCATION_KEY);
    int encumbrance = o.getInt(ENCUMBRANCE_KEY);
    String description = o.getString(DESCRIPTION_KEY);
    boolean countEncumbrance = o.getBoolean(COUNT_ENCUMBRANCE_KEY);

    return InventoryItem.of(name, quantity, location, encumbrance, description, countEncumbrance);
  }

  @NotNull
  static JSONArray toJsonArray(@NotNull List<InventoryItem> inventory) throws JSONException {
    JSONArray a = new JSONArray();
    for (InventoryItem item : inventory) {
      a.put(item.toJson());
    }

    return a;
  }
}

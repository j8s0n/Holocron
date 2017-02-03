package org.raincitygamers.holocron.rules.character;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class InventoryItem {
  private static final String NAME_KEY = "name";
  private static final String ENCUMBRANCE_KEY = "encumbrance";
  private static final String LOCATION_KEY = "location";
  private static final String QUANTITY_KEY = "quantity";
  private static final String COUNT_ENCUMBRANCE_KEY = "count_encumbrance";
  private static final String DESCRIPTION_KEY = "description";

  @Getter private final String name;
  @Getter private final int quantity;
  @Getter private final String location;
  @Getter private final int encumbrance;
  @Getter private final String description;
  @Getter private final boolean countEncumbrance;

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
  public static List<InventoryItem> parseInventory(JSONArray jsonArray) throws JSONException {
    List<InventoryItem> inventory = new ArrayList<>();

    for (int i = 0; i < jsonArray.length(); i++) {
      InventoryItem item = InventoryItem.of(jsonArray.getJSONObject(i));
      inventory.add(item);
    }

    return inventory;
  }

  @NotNull
  private static InventoryItem of(@NotNull String name, int quantity, @NotNull String location, int encumbrance,
                                 @NotNull String description, boolean countEncumbrance) {
    return new InventoryItem(name, quantity, location, encumbrance, description, countEncumbrance);
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
      public static InventoryItem of (@NotNull JSONObject o) throws JSONException {
    String name = o.getString(NAME_KEY);
    int quantity = o.getInt(QUANTITY_KEY);
    String location = o.getString(LOCATION_KEY);
    int encumbrance = o.getInt(ENCUMBRANCE_KEY);
    String description = o.getString(DESCRIPTION_KEY);
    boolean countEncumbrance = o.getBoolean(COUNT_ENCUMBRANCE_KEY);

    return new InventoryItem(name, quantity, location, encumbrance, description, countEncumbrance);
  }

  public static JSONArray toJson(List<InventoryItem> inventory) throws JSONException {
    JSONArray a = new JSONArray();
    for (InventoryItem item : inventory) {
      a.put(item.toJson());
    }

    return a;
  }
}

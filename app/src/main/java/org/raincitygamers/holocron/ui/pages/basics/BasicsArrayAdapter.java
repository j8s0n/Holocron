package org.raincitygamers.holocron.ui.pages.basics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.raincitygamers.holocron.R;
import org.raincitygamers.holocron.ui.pages.rowdata.KeyValueRowData;
import org.raincitygamers.holocron.ui.pages.rowdata.KeyValueRowData.KvPair;
import org.raincitygamers.holocron.ui.pages.rowdata.RowData;
import org.raincitygamers.holocron.ui.pages.rowdata.SectionRowData;

import java.util.List;

public class BasicsArrayAdapter extends ArrayAdapter<RowData> {
  public BasicsArrayAdapter(Context context, List<RowData> objects) {
    super(context, -1, objects);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    RowData rowData = getItem(position);

    switch (rowData.getType()) {
    case SECTION_ID:
      return displaySection(convertView, parent, ((SectionRowData)rowData).getSectionId());
    case KEY_VALUE:
      return displayKeyValuePair(convertView, parent, ((KeyValueRowData)rowData).getPair());
    default:
      // TODO
      return null;
    }
  }

  @NotNull
  private View displaySection(View convertView, ViewGroup parent, String sectionLabel) {
    ViewHolder viewHolder;
    if (convertView == null) {
      // TODO:
      // When convertView isn't null, but is switching row data types, need to treat it as null and recreate it.
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.section_list_item, parent, false);
      viewHolder.sectionLabel = (TextView) convertView.findViewById(R.id.section_label);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.sectionLabel.setText(sectionLabel);
    return convertView;
  }

  @NotNull
  private View displayKeyValuePair(View convertView, ViewGroup parent, KvPair pair) {
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      convertView = inflater.inflate(R.layout.key_value_list_item, parent, false);
      viewHolder.key = (TextView) convertView.findViewById(R.id.key);
      viewHolder.value = (TextView) convertView.findViewById(R.id.value);
      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder) convertView.getTag();
    }

    viewHolder.key.setText(pair.getKey());
    viewHolder.value.setText(pair.getValue());
    return convertView;
  }

  private static class ViewHolder {
    TextView sectionLabel;

    TextView key;
    TextView value;
  }
}

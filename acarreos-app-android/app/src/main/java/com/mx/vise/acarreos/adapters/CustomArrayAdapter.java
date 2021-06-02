package com.mx.vise.acarreos.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mx.vise.acarreos.R;

import java.util.ArrayList;
import java.util.List;

public class CustomArrayAdapter<T extends GenericAdapter> extends ArrayAdapter<T> {

    private static final String TAG = "VISE";
    private Context mContext;
    private List<T> list = new ArrayList<>();

    public CustomArrayAdapter(@NonNull Context context, List<T> list) {
        super(context, R.layout.spinner_custom_item, list);
        mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_custom_item, parent, false);

        T currentItem = list.get(position);

        TextView name = listItem.findViewById(R.id.spinnerItemTextView);
        name.setText(currentItem.getText());


        return listItem;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.spinner_custom_dropdown_item, parent, false);

        T currentSyndicate = list.get(position);
        TextView name = listItem.findViewById(R.id.spinnerItemTextView);
        name.setText(currentSyndicate.getText());


        return listItem;
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}

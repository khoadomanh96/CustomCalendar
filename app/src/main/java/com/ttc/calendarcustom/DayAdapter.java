package com.ttc.calendarcustom;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by khoado on 05,June,2020
 */

public class DayAdapter extends BaseAdapter {
    private String[] titleDays;
    private final LayoutInflater inflater;

    public DayAdapter(Context context,String[] titleDays) {
        this.titleDays = titleDays;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titleDays.length;
    }

    @Override
    public Object getItem(int position) {
        return titleDays[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.day_item, null);
        }
        final String currentItem = titleDays[position];
        final TextView textView = view.findViewById(R.id.title_day);
        textView.setText(currentItem);
        if (position == titleDays.length-1) {
            textView.setTextColor(Color.parseColor("#B7A0A0"));
        }
        return view;
    }
}

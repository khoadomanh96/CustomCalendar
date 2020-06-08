package com.ttc.calendarcustom;

/**
 * Created by khoado on 05,June,2020
 */

import java.util.Calendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
    private static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY;
    private final Calendar calendar;
    private final CalendarItem today;
    private final CalendarItem selected;
    private final LayoutInflater inflater;
    private CalendarItem[] days;
    private boolean expand = false;

    public CalendarAdapter(Context context, Calendar monthCalendar) {
        calendar = monthCalendar;
        today = new CalendarItem(monthCalendar);
        selected = new CalendarItem(monthCalendar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int position) {
        return days[position];
    }

    @Override
    public long getItemId(int position) {
        final CalendarItem item = days[position];
        if (item != null) {
            return days[position].id;
        }
        return -1;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.calendar_item, null);
        }
        final TextView dayView = (TextView)view.findViewById(R.id.date);
        final TextView detail = view.findViewById(R.id.tv_detail);
        final CalendarItem currentItem = days[position];

        if (currentItem == null) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
            view.setBackgroundDrawable(null);
            dayView.setBackgroundResource(R.drawable.normal_background);
            dayView.setText(null);
            toggle(false,view);
        } else {
            if (currentItem.equals(today)) {
                dayView.setBackgroundResource(R.drawable.today_background);
            } else if (currentItem.equals(selected)) {
                dayView.setBackgroundResource(R.drawable.selected_background);
            } else {
                dayView.setBackgroundResource(R.drawable.normal_background);
            }
            dayView.setText(currentItem.text);
            if ((position + 1) % 7 == 0 || position + 1 == 13 || position + 1 == 27) {
                dayView.setTextColor(Color.parseColor("#B7A0A0"));
            } else {
                dayView.setTextColor(Color.parseColor("#000000"));
            }
            if (currentItem.expend) {
//                detail.setVisibility(View.VISIBLE);
                toggle(true,view);
                view.setBackgroundResource(R.drawable.background_expand);
            } else {
//                detail.setVisibility(View.GONE);
                toggle(false,view);
                view.setBackgroundResource(R.drawable.normal_background);
            }
        }

        return view;
    }
    private void toggle(boolean show,View view) {
        View redLayout = view.findViewById(R.id.tv_detail);
        ViewGroup parent = view.findViewById(R.id.parent);

        Transition transition = new Fade();
        transition.setDuration(300);
        transition.addTarget(R.id.tv_detail);

        TransitionManager.beginDelayedTransition(parent, transition);
        redLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    public void setExpend() {
        expand = true;
        for (CalendarItem item : days) {
            if (item!=null) {
                item.expend = true;
            }
        }
        notifyDataSetChanged();
    }
    public void setCollapse() {
        expand = false;
        for (CalendarItem item : days) {
            if (item!=null) {
                item.expend = false;
            }
        }
        notifyDataSetChanged();
    }

    public final void setSelected(int year, int month, int day) {
        selected.year = year;
        selected.month = month;
        selected.day = day;
        notifyDataSetChanged();
    }

    public final void refreshDays() {
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
        final int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        final int blankies;
        final CalendarItem[] days;

        if (firstDayOfMonth == FIRST_DAY_OF_WEEK) {
            blankies = 0;
        } else if (firstDayOfMonth < FIRST_DAY_OF_WEEK) {
            blankies = Calendar.SATURDAY - (FIRST_DAY_OF_WEEK - 1);
        } else {
            blankies = firstDayOfMonth - FIRST_DAY_OF_WEEK;
        }
        days = new CalendarItem[lastDayOfMonth + blankies];

        for (int day = 1, position = blankies; position < days.length; position++) {
            days[position] = new CalendarItem(year, month, day++);
        }

        this.days = days;
        if (expand) {
            for (CalendarItem item : days) {
                if (item!=null) {
                    item.expend = true;
                }
            }
        } else {
            for (CalendarItem item : days) {
                if (item!=null) {
                    item.expend = false;
                }
            }
        }
        notifyDataSetChanged();
    }

    private static class CalendarItem {
        public int year;
        public int month;
        public int day;
        public String text;
        public long id;
        public boolean expend = false;

        public CalendarItem(Calendar calendar) {
            this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }

        public CalendarItem(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
            if (day < 10) {
                this.text = "0" + String.valueOf(day);
            } else {
                this.text = String.valueOf(day);
            }
            this.id = Long.valueOf(year + "" + month + "" + day);
        }

        @Override
        public boolean equals(Object o) {
            if (o != null && o instanceof CalendarItem) {
                final CalendarItem item = (CalendarItem)o;
                return item.year == year && item.month == month && item.day == day;
            }
            return false;
        }
    }
}
package com.ttc.calendarcustom;

/**
 * Created by khoado on 05,June,2020
 */

import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.fragment.app.Fragment;

public class CalendarView extends Fragment {
    private static final String TAG = "CalendarView";
    protected final Calendar calendar;
    private final Locale locale;
    private ViewSwitcher calendarSwitcher;
    private TextView currentMonth;
    private CalendarAdapter calendarAdapter;
    private GridView calendarGrid;
    private GridView calendarDayGrid;
    private LinearLayout view1;

    public CalendarView() {
        calendar = Calendar.getInstance();
        locale = Locale.getDefault();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RelativeLayout calendarLayout = (RelativeLayout)inflater.inflate(R.layout.calendar, container,false);
        calendarDayGrid = (GridView)calendarLayout.findViewById(R.id.calendar_days_grid);
        final GestureDetector swipeDetector = new GestureDetector(getActivity(), new SwipeGesture(getActivity()));
        calendarGrid = (GridView)calendarLayout.findViewById(R.id.calendar_grid);
        calendarSwitcher = (ViewSwitcher)calendarLayout.findViewById(R.id.calendar_switcher);
        currentMonth = (TextView)calendarLayout.findViewById(R.id.current_month);
        calendarAdapter = new CalendarAdapter(getActivity(), calendar);
        view1 = calendarLayout.findViewById(R.id.view_1);
        updateCurrentMonth();

        final ImageView nextMonth =  calendarLayout.findViewById(R.id.next_month);
        nextMonth.setOnClickListener(new NextMonthClickListener());
        final ImageView prevMonth =  calendarLayout.findViewById(R.id.previous_month);
        prevMonth.setOnClickListener(new PreviousMonthClickListener());
        calendarGrid.setOnItemClickListener(new DayItemClickListener());

        calendarGrid.setAdapter(calendarAdapter);
        calendarGrid.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return swipeDetector.onTouchEvent(event);
            }
        });
        DayAdapter dayAdapter = new DayAdapter(getActivity(),getResources().getStringArray(R.array.days_array));
        calendarDayGrid.setAdapter(dayAdapter);
        return calendarLayout;
    }

    protected void updateCurrentMonth() {
        calendarAdapter.refreshDays();
        String monthname=(String) DateFormat.format("MM", calendar);
        currentMonth.setText("Chấm công "+ monthname + "/" + calendar.get(Calendar.YEAR));
    }

    private final class DayItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final TextView dayView = (TextView)view.findViewById(R.id.date);
            final CharSequence text = dayView.getText();
            if (text != null && !"".equals(text)) {
                calendarAdapter.setSelected(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), Integer.valueOf(String.valueOf(text)));
            }
        }
    }

    protected final void onNextMonth() {
        calendarSwitcher.setInAnimation(getActivity(), R.anim.in_from_right);
        calendarSwitcher.setOutAnimation(getActivity(), R.anim.out_to_left);
        calendarSwitcher.showNext();
        if (calendar.get(Calendar.MONTH) == Calendar.DECEMBER) {
            calendar.set((calendar.get(Calendar.YEAR) + 1), Calendar.JANUARY, 1);
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        }
        updateCurrentMonth();
    }

    protected final void onPreviousMonth() {
        calendarSwitcher.setInAnimation(getActivity(), R.anim.in_from_left);
        calendarSwitcher.setOutAnimation(getActivity(), R.anim.out_to_right);
        calendarSwitcher.showPrevious();
        if (calendar.get(Calendar.MONTH) == Calendar.JANUARY) {
            calendar.set((calendar.get(Calendar.YEAR) - 1), Calendar.DECEMBER, 1);
        } else {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
        }
        updateCurrentMonth();
    }

    private final class NextMonthClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            onNextMonth();
        }
    }

    private final class PreviousMonthClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            onPreviousMonth();
        }
    }

    private final class SwipeGesture extends SimpleOnGestureListener {
        private final int swipeMinDistance;
        private final int swipeThresholdVelocity;

        public SwipeGesture(Context context) {
            final ViewConfiguration viewConfig = ViewConfiguration.get(context);
            swipeMinDistance = viewConfig.getScaledTouchSlop();
            swipeThresholdVelocity = viewConfig.getScaledMinimumFlingVelocity();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
//                collapse();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onNextMonth();
                    }
                },100);
            }  else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
//                collapse();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onPreviousMonth();
                    }
                },100);
            }  else if (e1.getY() - e2.getY() > swipeMinDistance && Math.abs(velocityY) > swipeThresholdVelocity) {
                Log.e(TAG, "onFling: collapse");
                collapse();
            }  else if (e2.getY() - e1.getY() > swipeMinDistance && Math.abs(velocityY) > swipeThresholdVelocity) {
                Log.e(TAG, "onFling: expand");
                expend();
            }
            return false;
        }
    }


    private void expend() {
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) view1.getLayoutParams();
        params1.removeRule(RelativeLayout.BELOW);
        params1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        view1.setLayoutParams(params1);
        view1.requestLayout();
        calendarAdapter.setExpend();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, calendarDayGrid.getId());
        params.addRule(RelativeLayout.ABOVE,view1.getId());
        calendarSwitcher.setLayoutParams(params);
        calendarGrid.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        calendarSwitcher.requestLayout();

    }

    private void collapse() {
        calendarAdapter.setCollapse();
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) view1.getLayoutParams();
        params1.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params1.addRule(RelativeLayout.BELOW,calendarSwitcher.getId());
        view1.setLayoutParams(params1);
        view1.requestLayout();
        calendarGrid.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        calendarGrid.requestLayout();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, calendarDayGrid.getId());
        calendarSwitcher.setLayoutParams(params);
        calendarSwitcher.requestLayout();

    }

}
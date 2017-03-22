package ru.binaryblitz.sportup.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;
import ru.binaryblitz.sportup.R;
import ru.binaryblitz.sportup.base.BaseActivity;

public class SportEventsActivity extends BaseActivity {

    private void initCalendar() {
        final HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .datesNumberOnScreen(7)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .showDayName(true)
                .showMonthName(false)
                .textColor(Color.WHITE, ContextCompat.getColor(this, R.color.colorPrimary))
                .selectedDateBackground(Color.WHITE)
                .selectorColor(Color.TRANSPARENT)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_feed);

        initCalendar();
    }
}

package com.tubb.calendarselector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.tubb.calendarselector.library.SingleMonthSelector;
import com.tubb.calendarselector.library.SCDateUtils;
import com.tubb.calendarselector.library.FullDay;
import com.tubb.calendarselector.library.IntervalSelectListener;
import com.tubb.calendarselector.library.SCMonth;
import com.tubb.calendarselector.library.CalendarSelector;
import com.tubb.calendarselector.library.MonthView;
import com.tubb.calendarselector.library.SegmentSelectListener;

import java.util.List;

public class SingleMonthSelectorActivity extends AppCompatActivity {

    private static final String TAG = "mv";
    SCMonth SCMonth;
    private SingleMonthSelector processor;
    private MonthView monthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_month_selector);
        monthView = (MonthView) findViewById(R.id.ssMv);
        TextView tvMonthTitle = (TextView) findViewById(R.id.tvMonthTitle);
        if(savedInstanceState != null){
            SCMonth = savedInstanceState.getParcelable("month");
        }
        if(SCMonth == null)
            SCMonth = new SCMonth(2016, 1);
        tvMonthTitle.setText(SCMonth.toString());
        segmentMode();
    }

    private void segmentMode(){
        SCMonth.getSelectedDays().clear();
        monthView.setSCMonth(SCMonth);
        processor = new SingleMonthSelector(CalendarSelector.Mode.SEGMENT);
        processor.setSegmentSelectListener(new SegmentSelectListener() {
            @Override
            public void onSegmentSelect(FullDay startDay, FullDay endDay) {
                Log.d(TAG, "segment select " + startDay.toString() + " : " + endDay.toString());
            }

            @Override
            public boolean onInterceptSelect(FullDay selectingDay) {
                if(SCDateUtils.isToday(selectingDay.getYear(), selectingDay.getMonth(), selectingDay.getDay())){
                    Toast.makeText(SingleMonthSelectorActivity.this, "Today can't be selected", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return super.onInterceptSelect(selectingDay);
            }

            @Override
            public boolean onInterceptSelect(FullDay startDay, FullDay endDay) {
                int differDays = SCDateUtils.countDays(startDay.getYear(), startDay.getMonth(), startDay.getDay(),
                        endDay.getYear(), endDay.getMonth(), endDay.getDay());
                Log.d(TAG, "differDays " + differDays);
                if(differDays > 5) {
                    Toast.makeText(SingleMonthSelectorActivity.this, "Selected days can't more than 5", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return super.onInterceptSelect(startDay, endDay);
            }

        });
        processor.bind(monthView);
    }

    private void intervalMode(){
        SCMonth.getSelectedDays().clear();
        monthView.setSCMonth(SCMonth);
        processor = new SingleMonthSelector(CalendarSelector.Mode.INTERVAL);
        processor.setIntervalSelectListener(new IntervalSelectListener() {
            @Override
            public void onIntervalSelect(List<FullDay> selectedDays) {
                Log.d(TAG, "interval selected days " + selectedDays.toString());
            }

            @Override
            public boolean onInterceptSelect(List<FullDay> selectedDays, FullDay selectingDay) {
                if(selectedDays.size() >= 5) {
                    Toast.makeText(SingleMonthSelectorActivity.this, "Selected days can't more than 5", Toast.LENGTH_LONG).show();
                    return true;
                }
                return super.onInterceptSelect(selectedDays, selectingDay);
            }
        });
        processor.bind(monthView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mode, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_segment) {
            segmentMode();
            return true;
        }
        else if (id == R.id.action_interval) {
            intervalMode();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

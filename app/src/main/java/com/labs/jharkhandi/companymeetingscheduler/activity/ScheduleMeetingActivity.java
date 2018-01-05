package com.labs.jharkhandi.companymeetingscheduler.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.labs.jharkhandi.companymeetingscheduler.R;
import com.labs.jharkhandi.companymeetingscheduler.Util.Util;
import com.labs.jharkhandi.companymeetingscheduler.api.ApiClient;
import com.labs.jharkhandi.companymeetingscheduler.model.Meeting;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.labs.jharkhandi.companymeetingscheduler.activity.MainActivity.DAY;
import static com.labs.jharkhandi.companymeetingscheduler.activity.MainActivity.MONTH;
import static com.labs.jharkhandi.companymeetingscheduler.activity.MainActivity.YEAR;

public class ScheduleMeetingActivity extends AppCompatActivity{

    private static final String TAG = ScheduleMeetingActivity.class.getSimpleName();

    private static final String START_HOUR = "start_hour";
    private static final String START_MINUTE = "start_minute";
    private static final String END_HOUR = "end_hour";
    private static final String END_MINUTE = "end_minute";
    private static final String IS_START_TIME_PICKER = "is_start_time_picker";

    private static final int DEFAULT_START_HOUR = 9;
    private static final int DEFAULT_END_HOUR = 10;

    private static final int DEFAULT_START_MINUTE = 0;
    private static final int DEFAULT_END_MINUTE = 0;

    private TextInputEditText mMeetingDate;

    private TextInputLayout mStartTimeLayout;
    private TextInputEditText mStartTime;

    private TextInputLayout mEndTimeLayout;
    private TextInputEditText mEndTime;

    private TextInputLayout mDescriptionLayout;
    private TextInputEditText mDescription;

    private Button mSubmitButton;

    private Button mActionLeftNavButton;

    private Button mActionRightNavButton;

    private TextView mActionHeading;

    private Calendar mDate;

    private int mStartHour, mStartMinute, mEndHour, mEndMinute;

    private boolean isStartTimePicker;

    private List<Meeting> mMeetingList;

    private ProgressDialog mProgressDialog;

    @Override
    @SuppressLint("DefaultLocale")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting);

        /* setup the date */
        mDate = Calendar.getInstance();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mDate.set(bundle.getInt(YEAR, mDate.get(Calendar.YEAR)),
                    bundle.getInt(MONTH, mDate.get(Calendar.MONTH)),
                    bundle.getInt(DAY, mDate.get(Calendar.DAY_OF_MONTH)));
        }

        restore(savedInstanceState);

        bindViews();

        setupToolBar();

        setListeners();

        /* update date field */
        String dateString = String.format("%02d-%02d-%d", mDate.get(Calendar.DAY_OF_MONTH),
                mDate.get(Calendar.MONTH) + 1, mDate.get(Calendar.YEAR));
        mMeetingDate.setText(dateString);
    }

    private void restore(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            mStartHour = savedInstanceState.getInt(START_HOUR, DEFAULT_START_HOUR);
            mStartMinute = savedInstanceState.getInt(START_HOUR, DEFAULT_START_MINUTE);

            mEndHour = savedInstanceState.getInt(START_HOUR, DEFAULT_END_HOUR);
            mEndMinute = savedInstanceState.getInt(START_HOUR, DEFAULT_END_MINUTE);

            mDate.set(savedInstanceState.getInt(YEAR, mDate.get(Calendar.YEAR)),
                    savedInstanceState.getInt(MONTH, mDate.get(Calendar.MONTH)),
                    savedInstanceState.getInt(DAY, mDate.get(Calendar.DAY_OF_MONTH)));

            isStartTimePicker = savedInstanceState.getBoolean(IS_START_TIME_PICKER, false);
        }else{
            /* initialize times to default value */
            mStartHour = DEFAULT_START_HOUR;
            mStartMinute = DEFAULT_START_MINUTE;
            mEndHour = DEFAULT_END_HOUR;
            mEndMinute = DEFAULT_END_MINUTE;
        }
    }

    private void setupToolBar() {
        /* make right nav button gone */
        mActionRightNavButton.setVisibility(View.GONE);

        /* Set button text */
        mActionLeftNavButton.setText(getString(R.string.schedule_meeting_activity_left_nav));

        /* Set listeners */
        mActionLeftNavButton.setOnClickListener(mOnClickListener);

        /* Set Heading Text */
        mActionHeading.setText(getString(R.string.schedule_meeting_activity_heading));
    }

    private void bindViews() {
        mMeetingDate = findViewById(R.id.meeting_date);
        mStartTimeLayout = findViewById(R.id.start_time_layout);
        mStartTime = findViewById(R.id.start_time);
        mEndTimeLayout = findViewById(R.id.end_time_layout);
        mEndTime = findViewById(R.id.end_time);
        mDescriptionLayout = findViewById(R.id.description_layout);
        mDescription = findViewById(R.id.description);
        mSubmitButton = findViewById(R.id.submit_button);
        mActionLeftNavButton = findViewById(R.id.action_left_nav);
        mActionRightNavButton = findViewById(R.id.action_right_nav);
        mActionHeading = findViewById(R.id.action_heading);
    }

    private void setListeners() {
        mActionLeftNavButton.setOnClickListener(mOnClickListener);
        mMeetingDate.setOnClickListener(mOnClickListener);
        mStartTime.setOnClickListener(mOnClickListener);
        mEndTime.setOnClickListener(mOnClickListener);
        mSubmitButton.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TimePickerDialog timePickerDialog;
            switch (view.getId()){
                case R.id.action_left_nav:
                    finish();
                    break;
                case R.id.meeting_date:
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            mOnDateSetListener, mDate.get(Calendar.YEAR),
                            mDate.get(Calendar.MONTH), mDate.get(Calendar.DAY_OF_MONTH));
                    dpd.setMinDate(Calendar.getInstance());
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                    break;
                case R.id.start_time:
                    isStartTimePicker = true;
                    timePickerDialog =
                            new TimePickerDialog(ScheduleMeetingActivity.this, mOnTimeSetListener,
                                    mStartHour, mStartMinute, false);
                    timePickerDialog.show();
                    break;
                case R.id.end_time:
                    timePickerDialog =
                            new TimePickerDialog(ScheduleMeetingActivity.this, mOnTimeSetListener,
                                    mEndHour, mEndMinute, false);
                    timePickerDialog.show();
                    break;
                case R.id.submit_button:
                    if(validateStartTime() & validateEndTime()){
                        mProgressDialog = ProgressDialog.show(ScheduleMeetingActivity.this, "",
                                "Checking slot availability...", true);
                        updateMeetingListNCheckAvailability();
                        if(mProgressDialog != null)
                            mProgressDialog.dismiss();
                    }else{
                        Toast.makeText(ScheduleMeetingActivity.this, "Please correct the marked fields",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    @SuppressLint("DefaultLocale")
    private DatePickerDialog.OnDateSetListener  mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            mDate.set(year, monthOfYear, dayOfMonth);
            String dateString = String.format("%02d-%02d-%d",dayOfMonth,monthOfYear+1,year);
            mMeetingDate.setText(dateString);
        }
    };

    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            if(isStartTimePicker){
                isStartTimePicker = false;
                mStartHour = hourOfDay;
                mStartMinute = minute;
                String text = getAMPMTime(hourOfDay, minute);
                mStartTime.setText(text);
                validateStartTime();
            }else{
                mEndHour = hourOfDay;
                mEndMinute = minute;
                String text = getAMPMTime(hourOfDay, minute);
                mEndTime.setText(text);
                validateEndTime();
            }
        }
    };

    @SuppressLint("DefaultLocale")
    private String getAMPMTime(int hourOfDay, int minute){
        return String.format("%02d:%02d%s",
                hourOfDay == 0 ? 12:(hourOfDay <= 12 ? hourOfDay: hourOfDay - 12),
                minute,
                hourOfDay < 12 ? "AM":"PM");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        /* Save date */
        outState.putInt(YEAR, mDate.get(Calendar.YEAR));
        outState.putInt(MONTH, mDate.get(Calendar.MONTH));
        outState.putInt(DAY, mDate.get(Calendar.DAY_OF_MONTH));

        /* Save start time */
        outState.putInt(START_HOUR, mStartHour);
        outState.putInt(START_MINUTE, mStartMinute);

        /* Save end time */
        outState.putInt(END_HOUR, mEndHour);
        outState.putInt(END_MINUTE, mEndMinute);

        /* time dialog */
        outState.putBoolean(IS_START_TIME_PICKER, isStartTimePicker);

        super.onSaveInstanceState(outState);
    }

    @SuppressLint("DefaultLocale")
    private void updateMeetingListNCheckAvailability(){
        ApiClient.CompanyMeetingApiInterface service = ApiClient.getCompanyMeetingApiInterface();
        Call<List<Meeting>> call = service.getMeetingListByDay(String.format("%d/%d/%d",
                mDate.get(Calendar.DAY_OF_MONTH),
                mDate.get(Calendar.MONTH) + 1, mDate.get(Calendar.YEAR)));

        call.enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                if(mProgressDialog != null) mProgressDialog.dismiss();
                if(response.body() != null){
                    mMeetingList = response.body();
                    /* call the callback method to process user request */
                    slotCheckCallBack();
                }else{
                    Log.e(TAG, "some error in receiving the data");
                    Toast.makeText(ScheduleMeetingActivity.this, "Server Error: Try after some time",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                if(mProgressDialog != null) mProgressDialog.dismiss();
                Log.e(TAG, "error in fetching meeting. Error: " + t.getMessage());
                Toast.makeText(ScheduleMeetingActivity.this, "Server Error: Try after some time",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Here I have assumed that only meeting can run at a time.
     */
    private void slotCheckCallBack(){
        boolean isSlotAvailable = true;
        if(mMeetingList != null){
            Collections.sort(mMeetingList);
            for (Meeting meeting: mMeetingList) {

                if(compareTime(meeting.getStartTime(), mEndHour, mEndMinute) >= 0) break;

                if(compareTime(meeting.getEndTime(), mStartHour, mStartMinute) > 0){
                    isSlotAvailable = false;
                    break;
                }
            }
            Toast.makeText(ScheduleMeetingActivity.this,
                    isSlotAvailable?"Slot available":"Slot not available",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(ScheduleMeetingActivity.this, "Server Error: Try after some time",
                    Toast.LENGTH_LONG).show();
        }
    }


    private int compareTime(String time24HrFormat, int hour, int minute){
        int hour1 = Util.getHourOfDay(time24HrFormat);
        int minute1 = Util.getMinute(time24HrFormat);

        if(hour1 - hour != 0){
            return hour1-hour;
        }else{
            return minute1-minute;
        }
    }

    private int compareTime(int hour1, int minute1, int hour, int minute){
        if(hour1 - hour != 0){
            return hour1-hour;
        }else{
            return minute1-minute;
        }
    }

    private boolean validateStartTime(){
        CharSequence text = mStartTime.getText();
        if(text == null || text.toString().equalsIgnoreCase("")){ /* empty field */
            mStartTimeLayout.setError(getString(R.string.err_empty));
            return false;
        }else if(mEndTime.getText() != null && !mEndTime.getText().toString().equalsIgnoreCase("") &&
                compareTime(mEndHour, mEndMinute, mStartHour, mStartMinute) <= 0){
            /* if end time is equal to or before start time */
            mStartTimeLayout.setError(getString(R.string.err_start_after_end));
            return false;
        }
        mStartTimeLayout.setError(null);
        if(mEndTime.getText() != null && !mEndTime.getText().toString().equalsIgnoreCase("")){
            mEndTimeLayout.setError(null);
        }
        return true;
    }

    private boolean validateEndTime(){
        CharSequence text = mEndTime.getText();
        if(text == null || text.toString().equalsIgnoreCase("")){ /* empty field */
            mEndTimeLayout.setError(getString(R.string.err_empty));
            return false;
        }else if(mStartTime.getText() != null && !mStartTime.getText().toString().equalsIgnoreCase("") &&
                compareTime(mEndHour, mEndMinute, mStartHour, mStartMinute) <= 0){
            /* if end time is equal to or before start time */
            mEndTimeLayout.setError(getString(R.string.err_end_before_start));
            return false;
        }
        mEndTimeLayout.setError(null);
        if(mStartTime.getText() != null && !mStartTime.getText().toString().equalsIgnoreCase("")){
            mStartTime.setError(null);
        }
        return true;
    }

    /*private boolean validateDescription(){
        CharSequence text = mDescription.getText();
        if(text == null || text.toString().equalsIgnoreCase("")){
            mDescriptionLayout.setError(getString(R.string.err_empty));
            return false;
        }
        return true;
    }*/
}

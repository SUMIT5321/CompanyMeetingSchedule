package com.labs.jharkhandi.companymeetingscheduler.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.labs.jharkhandi.companymeetingscheduler.R;
import com.labs.jharkhandi.companymeetingscheduler.Util.Util;
import com.labs.jharkhandi.companymeetingscheduler.adapter.MeetingAdapter;
import com.labs.jharkhandi.companymeetingscheduler.api.ApiClient;
import com.labs.jharkhandi.companymeetingscheduler.model.Meeting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String YEAR = "year";
    public static final String MONTH = "month";
    public static final String DAY = "day";

    private RecyclerView mMeetingRecycler;

    private Button mScheduleMeetingButton;

    private Button mActionLeftNavButton;

    private Button mActionRightNavButton;

    private TextView mActionHeading;

    private FrameLayout mProgressWrapper;

    private MeetingAdapter mMeetingAdapter;

    private List<Meeting> mMeetingList;

    private Calendar mDate;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDate = Calendar.getInstance();

        /* restore instance state */
        if(savedInstanceState != null){
            mDate.set(savedInstanceState.getInt(YEAR, mDate.get(Calendar.YEAR)),
                    savedInstanceState.getInt(MONTH, mDate.get(Calendar.MONTH)),
                    savedInstanceState.getInt(DAY, mDate.get(Calendar.DAY_OF_MONTH)));
        }

        bindViews();

        setupToolBar();

        setupMeetingRecycler();

        /* set listener to schedule meeting button */
        mScheduleMeetingButton.setOnClickListener(mOnClickListener);

        /* set progress visibility VISIBLE */
        mProgressWrapper.setVisibility(View.VISIBLE);
        mMeetingRecycler.setVisibility(View.GONE);

        updateScheduleMeetingButtonState();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onResume() {
        super.onResume();
        refreshMeetingList();
    }

    private void bindViews() {
        mMeetingRecycler = findViewById(R.id.meeting_recycler);
        mScheduleMeetingButton = findViewById(R.id.schedule_meeting_button);
        mActionLeftNavButton = findViewById(R.id.action_left_nav);
        mActionRightNavButton = findViewById(R.id.action_right_nav);
        mActionHeading = findViewById(R.id.action_heading);
        mProgressWrapper = findViewById(R.id.progress_wrapper);
    }

    private void setupToolBar() {
        /* Set button text */
        mActionLeftNavButton.setText(getString(R.string.main_activity_left_nav));
        mActionRightNavButton.setText(getString(R.string.main_activity_right_nav));

        /* Set listeners */
        mActionLeftNavButton.setOnClickListener(mOnClickListener);
        mActionRightNavButton.setOnClickListener(mOnClickListener);

        /* Set Heading Text */
        setHeading();
    }

    @SuppressLint("DefaultLocale")
    private void setHeading() {
        String dateString;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            dateString = String.format("%02d-%02d-%d", mDate.get(Calendar.DAY_OF_MONTH),
                    mDate.get(Calendar.MONTH) + 1, mDate.get(Calendar.YEAR));
        } else {
            dateString = String.format("%s, %02d-%02d-%d",
                    Util.DAY_OF_WEEK[mDate.get(Calendar.DAY_OF_WEEK) - 1],
                    mDate.get(Calendar.DAY_OF_MONTH),
                    mDate.get(Calendar.MONTH) + 1, mDate.get(Calendar.YEAR));
        }
        mActionHeading.setText(dateString);
    }

    private void setupMeetingRecycler() {
        mMeetingRecycler.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        if(mMeetingList == null) mMeetingList = new ArrayList<Meeting>();

        mMeetingAdapter = new MeetingAdapter(this, mMeetingList);

        mMeetingRecycler.setAdapter(mMeetingAdapter);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.action_left_nav:
                    mDate.add(Calendar.DAY_OF_MONTH, -1);
                    refreshMeetingList();
                    setHeading();
                    updateScheduleMeetingButtonState();
                    break;
                case R.id.action_right_nav:
                    mDate.add(Calendar.DAY_OF_MONTH, 1);
                    refreshMeetingList();
                    setHeading();
                    updateScheduleMeetingButtonState();
                    break;
                case R.id.schedule_meeting_button:
                    Intent intent = new Intent(MainActivity.this, ScheduleMeetingActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putInt(YEAR, mDate.get(Calendar.YEAR));
                    bundle.putInt(MONTH, mDate.get(Calendar.MONTH));
                    bundle.putInt(DAY, mDate.get(Calendar.DAY_OF_MONTH));

                    intent.putExtras(bundle);

                    startActivity(intent);
                    break;
            }
        }
    };

    @SuppressLint("DefaultLocale")
    private void refreshMeetingList(){
        mProgressWrapper.setVisibility(View.VISIBLE);
        ApiClient.CompanyMeetingApiInterface service = ApiClient.getCompanyMeetingApiInterface();
        Call<List<Meeting>> call = service.getMeetingListByDay(String.format("%d/%d/%d",
                mDate.get(Calendar.DAY_OF_MONTH),
                mDate.get(Calendar.MONTH) + 1, mDate.get(Calendar.YEAR)));

        call.enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                if(response.body() != null){
                    mProgressWrapper.setVisibility(View.GONE);
                    mMeetingRecycler.setVisibility(View.VISIBLE);
                    mMeetingList = response.body();
                    mMeetingAdapter.setMeetingList(mMeetingList);
                    mMeetingAdapter.notifyDataSetChanged();
                }else{
                    Log.e(TAG, "some error in receiving the data");
                    Toast.makeText(MainActivity.this, "Error in fetching data",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                Log.e(TAG, "error in fetching meeting. Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error in fetching data",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateScheduleMeetingButtonState() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        if(today.after(mDate)){
            mScheduleMeetingButton.setEnabled(false);
        }else{
            mScheduleMeetingButton.setEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(YEAR, mDate.get(Calendar.YEAR));
        outState.putInt(MONTH, mDate.get(Calendar.MONTH));
        outState.putInt(DAY, mDate.get(Calendar.DAY_OF_MONTH));
        super.onSaveInstanceState(outState);
    }
}

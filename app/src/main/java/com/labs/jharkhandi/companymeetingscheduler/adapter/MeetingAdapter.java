package com.labs.jharkhandi.companymeetingscheduler.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.labs.jharkhandi.companymeetingscheduler.R;

import com.labs.jharkhandi.companymeetingscheduler.Util.Util;
import com.labs.jharkhandi.companymeetingscheduler.model.Meeting;

import java.util.Collections;
import java.util.List;

/**
 * Created by sumit on 4/1/18.
 */

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingVH> {

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_NORMAL_VIEW = 1;

    Context context;

    List<Meeting> meetingList;

    LayoutInflater layoutInflater;

    public MeetingAdapter(Context context, List<Meeting> meetingList) {
        this.context = context;
        this.meetingList = meetingList;
        Collections.sort(meetingList);
        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setMeetingList(List<Meeting> meetingList) {
        this.meetingList = meetingList;
        Collections.sort(meetingList);
    }

    @Override
    public int getItemViewType(int position) {
        if(meetingList.isEmpty()){
            return VIEW_TYPE_EMPTY_LIST_PLACEHOLDER;
        }else{
            return VIEW_TYPE_NORMAL_VIEW;
        }
    }

    @Override
    public MeetingVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case VIEW_TYPE_EMPTY_LIST_PLACEHOLDER:
                Log.e("Adapter", "Creating placeholder view");
                v = layoutInflater.inflate(R.layout.no_meetings_view, parent, false);
                break;
            default:
                v = layoutInflater.inflate(R.layout.meeting_row, parent, false);
                break;
        }
        return new MeetingVH(v);
    }

    @Override
    public void onBindViewHolder(MeetingVH holder, int position) {

        /* Don't do any thing if this is an empty list case */
        int viewType = getItemViewType(position);
        if(VIEW_TYPE_EMPTY_LIST_PLACEHOLDER == viewType){
            return;
        }

        try{
            String startTimeStr = Util.getAMPMTime(meetingList.get(position).getStartTime());
            String endTimeStr = Util.getAMPMTime(meetingList.get(position).getEndTime());

            holder.timeStart.setText(startTimeStr);
            holder.timeEnd.setText(endTimeStr);
            holder.shortDesc.setText(meetingList.get(position).getDescription());

            if(holder.attendeeList != null){
                String participants = meetingList.get(position).getParticipants().toString();
                holder.attendeeList.setText(participants.substring(1, participants.length()-1));
            }
        }catch(Exception e){
            Log.e("MeetingAdapter", e.getMessage());
            Toast.makeText(context, "Error. Please try again", Toast.LENGTH_LONG)
            .show();
        }
    }

    @Override
    public int getItemCount() {
        Log.e("Adapter", "meeting list size: " + meetingList.size());
        /* to show empty state view */
        if(meetingList.size() == 0) return 1;
        return meetingList.size();
    }

    public static class MeetingVH extends RecyclerView.ViewHolder{

        TextView timeStart;
        TextView timeEnd;
        TextView shortDesc;
        TextView attendeeList;

        public MeetingVH(View itemView) {
            super(itemView);
            timeStart = itemView.findViewById(R.id.time_start);
            timeEnd = itemView.findViewById(R.id.time_end);
            shortDesc = itemView.findViewById(R.id.short_desc);
            attendeeList = itemView.findViewById(R.id.attendee_list);
        }
    }
}

package com.labs.jharkhandi.companymeetingscheduler.model;

import android.support.annotation.NonNull;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.labs.jharkhandi.companymeetingscheduler.Util.Util;

public class Meeting implements Comparable<Meeting>{

    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("participants")
    @Expose
    private List<String> participants = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Meeting() {
    }

    /**
     *
     * @param startTime
     * @param description
     * @param endTime
     * @param participants
     */
    public Meeting(String startTime, String endTime, String description, List<String> participants) {
        super();
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.participants = participants;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    @Override
    public int compareTo(@NonNull Meeting meeting) {
        int hourDiff = Util.getHourOfDay(this.getStartTime()) - Util.getHourOfDay(meeting.getStartTime());
        if(hourDiff != 0)
            return hourDiff;
        else
            return Util.getMinute(this.getStartTime()) - Util.getMinute(meeting.getStartTime());
    }
}
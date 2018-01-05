package com.labs.jharkhandi.companymeetingscheduler.api;

import com.labs.jharkhandi.companymeetingscheduler.model.Meeting;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sumit on 4/1/18.
 */

public class ApiClient {
    public static String BASE_URL = "http://fathomless-shelf-5846.herokuapp.com/api/";

    private static CompanyMeetingApiInterface companyMeetingApiInterface;

    public static CompanyMeetingApiInterface getCompanyMeetingApiInterface(){
        if(companyMeetingApiInterface == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            companyMeetingApiInterface = retrofit.create(CompanyMeetingApiInterface.class);
        }
        return companyMeetingApiInterface;
    }

    public interface CompanyMeetingApiInterface {
        @GET("schedule")
        Call<List<Meeting>> getMeetingListByDay(@Query("date") String date);
    }
}

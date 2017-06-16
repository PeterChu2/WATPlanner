package com.example.peterchu.watplanner.Networking;

import com.example.peterchu.watplanner.Models.Course.CourseDetailsResponse;
import com.example.peterchu.watplanner.Models.Course.CourseResponse;
import com.example.peterchu.watplanner.Models.Schedule.CourseScheduleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Timothy Tong on 6/5/17.
 */

public interface ApiInterface {
    @GET("terms/{termId}/courses.json")
    Call<CourseResponse> getCourses(@Path("termId") String termId, @Query("key") String apiKey);

    @GET("terms/{termId}/{subject}/{catalogNumber}/schedule.json")
    Call<CourseScheduleResponse> getCourseSchedule(
            @Path("termId") String termId,
            @Path("subject") String subject,
            @Path("catalogNumber") String catalogNumber,
            @Query("key") String apiKey
    );

    @GET("terms/{termId}/{subject}/schedule.json")
    Call<CourseScheduleResponse> getSubjectCourseSchedules(
            @Path("termId") String termId,
            @Path("subject") String subject,
            @Query("key") String apiKey
    );

    @GET("courses/{subject}/{catalogNumber}.json")
    Call<CourseDetailsResponse> getCourseDetails(
            @Path("subject") String subject,
            @Path("catalogNumber") String catalogNumber,
            @Query("key") String apiKey
    );

}

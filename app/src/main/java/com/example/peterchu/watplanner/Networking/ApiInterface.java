package com.example.peterchu.watplanner.Networking;

import com.example.peterchu.watplanner.Models.CourseResponse;

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
}

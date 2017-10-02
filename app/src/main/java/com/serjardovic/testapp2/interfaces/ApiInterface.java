package com.serjardovic.testapp2.interfaces;

import com.serjardovic.testapp2.model.images.dto.PageData;
import com.serjardovic.testapp2.model.images.dto.RequestData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("images.php")
    Call<PageData> getPageData(@Body RequestData request);

}

package com.sharon.employeeattendance.utils;

import com.sharon.employeeattendance.model.EmployeeAttDetails;
import com.sharon.employeeattendance.model.Employees;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {

    String BaseURL = "http://parxsys.com/accounting/";

    @GET("att_rprt_api.php?e76c37b493ea168cea60b8902072387caf297979")
    Call<List<Employees>> getEmployee();

    @FormUrlEncoded
    @POST("att_rprt_api.php?e76c37b493ea168cea60b8902072387caf297979")
    Call<List<EmployeeAttDetails>> getAttDet(
            @Field("emp_id") String emp_id,
            @Field("from_dt") String fromDate,
            @Field("to_dt") String todate

    );

}

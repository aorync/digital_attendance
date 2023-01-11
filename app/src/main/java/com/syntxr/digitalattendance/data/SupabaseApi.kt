package com.syntxr.digitalattendance.data

import com.syntxr.digitalattendance.`object`.Objects
import com.syntxr.digitalattendance.data.model.attendance.AttendanceIn
import com.syntxr.digitalattendance.data.model.attendance.AttendanceOut
import com.syntxr.digitalattendance.data.model.attendance.AttendanceRensponse
import com.syntxr.digitalattendance.data.model.attendance.ImageAttendance
import com.syntxr.digitalattendance.data.model.history.HistoryRensponse
import com.syntxr.digitalattendance.data.model.login.LoginRensponse
import com.syntxr.digitalattendance.data.model.register.Register
import com.syntxr.digitalattendance.data.model.register.RegisterRensponse
import com.syntxr.digitalattendance.data.model.update.Update
import com.syntxr.digitalattendance.data.model.update.UpdateRensponse
import com.syntxr.digitalattendance.data.model.users.ImageProfile
import com.syntxr.digitalattendance.data.model.users.UsersRensponse
import com.syntxr.digitalattendance.data.model.users.UsersRensponseItem
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


interface SupabaseApi {

    @POST("users")
    suspend fun registerUser(
        @Body register : Register,
        @Header("apikey") apikey : String = Objects.API_KEY,
        @Header("Prefer") Prefer : String = Objects.PREFER
    ) : RegisterRensponse

    @GET("users")
    suspend fun loginUser(
        @Query("select") select : String,
        @Query("email") email : String,
        @Query("password") password : String,
        @Header("apikey") apikey : String = Objects.API_KEY
    ) : LoginRensponse

    @GET("users")
    suspend fun allUser(
        @Query("select") select: String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : UsersRensponse

    @PATCH("users")
    suspend fun updateUser(
        @Query("id") id : String,
        @Body update : Update,
        @Header("apikey") apikey: String = Objects.API_KEY,
        @Header("Prefer") Prefer: String = Objects.PREFER
    ) : UpdateRensponse

    @DELETE("users")
    suspend fun deleteUser(
        @Query("id") id: String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : UsersRensponse

    @GET("users")
    suspend fun profilUser(
        @Query("id") id: String,
        @Query("select") select: String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : UsersRensponse

    @POST("attendance")
    suspend fun attendIn(
        @Body attendanceIn : AttendanceIn,
        @Header("apikey") apikey: String = Objects.API_KEY,
        @Header("Prefer") Prefer: String = Objects.PREFER
    ) : AttendanceRensponse

    @PATCH("attendance")
    suspend fun attendOut(
        @Query("user_id") userId : String,
        @Query("date") date: String,
        @Body attendanceOut : AttendanceOut,
        @Header("apikey") apikey: String = Objects.API_KEY,
        @Header("Prefer") Prefer: String = Objects.PREFER
    ) : AttendanceRensponse

    @PATCH("attendance")
    suspend fun uploadImageAttendance(
        @Query("user_id") userId: String,
        @Query("date") date: String,
        @Body imageAttendance : ImageAttendance,
        @Header("apikey") apikey: String = Objects.API_KEY,
        @Header("Prefer") Prefer: String = Objects.PREFER
    ) : AttendanceRensponse

    @PATCH("users")
    suspend fun uploadImageProfile(
        @Query("id") userId: String,
        @Body imageProfile : ImageProfile,
        @Header("apikey") apikey: String =  Objects.API_KEY,
        @Header("Prefer") Prefer: String = Objects.PREFER
    ) : UsersRensponse

    @GET("history")
    suspend fun history(
        @Query("select") select: String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : HistoryRensponse

    @GET("history")
    suspend fun getCheckInHistory(
        @Query("users_id") userId: String,
        @Query("select") select: String,
        @Query("date") date: String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : HistoryRensponse

    @GET("history")
    suspend fun getUserPosDetail(
//        @Query("users_id") userId: String,
        @Query("attendance_id") id: String,
        @Query("select") select: String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : HistoryRensponse

    @GET("attendance")
    suspend fun CheckAttendanceExist(
        @Query("user_id") userId: String,
        @Query("date") date : String,
        @Header("apikey") apikey: String = Objects.API_KEY
    ) : AttendanceRensponse




}
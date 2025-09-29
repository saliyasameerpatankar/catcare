package com.saveetha.myapp.network
import com.saveetha.myapp.ui.auth.*

import com.saveetha.myapp.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
interface ApiService {

    // ----------------- AUTH -----------------
    @Headers("Content-Type: application/json")
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @POST("signup.php")
    fun signup(@Body request: SignupRequest): Call<SignupResponse>

    // JSON request (no image)
    @Headers("Content-Type: application/json")
    @POST("catprofileupdate.php")
    fun addCatProfile(
        @Body requestBody: RequestBody
    ): Call<CatProfileUploadResponse>

    // Multipart request (with image)
    @Multipart
    @POST("addcatprofile.php")
    fun addCatProfileMultipart(
        @Part("userid") userid: RequestBody,
        @Part("catname") catname: RequestBody,
        @Part("age") age: RequestBody,
        @Part("breed") breed: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part photo: MultipartBody.Part? // nullable for no image
    ): Call<CatProfileUploadResponse>


    // Get all cats of a user
    @GET("getcatprofile.php")
    fun getCatProfiles(@Query("userid") userId: Int): Call<CatProfileResponse>

    // ----------------- DELETE CAT -----------------
    @FormUrlEncoded
    @POST("catdelete.php")
    suspend fun deletePet(@Field("catid") catId: Int): retrofit2.Response<DeleteResponse>

    // ----------------- GROOMING -----------------
    @FormUrlEncoded
    @POST("grooming_tasks.php")
    fun addTask(
        @Field("catid") catId: Int,
        @Field("task_name") taskName: String,
        @Field("frequency") frequency: String,
        @Field("last_done") lastDone: String,
        @Field("next_due") nextDue: String
    ): Call<AddTaskResponse>

    @FormUrlEncoded
    @POST("get_grooming.php")
    fun getGroomingTasks(@Field("id") catId: Int): Call<GroomingTaskResponse>

    // ----------------- VET -----------------
    @FormUrlEncoded
    @POST("vetappointmentcreated.php")
    fun getAppointments(@Field("catid") catId: Int): Call<VetAppointmentResponse>

    @FormUrlEncoded
    @POST("addvetappointment.php")
    fun addVetAppointment(
        @Field("catid") catid: Int,
        @Field("next_appointment_date") date: String,
        @Field("next_appointment_time") time: String,
        @Field("reason_or_visit_type") visitType: String,
        @Field("clinic_name") clinicName: String,
        @Field("vet_name") vetName: String,
        @Field("vet_phone_number") vetPhone: String,
        @Field("last_visit_date") lastVisit: String
    ): Call<VetAppointmentResponse>

    // ----------------- WEIGHT -----------------
    @FormUrlEncoded
    @POST("weight_entries.php")
    fun addWeight(
        @Field("catid") catId: Int,
        @Field("date") date: String,
        @Field("weight") weight: Double,
        @Field("notes") notes: String
    ): Call<WeightResponse>

    @GET("weight_entries.php")
    fun getWeights(@Query("catid") catId: Int): Call<List<WeightEntry>>

    // ----------------- PHOTO BOOTH -----------------
    @Multipart
    @POST("upload_photo.php")
    fun uploadPhoto(
        @Part("catid") catId: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<UploadResponse>
}

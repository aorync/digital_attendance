package com.syntxr.digitalattendance.`object`

import com.syntxr.digitalattendance.data.SupabaseApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Objects {

    fun create(): SupabaseApi {
        return Retrofit.Builder()
            .baseUrl(BASH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApi::class.java)
    }

    const val BASH_URL = "https://ngrrvubbkhssalzqgfpd.supabase.co/rest/v1/"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ncnJ2dWJia2hzc2FsenFnZnBkIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NjY3NTA0NjEsImV4cCI6MTk4MjMyNjQ2MX0.KHbQrMuD1BVxcZusdhIt7SwCHqV5fJnC4du1qslZg6U"
    const val PREFER = "return=representation"
    const val CONTENT_TYPE = "application/json"
    const val AUTHORIZATION = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5ncnJ2dWJia2hzc2FsenFnZnBkIiwicm9sZSI6ImFub24iLCJpYXQiOjE2NjY3NTA0NjEsImV4cCI6MTk4MjMyNjQ2MX0.KHbQrMuD1BVxcZusdhIt7SwCHqV5fJnC4du1qslZg6U"
}
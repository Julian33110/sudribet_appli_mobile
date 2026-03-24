package com.example.sudribet

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface EmailApi {
    @POST("send-email")
    fun sendEmail(@Body emailRequest: EmailRequest): Call<Void>
}

data class EmailRequest(
    val to: String,
    val subject: String,
    val content: String
)

object EmailService {
    private const val BASE_URL = "http://10.0.2.2:3000/" // Adresse IP spéciale pour accéder au localhost de l'ordi depuis l'émulateur

    private val api: EmailApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EmailApi::class.java)
    }

    fun sendConfirmationEmail(userEmail: String, userName: String) {
        val request = EmailRequest(
            to = userEmail,
            subject = "Bienvenue chez SudriBet !",
            content = "Félicitations $userName ! Votre compte a été créé avec succès."
        )
        api.sendEmail(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {}
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }

    fun sendBetConfirmation(userEmail: String, betSummary: String, cote: Double, mise: Double) {
        val request = EmailRequest(
            to = userEmail,
            subject = "Confirmation de votre Pari - SudriBet",
            content = "Votre pari a été validé !\n\nRésumé : $betSummary\nCote : $cote\nMise : $mise €\n\nBonne chance !"
        )
        api.sendEmail(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {}
            override fun onFailure(call: Call<Void>, t: Throwable) {}
        })
    }
}
package com.example.sudribet

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ─── Modèles de réponse API ───────────────────────────────────────────────────

data class ApiMatch(
    val id: String,
    val equipeA: String,
    val equipeB: String,
    val categorie: String,
    val heure: String,
    val coteA: Double,
    val coteB: Double,
    val coteNul: Double?,
    val scoreA: Int,
    val scoreB: Int,
    val isLive: Boolean,
    val isUpcoming: Boolean = false,
    val date: String = "",
    val statut: String?
)

data class MatchesResponse(
    val success: Boolean,
    val count: Int,
    val matches: List<ApiMatch>
)

// ─── Interface Retrofit ───────────────────────────────────────────────────────

interface MatchApiService {

    @GET("matches")
    suspend fun getMatches(
        @Query("sport") sport: String? = null,
        @Query("search") search: String? = null,
        @Query("upcoming") upcoming: String? = null
    ): MatchesResponse

    companion object {
        private const val BASE_URL = "https://sublime-manifestation-production-f0ae.up.railway.app/"

        fun create(): MatchApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MatchApiService::class.java)
        }
    }
}

// ─── Conversion ApiMatch → Match ──────────────────────────────────────────────

fun ApiMatch.toMatch() = Match(
    id = this.id,
    equipeA = this.equipeA,
    equipeB = this.equipeB,
    categorie = this.categorie,
    heure = this.heure,
    coteA = this.coteA,
    coteB = this.coteB,
    coteNul = this.coteNul,
    scoreA = this.scoreA,
    scoreB = this.scoreB,
    isLive = this.isLive,
    isUpcoming = this.isUpcoming,
    date = this.date
)

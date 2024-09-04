package com.example.projekat.data

import com.example.projekat.model.Kladionice
import com.example.projekat.model.Rate

interface RateRepository {
    suspend fun getKladionicaRates(
        bid: String
    ): Resource<List<Rate>>
    suspend fun getUserRates(): Resource<List<Rate>>
    suspend fun getUserAdForKladionica(): Resource<List<Rate>>
    suspend fun addRate(
        bid: String,
        rate: Int,
        kladionice: Kladionice
    ): Resource<String>

    suspend fun updateRate(
        rid: String,
        rate: Int,
    ): Resource<String>
}
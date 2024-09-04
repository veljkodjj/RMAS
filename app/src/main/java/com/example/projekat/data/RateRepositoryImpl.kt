package com.example.projekat.data

import com.example.projekat.model.Kladionice
import com.example.projekat.model.Rate
import com.example.projekat.model.service.DatabaseService
import com.example.projekat.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RateRepositoryImpl : RateRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val databaseService = DatabaseService(firestoreInstance)
    override suspend fun getKladionicaRates(
        bid: String
    ): Resource<List<Rate>> {
        return try {
            val rateDocRef = firestoreInstance.collection("rates")
            val querySnapshot = rateDocRef.get().await()
            val ratesList = mutableListOf<Rate>()
            for (document in querySnapshot.documents) {
                val kladionicaId = document.getString("kladionicaId") ?: ""
                if (kladionicaId == bid) {
                    ratesList.add(
                        Rate(
                            id = document.id,
                            userId = document.getString("userId") ?: "",
                            kladionicaId = bid,
                            rate = document.getLong("rate")?.toInt() ?: 0
                        )
                    )
                }
            }
            Resource.Success(ratesList)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override suspend fun getUserRates(): Resource<List<Rate>> {
        return try{
            val rateDocRef = firestoreInstance.collection("rates")
            val querySnapshot = rateDocRef.get().await()
            val ratesList = mutableListOf<Rate>()
            for(document in querySnapshot.documents){
                val userId = document.getString("userId") ?: ""
                if(userId == firebaseAuth.currentUser?.uid){
                    ratesList.add(Rate(
                        id = document.id,
                        kladionicaId = document.getString("kladionicaId") ?: "",
                        userId = userId,
                        rate = document.getLong("rate")?.toInt() ?: 0
                    ))
                }
            }
            Resource.Success(ratesList)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserAdForKladionica(): Resource<List<Rate>> {
        TODO("Not yet implemented")
    }

    override suspend fun addRate(
        bid: String,
        rate: Int,
        kladionice: Kladionice
    ): Resource<String> {
        return try{
            val myRate = Rate(
                userId = firebaseAuth.currentUser!!.uid,
                kladionicaId = bid,
                rate = rate
            )
            databaseService.addPoints(kladionice.userId, rate * 2)
            val result = databaseService.saveRateData(myRate)
            result
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateRate(rid: String, rate: Int): Resource<String> {
        return try{
            val result = databaseService.updateRate(rid, rate)
            result
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}
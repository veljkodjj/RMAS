package com.example.projekat.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient{
    fun getLocationUpdates(interval: Long): Flow<Location>
    class LocationException(message: String) : Exception()
}

//Omogucava azuriranje lokacije u app koristeci tok podataka za asinhrono obaves o promenama

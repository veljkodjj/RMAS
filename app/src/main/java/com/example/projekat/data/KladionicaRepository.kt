package com.example.projekat.data

import android.net.Uri
import com.example.projekat.model.Kladionice
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

interface KladionicaRepository {

    suspend fun getAllKladionice(): Resource<List<Kladionice>>
    suspend fun saveKladionicaData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String>

    suspend fun getUserKladionice(
        uid: String
    ): Resource<List<Kladionice>>
}
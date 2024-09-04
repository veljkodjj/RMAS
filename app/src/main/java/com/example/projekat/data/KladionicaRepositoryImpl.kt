package com.example.projekat.data

import android.net.Uri
import com.example.projekat.model.Kladionice
import com.example.projekat.model.service.DatabaseService
import com.example.projekat.model.service.StorageService
import com.example.projekat.utils.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.maps.model.LatLng


class KladionicaRepositoryImpl : KladionicaRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

    private val databaseService = DatabaseService(firestoreInstance)
    private val storageService = StorageService(storageInstance)


    override suspend fun getAllKladionice(): Resource<List<Kladionice>> {
        return try{
            val snapshot = firestoreInstance.collection("kladionice").get().await()
            val kladionice = snapshot.toObjects(Kladionice::class.java)
            Resource.Success(kladionice)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun saveKladionicaData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String> {
        return try{
            val currentUser = firebaseAuth.currentUser
            if(currentUser!=null){
                val mainImageUrl = storageService.uploadKladionicaMainImage(mainImage)
                val galleryImagesUrls = storageService.uploadKladionicaGalleryImages(galleryImages)
                val geoLocation = GeoPoint(
                    location.latitude,
                    location.longitude
                )
                val kladionica = Kladionice(
                    userId = currentUser.uid,
                    description = description,
                    crowd = crowd,
                    mainImage = mainImageUrl,
                    galleryImages = galleryImagesUrls,
                    location = geoLocation
                )
                databaseService.saveKladionicadata(kladionica)
                databaseService.addPoints(currentUser.uid, 10)
            }
            Resource.Success("Uspesno sačuvani svi podaci o kladionici")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserKladionice(uid: String): Resource<List<Kladionice>> {
        return try {
            val snapshot = firestoreInstance.collection("kladionice")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val kladionice = snapshot.toObjects(Kladionice::class.java)
            Resource.Success(kladionice)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}
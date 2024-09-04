package com.example.projekat

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class KladioniceApp: Application(){
    val db by lazy { Firebase.firestore }


}
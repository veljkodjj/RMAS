package com.example.projekat.model

import com.google.firebase.firestore.DocumentId

data class Rate (
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val kladionicaId: String = "",
    var rate: Int = 0
)
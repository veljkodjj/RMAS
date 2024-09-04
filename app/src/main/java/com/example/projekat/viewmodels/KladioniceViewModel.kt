package com.example.projekat.viewmodels

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projekat.data.KladionicaRepositoryImpl
import com.example.projekat.data.RateRepositoryImpl
import com.example.projekat.data.Resource
import com.example.projekat.model.Kladionice
import com.example.projekat.model.Rate
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class KladioniceViewModel: ViewModel() {
    val repository = KladionicaRepositoryImpl()
    val rateRepository = RateRepositoryImpl()

    private val _kladioniceFlow = MutableStateFlow<Resource<String>?>(null)
    val kladioniceFlow: StateFlow<Resource<String>?> = _kladioniceFlow

    private val _newRate = MutableStateFlow<Resource<String>?>(null)
    val newRate: StateFlow<Resource<String>?> = _newRate

    private val _kladionice = MutableStateFlow<Resource<List<Kladionice>>>(Resource.Success(emptyList()))
    val kladionice: StateFlow<Resource<List<Kladionice>>> get() = _kladionice

    private val _rates = MutableStateFlow<Resource<List<Rate>>>(Resource.Success(emptyList()))
    val rates: StateFlow<Resource<List<Rate>>> get() = _rates


    private val _userKladionice = MutableStateFlow<Resource<List<Kladionice>>>(Resource.Success(emptyList()))
    val userKladionice: StateFlow<Resource<List<Kladionice>>> get() = _userKladionice

    init {
        getAllKladionice()
    }

    fun getAllKladionice() = viewModelScope.launch {
        _kladionice.value = repository.getAllKladionice()
    }

    fun saveKladionicaData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: MutableState<LatLng?>
    ) = viewModelScope.launch{
        _kladioniceFlow.value = Resource.loading
        repository.saveKladionicaData(
            description = description,
            crowd = crowd,
            mainImage = mainImage,
            galleryImages = galleryImages,
            location = location.value!!
        )
        _kladioniceFlow.value = Resource.Success("Uspešno dodata kladionica")
    }


    fun getKladionicaAllRates(
        bid: String
    ) = viewModelScope.launch {
        _rates.value = Resource.loading
        val result = rateRepository.getKladionicaRates(bid)
        _rates.value = result
    }

    fun addRate(
        bid: String,
        rate: Int,
        kladionice: Kladionice
    ) = viewModelScope.launch {
        _newRate.value = rateRepository.addRate(bid, rate, kladionice)
    }

    fun updateRate(
        rid: String,
        rate: Int
    ) = viewModelScope.launch{
        _newRate.value = rateRepository.updateRate(rid, rate)
    }

    fun getUserKladionice(
        uid: String
    ) = viewModelScope.launch {
        _userKladionice.value = repository.getUserKladionice(uid)
    }
}

class KladioniceViewModelFactory:ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(KladioniceViewModel::class.java)){
            return KladioniceViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
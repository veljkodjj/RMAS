package com.example.projekat.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projekat.router.Routes
import com.example.projekat.data.Resource
import com.example.projekat.model.Kladionice
import com.example.projekat.model.Rate
import com.example.projekat.components.KladionicaMainImage
import com.example.projekat.components.CustomBackButton
import com.example.projekat.components.CustomKladionicaGallery
import com.example.projekat.components.CustomKladionicaLocation
import com.example.projekat.components.CustomKladionicaRate
import com.example.projekat.components.CustomCrowdIndicator
import com.example.projekat.components.CustomRateButton
import com.example.projekat.components.greyText
import com.example.projekat.components.greyTextBigger
import com.example.projekat.components.headingText
import com.example.projekat.screens.dialogs.RateKladionicaDialog
import com.example.projekat.viewmodels.AuthViewModel
import com.example.projekat.viewmodels.KladioniceViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.math.RoundingMode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun KladionicaScreen(
    navController: NavController,
    kladioniceViewModel: KladioniceViewModel,
    kladionica: Kladionice,
    viewModel: AuthViewModel,
    kladionice: MutableList<Kladionice>?
){
    val ratesResources = kladioniceViewModel.rates.collectAsState()
    val newRateResource = kladioniceViewModel.newRate.collectAsState()

    val rates = remember {
        mutableListOf<Rate>()
    }
    val averageRate = remember {
        mutableStateOf(0.0)
    }
    val showRateDialog = remember {
        mutableStateOf(false)
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    val myPrice = remember {
        mutableStateOf(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KladionicaMainImage(kladionica.mainImage)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            item{ CustomBackButton {
                if(kladionice == null) {
                    navController.popBackStack()
                }else{
                    val isCameraSet = true
                    val latitude = kladionica.location.latitude
                    val longitude = kladionica.location.longitude

                    val kladioniceJson = Gson().toJson(kladionice)
                    val encodedKladioniceJson = URLEncoder.encode(kladioniceJson, StandardCharsets.UTF_8.toString())
                    navController.navigate(Routes.indexScreenWithParams + "/$isCameraSet/$latitude/$longitude/$encodedKladioniceJson")
                }
            }}
            item{Spacer(modifier = Modifier.height(220.dp))}
            item{ CustomCrowdIndicator(crowd = kladionica.crowd)}
            item{Spacer(modifier = Modifier.height(20.dp))}
            item{headingText(textValue = "Kladionica u blizini")}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item{CustomKladionicaLocation(location = LatLng(kladionica.location.latitude, kladionica.location.longitude))}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item{CustomKladionicaRate(average = averageRate.value)}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item{greyTextBigger(textValue = kladionica.description.replace('+', ' '))}
            item{Spacer(modifier = Modifier.height(20.dp))}
            item{Text(text = "Galerija kladionice", style= TextStyle(fontSize = 20.sp))};
//            item{ CustomCrowdIndicator(crowd = 1)}
            item{Spacer(modifier = Modifier.height(10.dp))}
            item { CustomKladionicaGallery(images = kladionica.galleryImages)}
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 15.dp, vertical = 20.dp)
        ) {
            CustomRateButton(
                enabled = if(kladionica.userId == viewModel.currentUser?.uid) false
                else true,
                onClick = {
                    val rateExist = rates.firstOrNull{
                        it.kladionicaId == kladionica.id && it.userId == viewModel.currentUser!!.uid
                    }
                    if(rateExist != null)
                        myPrice.value = rateExist.rate
                    showRateDialog.value = true
                })
        }


        if(showRateDialog.value){
            RateKladionicaDialog(
                showRateDialog = showRateDialog,
                rate = myPrice,
                rateKladionica = {

                    val rateExist = rates.firstOrNull{
                        it.kladionicaId == kladionica.id && it.userId == viewModel.currentUser!!.uid
                    }
                    if(rateExist != null){
                        isLoading.value = true
                        kladioniceViewModel.updateRate(
                            rid = rateExist.id,
                            rate = myPrice.value
                        )
                    }else {
                        isLoading.value = true
                        kladioniceViewModel.addRate(
                            bid = kladionica.id,
                            rate = myPrice.value,
                            kladionice = kladionica
                        )
                    }
                },
                isLoading = isLoading
            )
        }
    }

    ratesResources.value.let {
        when(it){
            is Resource.Success -> {
                rates.addAll(it.result)
                var sum = 0.0
                for (rate in it.result){
                    sum += rate.rate.toDouble()
                }
                if(sum != 0.0) {
                    val rawPositive = sum / it.result.count()
                    val rounded = rawPositive.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                    averageRate.value = rounded
                }  else {}
            }
            is Resource.loading -> {

            }
            is Resource.Failure -> {
                Log.e("Podaci", it.toString())
            }
        }
    }
    newRateResource.value.let {
        when(it){
            is Resource.Success -> {
                isLoading.value = false

                val rateExist = rates.firstOrNull{rate ->
                    rate.id == it.result
                }
                if(rateExist != null){
                    rateExist.rate = myPrice.value
                }
            }
            is Resource.loading -> {
//                isLoading.value = false
            }
            is Resource.Failure -> {
                val context = LocalContext.current
                Toast.makeText(context, "Došlo je do greške prilikom ocenjivanja kladionice", Toast.LENGTH_LONG).show()
                isLoading.value = false
            }
            null -> {
                isLoading.value = false
            }
        }
    }
}
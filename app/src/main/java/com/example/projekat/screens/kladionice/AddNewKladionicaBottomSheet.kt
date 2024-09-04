package com.example.projekat.screens.kladionice

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.projekat.R
import com.example.projekat.components.CustomCrowd
import com.example.projekat.components.CustomGalleryForAddNewKladionica
import com.example.projekat.components.CustomImageForNewKladionica
import com.example.projekat.components.customRichTextInput
import com.example.projekat.components.headingText
import com.example.projekat.components.inputTextIndicator
import com.example.projekat.components.loginRegisterCustomButton
import com.example.projekat.data.Resource
import com.example.projekat.viewmodels.KladioniceViewModel
import com.google.android.gms.maps.model.LatLng


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNewKladionicaBottomSheet(
    kladioniceViewModel: KladioniceViewModel?,
    location: MutableState<LatLng?>,
    sheetState: ModalBottomSheetState
) {
    val kladionicaFlow = kladioniceViewModel?.kladioniceFlow?.collectAsState()
    val inputDescription = remember {
        mutableStateOf("")
    }
    val isDescriptionError = remember {
        mutableStateOf(false)
    }
    val descriptionError = remember {
        mutableStateOf("Ovo polje je obavezno")
    }
    val selectedOption = remember {
        mutableStateOf(0)
    }
    val buttonIsEnabled = remember {
        mutableStateOf(true)
    }
    val buttonIsLoading = remember {
        mutableStateOf(false)
    }

    val selectedImage = remember {
        mutableStateOf<Uri?>(Uri.EMPTY)
    }
    val selectedGallery = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val showedAlert = remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp, horizontal = 20.dp)
    ) {
        item{headingText(textValue = stringResource(id = R.string.add_new_kladionica_heading))}
        item{Spacer(modifier = Modifier.height(20.dp))}
        item{ CustomImageForNewKladionica(selectedImageUri = selectedImage)}
        item{Spacer(modifier = Modifier.height(20.dp))}
        item{inputTextIndicator(textValue = "Opis")}
        item{Spacer(modifier = Modifier.height(5.dp))}
        item{customRichTextInput(inputValue = inputDescription, inputText = "Unesite opis", isError = isDescriptionError, errorText = descriptionError)}
        item{Spacer(modifier = Modifier.height(20.dp))}
        item{inputTextIndicator(textValue = "Gužva")}
        item{Spacer(modifier = Modifier.height(5.dp))}
        item{CustomCrowd(selectedOption)}
        item{Spacer(modifier = Modifier.height(20.dp))}
        item{inputTextIndicator(textValue = "Galerija")}
        item{Spacer(modifier = Modifier.height(5.dp))}
        item{CustomGalleryForAddNewKladionica(selectedImages = selectedGallery)}
        item{Spacer(modifier = Modifier.height(20.dp))}
        item{loginRegisterCustomButton(buttonText = "Dodaj kladionicu", isEnabled = buttonIsEnabled, isLoading = buttonIsLoading) {
            showedAlert.value = false;
            buttonIsLoading.value = true
            kladioniceViewModel?.saveKladionicaData(
                description = inputDescription.value,
                crowd = selectedOption.value,
                mainImage = selectedImage.value!!,
                galleryImages = selectedGallery.value,
                location = location
            )
        }}
        item{Spacer(modifier = Modifier.height(5.dp))}
//        Spacer(modifier = Modifier.height(100.dp))
    }
    kladionicaFlow?.value.let {
        when (it){
            is Resource.Failure -> {
                Log.d("Stanje flowa", it.toString());
                buttonIsLoading.value = false
                val context = LocalContext.current
                if(!showedAlert.value) {
//                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                    showedAlert.value = true
                    kladioniceViewModel?.getAllKladionice()
                }else{}
            }
            is Resource.loading -> {

            }
            is Resource.Success -> {
                Log.d("Stanje flowa", it.toString());
                buttonIsLoading.value = false
                val context = LocalContext.current
                if(!showedAlert.value) {
//                    Toast.makeText(context, "Uspesno dodato", Toast.LENGTH_LONG).show()
                    showedAlert.value = true
                    kladioniceViewModel?.getAllKladionice()
                }else{}
            }
            null -> {}
        }
    }
}

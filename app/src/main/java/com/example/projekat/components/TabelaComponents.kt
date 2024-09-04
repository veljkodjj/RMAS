package com.example.projekat.components

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.outlined.ShareLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projekat.router.Routes
import com.example.projekat.model.Kladionice
import com.example.projekat.ui.theme.greyTextColor
import com.example.projekat.ui.theme.lightGreyColor
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun CustomTabela(
    kladionice: List<Kladionice>?,
    navController: NavController
) {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
            .horizontalScroll(horizontalScrollState)
    ) {
        Column {
            CustomTabelaHeader()

            Box(
                modifier = Modifier
                    .width(500.dp)
                    .height(2.dp)
                    .background(greyTextColor)
            )

            kladionice?.forEachIndexed { index, kladionica ->
                CustomTabelaRow(type = index%2, kladionica, openKladionicaScreen = {
                    val kladionicaJson = Gson().toJson(kladionica)
                    val encodedKladionicaJson = URLEncoder.encode(kladionicaJson, StandardCharsets.UTF_8.toString())
                    navController.navigate(Routes.kladionicaScreen + "/$encodedKladionicaJson")
                },
                    openKladionicaLocation = {
                        val isCameraSet = true
                        val latitude = kladionica.location.latitude
                        val longitude = kladionica.location.longitude

                        val kladioniceJson = Gson().toJson(kladionice)
                        val encodedKladioniceJson = URLEncoder.encode(kladioniceJson, StandardCharsets.UTF_8.toString())
                        navController.navigate(Routes.indexScreenWithParams + "/$isCameraSet/$latitude/$longitude/$encodedKladioniceJson")
                    }

                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun CustomTabelaHeader() {
    val boxModifier = Modifier.padding(12.dp)
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.width(60.dp))

        Box(modifier = boxModifier.width(200.dp)) {
            Text(
                text = "Opis",
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        }

        Box(modifier = boxModifier.width(120.dp)) {
            Text(
                text = "Gužva",
                style = TextStyle(
                    fontSize = 20.sp
                )
            )
        }

        Box(modifier = boxModifier.width(50.dp)) {}
    }
}

@Composable
fun CustomTabelaRow(
    type: Int,
    kladionica: Kladionice,
    openKladionicaScreen: () -> Unit,
    openKladionicaLocation: () -> Unit
) {
    val boxModifier = Modifier.padding(12.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (type == 0) Color.Transparent else lightGreyColor
            )
            .clickable { openKladionicaScreen() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(60.dp)){
            AsyncImage(
                model = kladionica.mainImage,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
                    .height(60.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
        }


        Box(modifier = boxModifier.width(200.dp)) {
            Text(
                text = if(kladionica.description.length > 20) kladionica.description.substring(0, 20).replace('+', ' ') + "..." else kladionica.description.replace('+', ' '),
                style = TextStyle(
                    fontSize = 16.sp
                )
            )
        }

        Box(modifier = boxModifier.width(120.dp)) {
            CustomCrowdIndicator(crowd = kladionica.crowd)
        }
        Box(modifier = boxModifier.width(50.dp)) {
            IconButton(
                onClick = openKladionicaLocation,
            ){
                Icon(
                    imageVector = Icons.Outlined.ShareLocation,
                    contentDescription = ""
                )
            }
        }
    }
}

package com.example.projekat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projekat.router.Routes
import com.example.projekat.R
import com.example.projekat.model.Kladionice
import com.example.projekat.ui.theme.buttonDisabledColor
import com.example.projekat.ui.theme.lightGreyColor
import com.example.projekat.ui.theme.mainColor
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun TextWithLabel(label: String, count: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = count, style = MaterialTheme.typography.h6)
        Text(text = label, style = MaterialTheme.typography.body2, color = Color.Gray)
    }
}

@Composable
fun ProfileButton(text: String) {
    Button(
        onClick = { /* Handle click */ },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun PhotosSection(
    kladionice: List<Kladionice>,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "Dodate kladionice", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Prikazivanje slika
            if(kladionice.isNotEmpty()) {
                for (kladionica in kladionice) {
                    item {
                        AsyncImage(
                            model = kladionica.mainImage,
                            contentScale = ContentScale.Crop,
                            contentDescription = "",
                            modifier =
                            Modifier
                                .width(150.dp)
                                .height(150.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Color.White,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    val kladionicaJson = Gson().toJson(kladionica)
                                    val encodedKladionicaJson = URLEncoder.encode(
                                        kladionicaJson,
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    navController.navigate(Routes.kladionicaScreen + "/$encodedKladionicaJson")
                                }
                        )
                    }
                }
            }else{
                item {
                    Image(
                        imageVector = Icons.Filled.DoNotDisturb,
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier =
                        Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                lightGreyColor,
                                RoundedCornerShape(20.dp)
                            )
                    )
                }
            }
        }
    }
}


@Composable
fun LogoutButton(
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .height(60.dp)
                .background(mainColor, RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = lightGreyColor,
                contentColor = Color.Black,
                disabledContainerColor = buttonDisabledColor,
                disabledContentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Odjavi se",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(imageVector = Icons.Filled.Logout, contentDescription = "")
            }
        }
    }
}

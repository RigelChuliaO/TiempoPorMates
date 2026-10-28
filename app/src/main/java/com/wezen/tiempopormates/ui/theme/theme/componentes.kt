package com.wezen.tiempopormates.ui.theme.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wezen.tiempopormates.R

//CLASE CREADA PARA DEFINIR LOS FORMATOS UI DE LOS ELEMENTOS QUE APRECEN EN LA APLICACION
//Boton Corregir
val ButtonGreen = Color(0xFF6AB04C)
val NeonRed = Color(0xFFFF4500)

@Composable
fun BotonCorregir(
    text: String,//Texto que aparece sobre el boton
    onClick: () -> Unit, //Lo que hara cuando lo pulse
    modifier: Modifier = Modifier, //Por si se le tiene que pedir alguna modificacion al boton estandar
    enabled: Boolean = true //Para poder cambiar su estado de enable
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
        shape = RoundedCornerShape(24.dp), // Bordes redondeados
        enabled = enabled, // Habilitar o deshabilitar el botón
        // El botón en sí no necesita sombra, aplicamos el resplandor a su contenedor
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .height(60.dp)
            // **CLAVE:** Aplicamos el resplandor usando el color del botón
            .shadow(
                elevation = 16.dp, // Intensidad alta para un botón prominente
                shape = RoundedCornerShape(24.dp),
                spotColor = ButtonGreen,
                ambientColor = ButtonGreen
            )
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.letratiza))
        )

    }
}
//Boton Siguiente
@Composable
fun BotonSiguiente(
    text: String,//Texto que aparece sobre el boton
    onClick: () -> Unit, //Lo que hara cuando lo pulse
    modifier: Modifier = Modifier //Por si se le tiene que pedir alguna modificacion al boton estandar
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF868C80),
            contentColor = Color(0xFF868C80)
        )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily(Font(R.font.letratiza))
        )
    }
}
//Boton Atras
@Composable
fun BotonAtras(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(60.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4B8C13),
            contentColor = Color.White
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Atrás",
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun BrilloTexto(text: String, color: Color, size: Int = 40) {
    Text(
        text = text,
        color = color, // Color del texto (puede ser blanco o un color claro)
        fontSize = size.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            // **CLAVE:** El modificador shadow crea el resplandor
            .shadow(
                elevation = 8.dp, // La "intensidad" del resplandor
                shape = RectangleShape, // O la forma de tu elemento
                spotColor = color, // Usamos el color base del elemento como color del brillo
                ambientColor = color // Mismo color para un brillo uniforme
            )
    )
}

package portfolio

import Greeting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dineshportfolio.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import dineshportfolio.composeapp.generated.resources.compose_multiplatform

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Preview
fun MainLandingPage() {
    MaterialTheme {
        val pagerState = rememberPagerState(pageCount = {3})

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> PageOne()
                1 -> PageTwo()
                2 -> PageThree()
            }
        }
    }
}

@Composable
fun PageOne() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(64.dp)
    ) {
        Text(
            "Hello, I am",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            "Dinesh.",
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
            fontFamily = FontFamily.SansSerif
        )
        Text(
            "Here is where people would say \"I'm a Developer\"",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp),
            color = Color.Gray,
            fontFamily = FontFamily.SansSerif
        )
        Text(
            "but I'm gonna do something a little different.",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp),
            color = Color.Gray,
            fontFamily = FontFamily.SansSerif
        )
    }
}

@Composable
fun PageTwo() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Cyan)
    ) {
        BasicText(
            text = "This is the second page",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PageThree() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Magenta)
    ) {
        BasicText(
            text = "This is the third page",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
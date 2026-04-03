package com.example.testfilterimage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import com.example.testfilterimage.ui.theme.TestFilterImageTheme
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestFilterImageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ImageListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ImageListScreen(modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val images = List(150) { imageIndex ->
        drawableToCacheFile(
            context = context,
            drawableRes = R.drawable.android_logo,
            fileName = "android_logo_$imageIndex.png"
        )
    }

    LaunchedEffect(true) {
        listState.scrollToItem(images.lastIndex)
    }

    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                items = images
            ) { index, image ->

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Item $index")
                    Spacer(Modifier.width(12.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(image)
                            .size(2100)
                            .transformations(ImageTransformation(Filter.entries.random()))
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    scope.launch {
                        listState.scrollToItem(0)
                    }
                }
            ) {
                Text("⬆️ Top")
            }

            Button(
                onClick = {
                    scope.launch {
                        listState.scrollToItem(images.lastIndex)
                    }
                }
            ) {
                Text("⬇️ Bottom")
            }
        }
    }
}

private fun drawableToCacheFile(
    context: Context,
    @DrawableRes drawableRes: Int,
    fileName: String
): File {
    val file = File(context.cacheDir, fileName)
    if (!file.exists()) {
        context.resources.openRawResource(drawableRes).use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
    return file
}

package com.example.testfilterimage

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
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
//                    ImageCarouselScreen(modifier = Modifier.padding(innerPadding))
                    ImageListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ImageCarouselScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val filters = Filter.entries
    val initialSelectedIndexes = remember(filters) {
        listOf(0, filters.lastIndex, filters.size / 2)
    }
    val selectedFilterIndexes = remember {
        mutableStateListOf<Int>().apply {
            addAll(initialSelectedIndexes)
        }
    }
    val pagerImages = remember(context) {
        List(3) { index ->
            drawableToCacheFile(
                context = context,
                drawableRes = R.drawable.android_logo,
                fileName = "android_logo_page_$index.png"
            )
        }
    }
    val pageRows = remember(context) {
        List(3) { pageIndex ->
            List(filters.size) { imageIndex ->
                drawableToCacheFile(
                    context = context,
                    drawableRes = R.drawable.android_logo,
                    fileName = "android_logo_${pageIndex}_$imageIndex.png"
                )
            }
        }
    }
    val pagerState = rememberPagerState(pageCount = { pagerImages.size })

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Sample",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            val pageImage = pagerImages[page]
            val pageImagesRow = pageRows[page]
            val selectedFilterIndex = selectedFilterIndexes[page]
            val selectedFilter = filters[selectedFilterIndex]
            val listState = rememberLazyListState()

            LaunchedEffect(page) {
                listState.scrollToItem(selectedFilterIndex)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                val thumbnailSize = 80.dp
                val thumbnailSizeFactor = 10
                val originalImageLoadingSize = with(LocalDensity.current) { thumbnailSize.times(thumbnailSizeFactor).toPx().toInt() }
                val headerImageRequest = //remember(pageImage, selectedFilter) {
                    ImageRequest.Builder(context)
                        .data(pageImage)
                        .transformations(ImageTransformation(selectedFilter))
                        .build()
                //}

                AsyncImage(
                    model = headerImageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.LightGray)
                )

                Text(
                    text = "Filters",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pageImagesRow.zip(filters).withIndex().toList()) { indexedItem ->
                        val filterIndex = indexedItem.index
                        val (imageFile, filter) = indexedItem.value
                        val isSelected = selectedFilterIndex == filterIndex
                        val imageRequest = //remember(imageFile, filter) {
                            ImageRequest.Builder(context)
                                .data(imageFile)
                                .size(originalImageLoadingSize)
                                .transformations(ImageTransformation(filter))
                                .build()
                        //}

                        Column(
                            modifier = Modifier.clickable {
                                selectedFilterIndexes[page] = filterIndex
                            },
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AsyncImage(
                                model = imageRequest,
                                contentDescription = filter.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
//                                    .size(width = 220.dp, height = 140.dp)
                                    .size(thumbnailSize)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) {
                                            Color(filter.colorInt).copy(alpha = 0.22f)
                                        } else {
                                            Color.LightGray
                                        }
                                    )
                            )
                            Text(
                                text = filter.label,
                                color = if (isSelected) {
                                    Color(filter.colorInt)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerImages.size) { index ->
                val isSelected = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(
                            if (isSelected) Color.DarkGray else Color.LightGray
                        )
                )
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

@Preview(showBackground = true)
@Composable
fun ImageCarouselPreview() {
    TestFilterImageTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            ImageCarouselScreen()
        }
    }
}

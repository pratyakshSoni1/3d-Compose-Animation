package com.adityaikhbalm.slideranimation.presentation

import android.annotation.SuppressLint
import android.graphics.Path
import android.graphics.RectF
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser
import com.adityaikhbalm.slideranimation.R
import com.adityaikhbalm.slideranimation.presentation.shape.CustomShape
import com.adityaikhbalm.slideranimation.presentation.shape.OverlayImagePainter
import com.adityaikhbalm.slideranimation.presentation.shape.VectorDrawableParser
import com.adityaikhbalm.slideranimation.ui.theme.apple
import com.adityaikhbalm.slideranimation.ui.theme.apple2
import com.adityaikhbalm.slideranimation.ui.theme.orange
import com.adityaikhbalm.slideranimation.ui.theme.orange2
import com.adityaikhbalm.slideranimation.ui.theme.strawberry
import com.adityaikhbalm.slideranimation.ui.theme.strawberry2
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("RestrictedApi", "CoroutineCreationDuringComposition")
@Composable
fun SliderAnimation() {
    val colorBackground = arrayListOf(
        strawberry,
        orange,
        apple
    )
    val colorBackground2 = arrayListOf(
        strawberry2,
        orange2,
        apple2
    )

    var pageOffset: Float by remember { mutableFloatStateOf(0f) }
    var pageCurrent: Int by remember { mutableIntStateOf(0) }
    var pageCurrentAnimation: Int by remember { mutableIntStateOf(0) }
    var boxWidth: Int by remember { mutableIntStateOf(0) }
    var boxHeight: Int by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                boxWidth = coordinates.size.width
                boxHeight = coordinates.size.height
            }
            .background(
                Brush.radialGradient(
                    if (pageCurrent < 2) {
                        listOf(
                            lerp(
                                colorBackground2[pageCurrent],
                                colorBackground2[pageCurrent + 1],
                                pageOffset
                            ),
                            lerp(
                                colorBackground[pageCurrent],
                                colorBackground[pageCurrent + 1],
                                pageOffset
                            )
                        )
                    } else {
                        listOf(
                            lerp(
                                colorBackground2[pageCurrent],
                                colorBackground2[pageCurrent - 1],
                                pageOffset
                            ),
                            lerp(
                                colorBackground[pageCurrent],
                                colorBackground[pageCurrent - 1],
                                pageOffset
                            )
                        )
                    }
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val rowScrollState = rememberScrollState()
        val columnScrollState = rememberScrollState()
        val scope = rememberCoroutineScope()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp
        val slider = listOf(0, boxWidth, boxWidth * 2)
        val slider2 = listOf(0, boxHeight, boxHeight * 2)

        Row(
            modifier = Modifier
                .horizontalScroll(rowScrollState)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val text = arrayOf("STRAWBERRY", "ORANGE", "APPLE")
            val textSize = arrayOf(52.sp, 72.sp, 92.sp)
            text.forEachIndexed { index, s ->
                TextBackground(s, screenWidth, textSize[index])
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(columnScrollState)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "translate")
            val valueTranslate by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000,
                        easing = {
                            AccelerateDecelerateInterpolator().getInterpolation(it)
                        }
                    ),
                    repeatMode = RepeatMode.Reverse
                ), label = "translate"
            )

            Box(
                modifier = Modifier.size(screenWidth, screenHeight),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.strawberry1),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 2.3f, screenHeight / 2.8f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 50
                                rotationZ = valueTranslate * 10
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.strawberry6),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 5, screenHeight / 2.5f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = -valueTranslate * 100
                                translationY = -(valueTranslate * 100)
                                rotationZ = valueTranslate * 360
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.strawberry2),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 7, screenHeight / 2.9f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 100
                                translationY = valueTranslate * 200
                                rotationZ = valueTranslate * 60
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.strawberry3),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 2.5f, screenHeight / 9)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = -valueTranslate * 100
                                translationY = valueTranslate * 50
                                rotationZ = valueTranslate * 20
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.strawberry4),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 3.7f, -screenHeight / 4)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = -(valueTranslate * 30)
                                translationY = valueTranslate * 100
                                rotationZ = -(valueTranslate * 90)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.strawberry7),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 4, -screenHeight / 4)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationY = valueTranslate * 50
                                rotationZ = valueTranslate * 60
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.strawberry5),
                        contentDescription = "strawberry",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 2.4f, -screenHeight / 7f)
                            .scale(1.5f)
                            .graphicsLayer {
                                rotationZ = -(valueTranslate * 360)
                            }
                    )
                }
            }

            Box(
                modifier = Modifier.size(screenWidth, screenHeight),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.orange1),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 2.5f, screenHeight / 3f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 50
                                translationY = valueTranslate * 50
                                rotationZ = valueTranslate * 10
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange2),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 15f, screenHeight / 2.8f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 20
                                translationY = valueTranslate * 50
                                rotationZ = -(valueTranslate * 30)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange3),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 2.8f, screenHeight / 3.4f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 20
                                translationY = valueTranslate * 100
                                rotationZ = valueTranslate * 60
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange8),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 3.1f, screenHeight / 7f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = -(valueTranslate * 50)
                                rotationZ = valueTranslate * 60
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange4),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 2.9f, -screenHeight / 10f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 50
                                rotationZ = -(valueTranslate * 120)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange9),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 3.8f, -screenHeight / 4f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 50
                                rotationZ = valueTranslate * 180
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange5),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 13f, -screenHeight / 3f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 50
                                translationY = valueTranslate * 50
                                rotationZ = valueTranslate * 180
                                scaleX = 1f + if (valueTranslate <= 0.6f) valueTranslate else 0.6f
                                scaleY = 1f + if (valueTranslate <= 0.6f) valueTranslate else 0.6f
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange6),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 4f, -screenHeight / 3.5f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 90
                                translationY = -(valueTranslate * 60)
                                rotationZ = valueTranslate * 120
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange10),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 2.4f, -screenHeight / 5.1f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 90
                                translationY = valueTranslate * 100
                                rotationZ = -(valueTranslate * 160)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.orange7),
                        contentDescription = "orange",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 2.1f, -screenHeight / 14f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 30
                                translationY = valueTranslate * 150
                                rotationZ = valueTranslate * 160
                            }
                    )
                }
            }

            Box(
                modifier = Modifier.size(screenWidth, screenHeight),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.apple1),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 3f, screenHeight / 3.1f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 100
                                translationY = -(valueTranslate * 50)
                                rotationZ = -(valueTranslate * 30)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.apple2),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 7f, screenHeight / 2.9f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 50
                                translationY = valueTranslate * 50
                                rotationZ = valueTranslate * 30
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.apple3),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 2.8f, screenHeight / 5f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = -(valueTranslate * 70)
                                translationY = -(valueTranslate * 160)
                                rotationZ = -(valueTranslate * 30)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.apple4),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(-screenWidth / 3.8f, -screenHeight / 5f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = -(valueTranslate * 70)
                                translationY = valueTranslate * 160
                                rotationZ = -(valueTranslate * 80)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.apple7),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 6f, -screenHeight / 4.5f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 70
                                rotationZ = -(valueTranslate * 10)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.apple5),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 4.2f, -screenHeight / 4.2f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 70
                                rotationZ = -(valueTranslate * 10)
                            }
                    )

                    Image(
                        painter = painterResource(id = R.drawable.apple6),
                        contentDescription = "apple",
                        modifier = Modifier
                            .wrapContentSize()
                            .offset(screenWidth / 3f, -screenHeight / 7f)
                            .scale(1.5f)
                            .graphicsLayer {
                                translationX = valueTranslate * 100
                                translationY = valueTranslate * 150
                                rotationZ = valueTranslate * 60
                                scaleX = 0.8f + valueTranslate
                                scaleY = 0.8f + valueTranslate
                            }
                    )
                }
            }
        }

        val data = VectorDrawableParser.parsedVectorDrawable(
            LocalContext.current.resources,
            R.drawable.cansvg
        )
        val path = PathParser.createPathFromPathData(data?.pathData)
        val pathWidth = getAreaFromPath(path).width().roundToInt().pxToDp()
        val pathHeight = getAreaFromPath(path).height().roundToInt().pxToDp()

        Spacer(
            modifier = Modifier
                .offset(0.dp, pathHeight / 1.9f)
                .height(30.dp)
                .width(pathWidth / 1.3f)
                .clip(RoundedCornerShape(30.dp, 30.dp, 15.dp, 15.dp))
                .advancedShadow(
                    color = Color(0x341A1818),
                    alpha = 1f,
                    cornersRadius = 30.dp,
                    shadowBlurRadius = 8.dp,
                    offsetY = (-15).dp,
                    offsetX = 0.dp
                )
        )

        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(CustomShape(path)),
            contentAlignment = Alignment.Center,
        ) {
            val background = arrayListOf(
                ImageBitmap.imageResource(id = R.drawable.bgstrawberry),
                ImageBitmap.imageResource(id = R.drawable.bgorange),
                ImageBitmap.imageResource(id = R.drawable.bgapple)
            )
            val mask = ImageBitmap.imageResource(id = R.drawable.can)
            val customPainter = mutableListOf(
                OverlayImagePainter(mask, background[0]),
                OverlayImagePainter(mask, background[1]),
                OverlayImagePainter(mask, background[2])
            )

            Image(
                painter = painterResource(id = R.drawable.can),
                contentDescription = "can",
                modifier = Modifier.wrapContentSize()
            )

            val pageCount = 3
            val pagerState = rememberPagerState { pageCount }

            val fling = PagerDefaults.flingBehavior(
                state = pagerState,
                pagerSnapDistance = PagerSnapDistance.atMost(3)
            )

            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = pageCount,
                flingBehavior = fling,
                modifier = Modifier
                    .size(pathWidth, pathHeight)
            ) {
                val currentOffset = pagerState.currentPageOffsetFraction
                pageOffset = if (currentOffset >= 0f) currentOffset else 1 - abs(currentOffset)

                if (pageCurrent > 0) {
                    if (currentOffset < 0f) pageCurrent = pagerState.currentPage - 1
                }

                if (1 - abs(currentOffset) >= 1f) {
                    pageCurrent = pagerState.currentPage
                    pageCurrentAnimation = pagerState.currentPage
                }

                scope.launch {
                    if (pageCurrent == 0) {
                        columnScrollState.scrollTo((slider2[1] * pageOffset).roundToInt())
                    } else if (pageCurrent == 1) {
                        columnScrollState.scrollTo((slider2[1] * pageOffset).roundToInt() + slider2[1])
                    }

                    if (1 - abs(currentOffset) >= 1f) {
                        rowScrollState.animateScrollTo(slider[pageCurrent])
                        columnScrollState.scrollTo((slider2[pageCurrent]))
                    }
                }

                Image(
                    painter = customPainter[it],
                    contentDescription = "fruit soda",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
}

@Composable
fun TextBackground(text: String, screenWidth: Dp, textSize: TextUnit) {
    Box(
        modifier = Modifier.size(screenWidth),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = textSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun getAreaFromPath(sourcePath: Path): RectF {
    val rectF = RectF()
    sourcePath.computeBounds(rectF, true)

    return rectF
}
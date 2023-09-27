package com.adityaikhbalm.slideranimation.presentation.shape

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Path
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.AndroidPath
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toSize
import org.xmlpull.v1.XmlPullParser
import kotlin.math.roundToInt


class OverlayImagePainter constructor(
    private val image: ImageBitmap,
    private val imageOverlay: ImageBitmap,
    private val srcOffset: IntOffset = IntOffset.Zero,
    private val srcSize: IntSize = IntSize(image.width, image.height),
    private val overlaySize: IntSize = IntSize(imageOverlay.width, imageOverlay.height)
) : Painter() {

    private val size: IntSize = validateSize(srcOffset, srcSize)
    override fun DrawScope.onDraw() {
        // draw the first image without any blend mode
        drawImage(
            image,
            srcOffset,
            srcSize,
            dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            ),
            blendMode = BlendMode.Color
        )
        // draw the second image with an Overlay blend mode to blend the two together
        drawImage(
            imageOverlay,
            srcOffset,
            overlaySize,
            dstSize = IntSize(
                this@onDraw.size.width.roundToInt(),
                this@onDraw.size.height.roundToInt()
            ),
            blendMode = BlendMode.Multiply
        )
    }

    /**
     * Return the dimension of the underlying [ImageBitmap] as it's intrinsic width and height
     */
    override val intrinsicSize: Size get() = size.toSize()

    private fun validateSize(srcOffset: IntOffset, srcSize: IntSize): IntSize {
        require(
            srcOffset.x >= 0 &&
                    srcOffset.y >= 0 &&
                    srcSize.width >= 0 &&
                    srcSize.height >= 0 &&
                    srcSize.width <= image.width &&
                    srcSize.height <= image.height
        )
        return srcSize
    }
}

data class ParsedVectorDrawable(
    val width: Float,
    val height: Float,
    val viewportWidth: Float,
    val viewportHeight: Float,
    val pathData: String
)

object VectorDrawableParser {

    private val digitsOnly: Regex
        get() = Regex("[^0-9.]")

    @SuppressLint("ResourceType")
    fun parsedVectorDrawable(
        resources: Resources,
        @DrawableRes drawable: Int
    ): ParsedVectorDrawable? {
        var pathData: String? = null
        var width: Float? = null
        var height: Float? = null
        var viewportWidth: Float? = null
        var viewportHeight: Float? = null

        // This is very simple parser, it doesn't support <group> tag, nested tags and other stuff
        resources.getXml(drawable).use { xml ->
            var event = xml.eventType
            while (event != XmlPullParser.END_DOCUMENT) {

                if (event != XmlPullParser.START_TAG) {
                    event = xml.next()
                    continue
                }

                when (xml.name) {
                    "vector" -> {
                        width = xml.getAttributeValue(getAttrPosition(xml, "width"))
                            .replace(digitsOnly, "")
                            .toFloatOrNull()
                        height = xml.getAttributeValue(getAttrPosition(xml, "height"))
                            .replace(digitsOnly, "")
                            .toFloatOrNull()
                        viewportWidth = xml.getAttributeValue(getAttrPosition(xml, "viewportWidth"))
                            .toFloatOrNull()
                        viewportHeight =
                            xml.getAttributeValue(getAttrPosition(xml, "viewportHeight"))
                                .toFloatOrNull()
                    }
                    "path" -> {
                        pathData = xml.getAttributeValue(getAttrPosition(xml, "pathData"))
                    }
                }

                event = xml.next()
            }
        }

        return ParsedVectorDrawable(
            width ?: return null,
            height ?: return null,
            viewportWidth ?: return null,
            viewportHeight ?: return null,
            pathData ?: return null
        )
    }

    private fun getAttrPosition(xml: XmlPullParser, attrName: String): Int =
        (0 until xml.attributeCount)
            .firstOrNull { i -> xml.getAttributeName(i) == attrName }
            ?: -1
}

class CustomShape(private val path: Path) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(AndroidPath(path))
    }
}
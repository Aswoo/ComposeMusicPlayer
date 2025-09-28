package com.sdu.composemusicplayer.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.utils.AndroidConstants

val Shapes =
    Shapes(
        small = RoundedCornerShape(AndroidConstants.Dp.SMALL.dp),
        medium = RoundedCornerShape(AndroidConstants.Dp.SMALL.dp),
        large = RoundedCornerShape(AndroidConstants.Dp.ZERO.dp),
    )

val roundedShape =
    object : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Outline {
            val p1 =
                Path().apply {
                    addOval(
                        Rect(
                            AndroidConstants.Dp.SMALL.toFloat(),
                            AndroidConstants.Misc.SHAPE_OFFSET_Y,
                            size.width - AndroidConstants.Dp.SMALL,
                            size.height - AndroidConstants.Dp.SMALL,
                        ),
                    )
                }
            val thickness = size.height / AndroidConstants.Misc.SHAPE_THICKNESS_DIVISOR
            val p2 =
                Path().apply {
                    addOval(
                        Rect(
                            thickness,
                            thickness,
                            size.width - thickness,
                            size.height - thickness,
                        ),
                    )
                }
            val p3 = Path()
            p3.op(p1, p2, PathOperation.Difference)

            return Outline.Generic(p3)
        }
    }

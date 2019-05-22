package com.hedvig.logged_in.profile.ui.referral

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.R

class DiscountView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    //colors
    private val pink = context.compatColor(R.color.pink)
    private val purple = context.compatColor(R.color.purple)
    private val darkPurple = context.compatColor(R.color.dark_purple)

    private val paint = Paint(ANTI_ALIAS_FLAG)

    private var centerX = -1
    private var centerY = -1

    // dimens
    private val tankWidth = 200
    private val tankPaddingTop = 0

    private val helpLineThickness = 4

    val factor = 60


    private val roofHeight = 40
    private val roofHeightHalf = roofHeight / 2

    private val sectionSpacing = 2

    //rect to recycle
    private val rect = Rect()
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        centerX = getCenterX()
        centerY = getCenterY()

        drawHelpLines(canvas)
        drawRoof(canvas)
    }

    private fun drawHelpLines(canvas: Canvas) {
        paint.color = pink

        // vertical
        canvas.drawLine((centerX - helpLineThickness).toFloat(), 0f, (centerX + helpLineThickness).toFloat(), height.toFloat(), paint)

        // horizontal
        canvas.drawLine(0f, (centerY - helpLineThickness).toFloat(), width.toFloat(), (centerY + helpLineThickness).toFloat(), paint)

        drawRightFace(canvas, 0, 30)
        drawRightFace(canvas, 1, 30)
        drawRightFace(canvas, 2, 30)
        drawRightFace(canvas, 3, 30)
    }

    private fun drawRoof(canvas: Canvas) {
        path.reset()

        paint.color = purple
        paint.style = Paint.Style.FILL

        path.moveTo(centerX, tankPaddingTop)
        path.lineTo(centerX + tankWidth, tankPaddingTop + roofHeightHalf)
        path.lineTo(centerX, tankPaddingTop + roofHeight)
        path.lineTo(centerX - tankWidth, tankPaddingTop + roofHeightHalf)
        path.close()

        canvas.drawPath(path, paint)
    }

    private fun drawRightFace(canvas: Canvas, index: Int, height: Int) {
        path.reset()

        paint.color = purple
        paint.style = Paint.Style.FILL

        path.moveTo(centerX - tankWidth, tankPaddingTop + roofHeightHalf + (index * height) + sectionSpacing)
        path.lineTo(centerX - sectionSpacing, tankPaddingTop + roofHeight + (index * height) + sectionSpacing)
        path.lineTo(centerX - sectionSpacing, tankPaddingTop + roofHeight + (index * height) + height)
        path.lineTo(centerX - tankWidth, tankPaddingTop + roofHeightHalf + (index * height) + height)
        path.close()

        canvas.drawPath(path, paint)
    }


    private fun getCenterX() = this.width / 2
    private fun getCenterY() = this.height / 2

}

// remove this
fun Path.moveTo(x: Int, y: Int) = this.moveTo(x.toFloat(), y.toFloat())

fun Path.lineTo(x: Int, y: Int) = this.lineTo(x.toFloat(), y.toFloat())

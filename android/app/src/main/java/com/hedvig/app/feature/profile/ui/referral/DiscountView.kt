package com.hedvig.app.feature.profile.ui.referral

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import com.hedvig.app.R
import com.hedvig.app.util.extensions.compatColor
import android.graphics.drawable.BitmapDrawable
import android.support.animation.FloatValueHolder
import android.support.animation.SpringAnimation
import android.support.v4.content.res.ResourcesCompat
import timber.log.Timber
import android.support.animation.SpringForce

class DiscountView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    private val premium = 100
    private val discountedPremium = 80
    private val step = 10
    private val segments = premium / step

    //colors
    private val pink = context.compatColor(R.color.pink)
    private val purple = context.compatColor(R.color.purple)
    private val darkPurple = context.compatColor(R.color.dark_purple)
    private val lightPurple = context.compatColor(R.color.referral_tank_lines)
    private val offBlackDark = context.compatColor(R.color.off_black_dark)
    private val white = context.compatColor(R.color.white)

    private val green = context.compatColor(R.color.green)

    private val paint = Paint(ANTI_ALIAS_FLAG)

    private var centerX = -1f
    private var centerY = -1f

    // dimens
    private val tankWidth = 400f
    private val tankWidthHalf = tankWidth / 2
    private var tankPaddingTop = 0f
    private var animationTopPadding = 0f

    private val labelsMarginFromTank = 50

    private val helpLineThickness = 4

    private val roofHeight = 100
    private val roofHeightHalf = roofHeight / 2

    private val sectionSpacing = 4
    private val sectionSpacingHalf = sectionSpacing / 2

    private val textLabelRadius = 20f

    private val textSizeLabelLeft = 44f
    private val textSizeLabelRight = 64f
    private val textPadding = 40f
    private val textLabelArrowSquareSize = 10


    private val springStartValue = 100f

    val spring = SpringForce(0f)
        .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
        .setStiffness(SpringForce.STIFFNESS_LOW)


    private val tankFloatValueHolder = FloatValueHolder(0f)
    private val tankSpringAnimation = SpringAnimation(tankFloatValueHolder).also {
        it.setMaxValue(springStartValue)
        it.setMinValue(0f)
        it.spring = spring
        it.setStartValue(springStartValue)
    }

    private val textFloatValueHolder = FloatValueHolder(0f)
    private val textSpringAnimation = SpringAnimation(textFloatValueHolder).also {
        it.setMaxValue(springStartValue)
        it.setMinValue(0f)
        it.spring = spring
        it.setStartValue(springStartValue)
    }

    private val text2FloatValueHolder = FloatValueHolder(0f)
    private val text2SpringAnimation = SpringAnimation(text2FloatValueHolder).also {
        it.setMaxValue(springStartValue)
        it.setMinValue(0f)
        it.spring = spring
        it.setStartValue(springStartValue)
    }

    private val text3FloatValueHolder = FloatValueHolder(0f)
    private val text3SpringAnimation = SpringAnimation(text3FloatValueHolder).also {
        it.setMaxValue(springStartValue)
        it.setMinValue(0f)
        it.spring = spring
        it.setStartValue(springStartValue)
    }

    val font by lazy {
        ResourcesCompat.getFont(context, R.font.circular_bold)
    }

    private var polkaDrawable: BitmapDrawable

    init {
        val polkaTile = BitmapFactory.decodeResource(context.resources, R.mipmap.polka_pattern_green_tile)
        polkaDrawable = BitmapDrawable(resources, polkaTile)
        polkaDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
    }

    //rect to recycle
    private val rect = Rect()
    private val rectF = RectF()
    private val path = Path()

    private var isFirstDraw = true

    init {
        setOnClickListener {
            postInvalidateOnAnimation()
            tankSpringAnimation.setStartValue(springStartValue)
            tankSpringAnimation.start()
            postDelayed({
                postInvalidateOnAnimation()
                textSpringAnimation.setStartValue(springStartValue)
                textSpringAnimation.start()
            }, 100)
            postDelayed({
                postInvalidateOnAnimation()
                text2SpringAnimation.setStartValue(springStartValue)
                text2SpringAnimation.start()
            }, 200)
            postDelayed({
                postInvalidateOnAnimation()
                text3SpringAnimation.setStartValue(springStartValue)
                text3SpringAnimation.start()
            }, 300)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val current = System.currentTimeMillis()
        hasDiscount = discountedSegments() != 0
        if (hasDiscount) {
            resetMask()
        }
        if (isFirstDraw) {
            centerX = getCenterX().toFloat()
            centerY = getCenterY().toFloat()
            polkaDrawable.setBounds(0, 0, tankWidth.toInt(), height)
        }

        val segmentHeight = (height - roofHeight - tankPaddingTop) / segments
        val animatedSegmentHeight = (height - roofHeight - tankPaddingTop - animationTopPadding) / segments

        if (tankSpringAnimation.isRunning) {
            val v = tankFloatValueHolder.value / springStartValue

            animationTopPadding = (height - roofHeight) * v

            for (i in segments - 1 downTo 0) {
                drawSegment(canvas, i, animatedSegmentHeight, isSegmentDiscounted(i))
            }
        } else {
            for (i in segments - 1 downTo 0) {
                drawSegment(canvas, i, segmentHeight, isSegmentDiscounted(i))
            }
        }

        if (tankSpringAnimation.isRunning || textSpringAnimation.isRunning) {
            val v = textFloatValueHolder.value / springStartValue
            if (textSpringAnimation.isRunning) {
                drawTextLabelLeft(canvas, "Gratis!", tankPaddingTop + roofHeightHalf + (segmentHeight * segments), v)
            }
        } else {
            drawTextLabelLeft(canvas, "Gratis!", tankPaddingTop + roofHeightHalf + (segmentHeight * segments))
        }

        if (tankSpringAnimation.isRunning || textSpringAnimation.isRunning || text2SpringAnimation.isRunning) {
            val v = text2FloatValueHolder.value / springStartValue
            if (text2SpringAnimation.isRunning) {
                drawTextLabelRight(canvas, segmentHeight, v)
            }
        } else {
            drawTextLabelRight(canvas, segmentHeight)
        }

        if (tankSpringAnimation.isRunning || textSpringAnimation.isRunning || text2SpringAnimation.isRunning || text3SpringAnimation.isRunning) {
            val v = text3FloatValueHolder.value / springStartValue
            if (text3SpringAnimation.isRunning) {
                drawTextLabelLeft(canvas, "$premium kr", tankPaddingTop + roofHeightHalf, v)
            }
        } else {
            drawTextLabelLeft(canvas, "$premium kr", tankPaddingTop + roofHeightHalf)
        }

        if (hasDiscount) {
            drawTiledFace(canvas)
        }

        if (tankSpringAnimation.isRunning || textSpringAnimation.isRunning || text2SpringAnimation.isRunning || text3SpringAnimation.isRunning) {
            postInvalidateOnAnimation()
        }
        isFirstDraw = false
        Timber.i("drawTime: ${System.currentTimeMillis() - current}")
    }

    private fun drawTextLabelLeft(canvas: Canvas, text: String, yPosition: Float, animationValue: Float = 0f) {
        paint.color = offBlackDark
        paint.style = Paint.Style.FILL
        paint.alpha = (255 - (animationValue * 255)).toInt()
        paint.typeface = font
        paint.textSize = textSizeLabelLeft
        paint.textAlign = Paint.Align.CENTER

        paint.getTextBounds(text, 0, text.length, rect)

        val textHeight = rect.height().toFloat()

        val labelWidth = paint.measureText(text) + textPadding
        val labelHeightHalf = (textHeight + textPadding) / 2
        rectF.set(
            (centerX - tankWidthHalf - labelsMarginFromTank - labelWidth),
            (yPosition - labelHeightHalf),
            centerX - tankWidthHalf - labelsMarginFromTank,
            (yPosition + labelHeightHalf)
        )

        canvas.drawRoundRect(rectF, textLabelRadius, textLabelRadius, paint)

        rect.set(
            rectF.right.toInt() - textLabelArrowSquareSize,
            rectF.centerY().toInt() - textLabelArrowSquareSize,
            rectF.right.toInt() + textLabelArrowSquareSize,
            rectF.centerY().toInt() + textLabelArrowSquareSize
        )

        canvas.save()
        canvas.rotate(45f, rect.exactCenterX(), rect.exactCenterY())
        canvas.drawRect(rect, paint)
        canvas.restore()

        paint.color = white
        canvas.drawText(text, rectF.centerX(), rectF.centerY() - ((paint.descent() + paint.ascent()) / 2), paint)
        paint.alpha = 255
    }

    private var hasDiscount = false

    private fun drawTextLabelRight(canvas: Canvas, segmentHeight: Float, animationValue: Float = 0f) {
        val text = if (hasDiscount) "-${premium - discountedPremium} kr" else "Bjud in!"

        paint.color = if (hasDiscount) green else purple
        paint.style = Paint.Style.FILL
        paint.alpha = (255 - (animationValue * 255)).toInt()
        paint.typeface = font
        paint.textSize = textSizeLabelRight
        paint.textAlign = Paint.Align.CENTER

        paint.getTextBounds(text, 0, text.length, rect)

        val yPosition = tankPaddingTop + roofHeightHalf + if (hasDiscount) (segmentHeight * discountedSegments()) / 2 else segmentHeight / 2
        val textHeight = rect.height().toFloat()

        val labelWidth = rect.width() + textPadding
        val labelHeightHalf = (textHeight + textPadding) / 2
        rectF.set(
            (centerX + tankWidthHalf + labelsMarginFromTank).toFloat(),
            (yPosition - labelHeightHalf),
            (centerX + tankWidthHalf + labelsMarginFromTank + labelWidth),
            (yPosition + labelHeightHalf)
        )

        canvas.drawRoundRect(rectF, textLabelRadius, textLabelRadius, paint)

        rect.set(
            rectF.left.toInt() - textLabelArrowSquareSize,
            rectF.centerY().toInt() - textLabelArrowSquareSize,
            rectF.left.toInt() + textLabelArrowSquareSize,
            rectF.centerY().toInt() + textLabelArrowSquareSize
        )

        canvas.save()
        canvas.rotate(45f, rect.exactCenterX(), rect.exactCenterY())
        canvas.drawRect(rect, paint)
        canvas.restore()

        paint.color = if (hasDiscount) offBlackDark else white
        canvas.drawText(text, rectF.centerX(), rectF.centerY() - ((paint.descent() + paint.ascent()) / 2), paint)
        paint.alpha = 255
    }

    private fun drawSegment(canvas: Canvas, index: Int, segmentHeight: Float, isDiscounted: Boolean) {
        drawLeftFace(canvas, index, segmentHeight, isDiscounted)
        drawRightFace(canvas, index, segmentHeight, isDiscounted)
        // only draw roof on last if not animating
        if (index == 0)
            drawRoof(canvas, index, segmentHeight, isDiscounted)
    }

    private fun drawHelpLines(canvas: Canvas) {
        paint.color = pink

        // vertical
        canvas.drawLine((centerX - helpLineThickness).toFloat(), 0f, (centerX + helpLineThickness).toFloat(), height.toFloat(), paint)

        // horizontal
        canvas.drawLine(0f, (centerY - helpLineThickness).toFloat(), width.toFloat(), (centerY + helpLineThickness).toFloat(), paint)

    }

    private fun drawRoof(canvas: Canvas, index: Int, segmentHeight: Float, isDiscounted: Boolean) {
        path.reset()

        paint.color = if (isDiscounted) green else purple
        paint.style = Paint.Style.FILL

        path.moveTo(centerX, tankPaddingTop + animationTopPadding + (index * segmentHeight))
        path.lineTo(centerX + tankWidthHalf, tankPaddingTop + animationTopPadding + roofHeightHalf + (index * segmentHeight))
        path.lineTo(centerX, tankPaddingTop + animationTopPadding + roofHeight + (index * segmentHeight))
        path.lineTo(centerX - tankWidthHalf, tankPaddingTop + animationTopPadding + roofHeightHalf + (index * segmentHeight))
        path.close()

        canvas.drawPath(path, paint)
    }

    private fun drawLeftFace(canvas: Canvas, index: Int, segmentHeight: Float, isDiscounted: Boolean) {
        path.reset()

        val sectionLeft = if (isDiscounted) 0f else centerX - tankWidthHalf
        val sectionRight = if (isDiscounted) tankWidthHalf else centerX

        val sectionTop = tankPaddingTop + animationTopPadding + (index * segmentHeight)
        val sectionBottom = sectionTop + segmentHeight

        path.moveTo(sectionLeft, sectionTop + roofHeightHalf)
        path.lineTo(sectionRight, sectionTop + roofHeight)
        path.lineTo(sectionRight, sectionBottom + roofHeight)
        path.lineTo(sectionLeft, sectionBottom + roofHeightHalf)
        path.close()

        paint.style = Paint.Style.FILL
        if (isDiscounted) {
            addPathToMask(path)
            drawVerticalGreenOutline(
                canvas,
                centerX - tankWidthHalf + sectionSpacingHalf,
                sectionTop + roofHeightHalf,
                sectionBottom + roofHeightHalf
            )
        } else {
            paint.color = purple
            canvas.drawPath(path, paint)
            paint.color = lightPurple
        }
        setUpPaintForLine()
        if (!isDiscounted || isLastDiscountedSegment(index)) {
            canvas.drawLine(
                centerX - tankWidthHalf,
                sectionBottom + roofHeightHalf + sectionSpacingHalf,
                centerX,
                sectionBottom + roofHeight + sectionSpacingHalf,
                paint)
        }
        if (index == 0) {
            canvas.drawLine(
                centerX - tankWidthHalf,
                sectionTop + roofHeightHalf + sectionSpacingHalf,
                centerX,
                sectionTop + roofHeight + sectionSpacingHalf,
                paint
            )
        }
    }

    private fun drawRightFace(canvas: Canvas, index: Int, segmentHeight: Float, isDiscounted: Boolean) {
        path.reset()

        val sectionLeft = if (isDiscounted) tankWidthHalf else centerX
        val sectionRight = if (isDiscounted) tankWidth else centerX + tankWidthHalf

        val sectionTop = tankPaddingTop + animationTopPadding + (index * segmentHeight)
        val sectionBottom = sectionTop + segmentHeight

        path.moveTo(sectionLeft, sectionTop + roofHeight)
        path.lineTo(sectionRight, sectionTop + roofHeightHalf)
        path.lineTo(sectionRight, sectionBottom + roofHeightHalf)
        path.lineTo(sectionLeft, sectionBottom + roofHeight)
        path.close()

        paint.style = Paint.Style.FILL
        if (isDiscounted) {
            addPathToMask(path)
            setUpPaintForLine()
            drawVerticalGreenOutline(
                canvas,
                centerX + tankWidthHalf - sectionSpacingHalf,
                sectionTop + roofHeightHalf,
                sectionBottom + roofHeightHalf
            )
        } else {
            paint.color = darkPurple
            canvas.drawPath(path, paint)
            paint.color = lightPurple
        }
        setUpPaintForLine()
        if (!isDiscounted || isLastDiscountedSegment(index)) {
            canvas.drawLine(
                centerX,
                sectionBottom + roofHeight + sectionSpacingHalf,
                centerX + tankWidthHalf,
                sectionBottom + roofHeightHalf + sectionSpacingHalf,
                paint
            )
        }
        if (index == 0) {
            canvas.drawLine(
                centerX,
                sectionTop + roofHeight + sectionSpacingHalf,
                centerX + tankWidthHalf,
                sectionTop + roofHeightHalf + sectionSpacingHalf,
                paint
            )
        }
    }

    private fun drawVerticalGreenOutline(canvas: Canvas, xPosition: Float, yTop: Float, yBottom: Float) {
        setUpPaintForLine()
        paint.color = green
        canvas.drawLine(
            xPosition,
            yTop,
            xPosition,
            yBottom,
            paint
        )
    }

    lateinit var tileResult: Bitmap
    lateinit var tileCanvas: Canvas

    lateinit var maskBitmap: Bitmap
    lateinit var maskCanvas: Canvas

    private fun resetMask() {
        tileResult = Bitmap.createBitmap(tankWidth.toInt(), height, Bitmap.Config.ARGB_8888)
        tileCanvas = Canvas(tileResult)

        maskBitmap = Bitmap.createBitmap(tankWidth.toInt(), height, Bitmap.Config.ARGB_8888)
        maskCanvas = Canvas(maskBitmap)
    }

    private fun addPathToMask(path: Path) {
        paint.style = Paint.Style.FILL
        paint.color = green
        maskCanvas.drawPath(path, paint)
    }

    private fun drawTiledFace(canvas: Canvas) {
        polkaDrawable.draw(tileCanvas)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        tileCanvas.drawBitmap(maskBitmap, 0f, 0f, paint)
        paint.xfermode = null
        canvas.drawBitmap(tileResult, (centerX - tankWidthHalf).toFloat(), 0f, paint)
    }

    private fun isSegmentDiscounted(index: Int) = (premium - (index * step)) > discountedPremium
    private fun isLastDiscountedSegment(index: Int) = isSegmentDiscounted(index) && (premium - (index * step)) - step <= discountedPremium
    private fun discountedSegments() = (premium - discountedPremium) / step

    private fun setUpPaintForLine() {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = sectionSpacing.toFloat()
    }

    private fun getCenterX() = this.width / 2
    private fun getCenterY() = this.height / 2
}

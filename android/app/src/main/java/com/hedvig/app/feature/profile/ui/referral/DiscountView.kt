package com.hedvig.app.feature.profile.ui.referral

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import com.hedvig.app.R
import com.hedvig.app.util.extensions.compatColor
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat

class DiscountView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    private val premium = 100
    private val discountedPremium = 100
    private val step = 10
    private val segments = premium / step

    private val isAnimating = false

    //colors
    private val pink = context.compatColor(R.color.pink)
    private val purple = context.compatColor(R.color.purple)
    private val darkPurple = context.compatColor(R.color.dark_purple)
    private val lightPurple = context.compatColor(R.color.referral_tank_lines)
    private val offBlackDark = context.compatColor(R.color.off_black_dark)
    private val white = context.compatColor(R.color.white)

    private val green = context.compatColor(R.color.green)

    private val paint = Paint(ANTI_ALIAS_FLAG)

    private var centerX = -1
    private var centerY = -1

    // dimens
    private val tankWidth = 200
    private val tankPaddingTop = 0

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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        polkaDrawable.setBounds(0, 0, width, height) // Todo: should be smaller
        centerX = getCenterX()
        centerY = getCenterY()

        val segmentHeight = (height - roofHeight - tankPaddingTop) / segments

        for (i in segments - 1 downTo 0) {
            drawSegment(canvas, i, segmentHeight, isSegmentDiscounted(i))
        }

        drawTextLabelLeft(canvas, "$premium kr", tankPaddingTop + roofHeightHalf)
        drawTextLabelLeft(canvas, "Gratis!", tankPaddingTop + roofHeightHalf + (segmentHeight * segments))
        drawTextLabelRight(canvas, segmentHeight)
    }

    private fun drawTextLabelLeft(canvas: Canvas, text: String, yPosition: Int) {
        paint.color = offBlackDark
        paint.style = Paint.Style.FILL
        paint.typeface = font
        paint.textSize = textSizeLabelLeft
        paint.textAlign = Paint.Align.CENTER


        paint.getTextBounds(text, 0, text.length, rect)

        val textHeight = rect.height().toFloat()

        val labelWidth = paint.measureText(text) + textPadding
        val labelHeightHalf = (textHeight + textPadding) / 2
        rectF.set(
            (centerX - tankWidth - labelsMarginFromTank - labelWidth),
            (yPosition - labelHeightHalf),
            (centerX - tankWidth - labelsMarginFromTank).toFloat(),
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
    }

    private fun drawTextLabelRight(canvas: Canvas, segmentHeight: Int) {
        val hasDiscount = discountedSegments() != 0
        val text = if(hasDiscount) "-${premium - discountedSegments()} kr" else "Bjud in!"

        paint.color = if (hasDiscount) green else purple
        paint.style = Paint.Style.FILL
        paint.typeface = font
        paint.textSize = textSizeLabelRight
        paint.textAlign = Paint.Align.CENTER

        paint.getTextBounds(text, 0, text.length, rect)

        val yPosition = tankPaddingTop + roofHeightHalf + if (hasDiscount) (segmentHeight * discountedSegments()) / 2 else  segmentHeight / 2
        val textHeight = rect.height().toFloat()

        val labelWidth = rect.width() + textPadding
        val labelHeightHalf = (textHeight + textPadding) / 2
        rectF.set(
            (centerX + tankWidth + labelsMarginFromTank).toFloat(),
            (yPosition - labelHeightHalf),
            (centerX + tankWidth + labelsMarginFromTank + labelWidth),
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
    }

    private fun drawSegment(canvas: Canvas, index: Int, segmentHeight: Int, isDiscounted: Boolean) {
        drawLeftFace(canvas, index, segmentHeight, isDiscounted)
        drawRightFace(canvas, index, segmentHeight, isDiscounted)
        // only draw roof on last if not animating
        if (isAnimating || index == 0)
            drawRoof(canvas, index, segmentHeight, isDiscounted)
    }

    private fun drawHelpLines(canvas: Canvas) {
        paint.color = pink

        // vertical
        canvas.drawLine((centerX - helpLineThickness).toFloat(), 0f, (centerX + helpLineThickness).toFloat(), height.toFloat(), paint)

        // horizontal
        canvas.drawLine(0f, (centerY - helpLineThickness).toFloat(), width.toFloat(), (centerY + helpLineThickness).toFloat(), paint)

    }

    private fun drawRoof(canvas: Canvas, index: Int, segmentHeight: Int, isDiscounted: Boolean) {
        path.reset()

        paint.color = if (isDiscounted) green else purple
        paint.style = Paint.Style.FILL

        path.moveTo(centerX, tankPaddingTop + (index * segmentHeight))
        path.lineTo(centerX + tankWidth, tankPaddingTop + roofHeightHalf + (index * segmentHeight))
        path.lineTo(centerX, tankPaddingTop + roofHeight + (index * segmentHeight))
        path.lineTo(centerX - tankWidth, tankPaddingTop + roofHeightHalf + (index * segmentHeight))
        path.close()

        canvas.drawPath(path, paint)
    }

    private fun drawLeftFace(canvas: Canvas, index: Int, segmentHeight: Int, isDiscounted: Boolean) {
        path.reset()

        val sectionLeft = centerX - tankWidth
        val sectionRight = centerX

        val sectionTop = tankPaddingTop + (index * segmentHeight)
        val sectionBottom = sectionTop + segmentHeight

        path.moveTo(sectionLeft, sectionTop + roofHeightHalf)
        path.lineTo(sectionRight, sectionTop + roofHeight)
        path.lineTo(sectionRight, sectionBottom + roofHeight)
        path.lineTo(sectionLeft, sectionBottom + roofHeightHalf)
        path.close()

        paint.style = Paint.Style.FILL
        if (isDiscounted) {
            drawTiledFace(canvas, path)
            drawVerticalGreenOutline(
                canvas,
                sectionLeft + sectionSpacingHalf,
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
                sectionLeft,
                sectionBottom + roofHeightHalf + sectionSpacingHalf,
                sectionRight,
                sectionBottom + roofHeight + sectionSpacingHalf,
                paint)
        }
        if (index == 0) {
            canvas.drawLine(
                sectionLeft,
                sectionTop + roofHeightHalf + sectionSpacingHalf,
                sectionRight,
                sectionTop + roofHeight + sectionSpacingHalf,
                paint
            )
        }
    }


    private fun drawRightFace(canvas: Canvas, index: Int, segmentHeight: Int, isDiscounted: Boolean) {
        path.reset()

        val sectionLeft = centerX
        val sectionRight = centerX + tankWidth

        val sectionTop = tankPaddingTop + (index * segmentHeight)
        val sectionBottom = sectionTop + segmentHeight

        path.moveTo(sectionLeft, sectionTop + roofHeight)
        path.lineTo(sectionRight, sectionTop + roofHeightHalf)
        path.lineTo(sectionRight, sectionBottom + roofHeightHalf)
        path.lineTo(sectionLeft, sectionBottom + roofHeight)
        path.close()

        paint.style = Paint.Style.FILL
        if (isDiscounted) {
            drawTiledFace(canvas, path)
            setUpPaintForLine()
            drawVerticalGreenOutline(
                canvas,
                sectionRight - sectionSpacingHalf,
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
                sectionLeft,
                sectionBottom + roofHeight + sectionSpacingHalf,
                sectionRight,
                sectionBottom + roofHeightHalf + sectionSpacingHalf,
                paint
            )
        }
        if (index == 0) {
            canvas.drawLine(
                sectionLeft,
                sectionTop + roofHeight + sectionSpacingHalf,
                sectionRight,
                sectionTop + roofHeightHalf + sectionSpacingHalf,
                paint
            )
        }
    }

    private fun drawVerticalGreenOutline(canvas: Canvas, xPosition: Int, yTop: Int, yBottom: Int) {
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

    private fun drawTiledFace(canvas: Canvas, path: Path) {
        val mask = getMask(path)

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(result)
        polkaDrawable.draw(tempCanvas)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        tempCanvas.drawBitmap(mask, 0f, 0f, paint)
        paint.xfermode = null
        canvas.drawBitmap(result, 0f, 0f, paint)
    }

    private fun getMask(path: Path): Bitmap {
        val mask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas1 = Canvas(mask)
        tempCanvas1.drawPath(path, paint)
        return mask
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

// remove this
fun Path.moveTo(x: Int, y: Int) = this.moveTo(x.toFloat(), y.toFloat())

fun Path.lineTo(x: Int, y: Int) = this.lineTo(x.toFloat(), y.toFloat())

fun Canvas.drawLine(startX: Int, startY: Int, stopX: Int, stopY: Int, paint: Paint) = this.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), paint)

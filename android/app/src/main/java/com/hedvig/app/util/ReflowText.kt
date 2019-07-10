package com.hedvig.app.util

/*
 *   Copyright 2018 Google LLC
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.ColorInt
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.transition.Transition
import android.transition.TransitionValues
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.hedvig.app.R

import java.util.ArrayList

/**
 * A transition for repositioning text. This will animate changes in text size and position,
 * re-flowing line breaks as necessary.
 *
 *
 * Strongly recommended to use a curved `pathMotion` for a more natural transition.
 */
class ReflowText(context: Context, attrs: AttributeSet) : Transition(context, attrs) {

    private var velocity = 700         // pixels per second
    private var minDuration: Long = 200     // ms
    private var maxDuration: Long = 400     // ms
    private var staggerDelay: Long = 40     // ms
    private var _duration: Long = 0
    // this is hack for preventing view from drawing briefly at the end of the transition :(
    private val freezeFrame: Boolean

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ReflowText)
        velocity = a.getDimensionPixelSize(R.styleable.ReflowText_velocity, velocity)
        minDuration = a.getInt(R.styleable.ReflowText_minDuration, minDuration.toInt()).toLong()
        maxDuration = a.getInt(R.styleable.ReflowText_maxDuration, maxDuration.toInt()).toLong()
        staggerDelay = a.getInt(R.styleable.ReflowText_staggerDelay, staggerDelay.toInt()).toLong()
        freezeFrame = a.getBoolean(R.styleable.ReflowText_freezeFrame, false)
        a.recycle()
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun getTransitionProperties(): Array<String> {
        return PROPERTIES
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {

        if (startValues == null || endValues == null) return null

        val view = endValues.view
        val transition = AnimatorSet()
        val startData = startValues!!.values[PROPNAME_DATA] as ReflowData?
        val endData = endValues!!.values[PROPNAME_DATA] as ReflowData?
        _duration = calculateDuration(startData!!.bounds, endData!!.bounds)

        // create layouts & capture a bitmaps of the text in both states
        // (with max lines variants where needed)
        val startLayout = createLayout(startData, sceneRoot.context, false)
        val endLayout = createLayout(endData, sceneRoot.context, false)
        var startLayoutMaxLines: Layout? = null
        var endLayoutMaxLines: Layout? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // StaticLayout maxLines support
            if (startData.maxLines != -1) {
                startLayoutMaxLines = createLayout(startData, sceneRoot.context, true)
            }
            if (endData.maxLines != -1) {
                endLayoutMaxLines = createLayout(endData, sceneRoot.context, true)
            }
        }
        val startText = createBitmap(
            startData,
            startLayoutMaxLines ?: startLayout
        )
        val endText = createBitmap(
            endData,
            endLayoutMaxLines ?: endLayout
        )

        // temporarily turn off clipping so we can draw outside of our bounds don't draw
        view.setWillNotDraw(true)
        (view.parent as ViewGroup).clipChildren = false

        // calculate the runs of text to move together
        val runs = getRuns(
            startData, startLayout, startLayoutMaxLines,
            endData, endLayout, endLayoutMaxLines
        )

        // create animators for moving, scaling and fading each run of text
        transition.playTogether(
            createRunAnimators(view, startData, endData, startText, endText, runs)
        )

        if (!freezeFrame) {
            transition.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // clean up
                    view.setWillNotDraw(false)
                    view.overlay.clear()
                    (view.parent as ViewGroup).clipChildren = true
                    startText.recycle()
                    endText.recycle()
                }
            })
        }
        return transition
    }

    override fun setDuration(duration: Long): Transition {
        /* don't call super as we want to handle duration ourselves */
        return this
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val reflowData = getReflowData(transitionValues.view)
        transitionValues.values[PROPNAME_DATA] = reflowData
        if (reflowData != null) {
            // add these props to the map separately (even though they are captured in the reflow
            // data) to use only them to determine whether to create an animation i.e. only
            // animate if text size or bounds have changed (see #getTransitionProperties())
            transitionValues.values[PROPNAME_TEXT_SIZE] = reflowData.textSize
            transitionValues.values[PROPNAME_BOUNDS] = reflowData.bounds
        }
    }

    private fun getReflowData(view: View): ReflowData? {
        val reflowData = view.getTag(R.id.tag_reflow_data) as ReflowData?
        if (reflowData != null) {
            view.setTag(R.id.tag_reflow_data, null)
            return reflowData
        }
        return null
    }

    /**
     * Calculate the [Run]s i.e. diff the start and end states, see where text changes
     * line and track the bounds of sections of text that can move together.
     *
     *
     * If a text block has a max number of lines, consider both with and without this limit applied.
     * This allows simulating the correct line breaking as well as calculating the position that
     * overflowing text would have been laid out, so that it can animate from/to that position.
     */
    private fun getRuns(
        startData: ReflowData, startLayout: Layout,
        startLayoutMaxLines: Layout?, endData: ReflowData,
        endLayout: Layout, endLayoutMaxLines: Layout?
    ): List<Run> {
        val textLength = endLayout.text.length
        var currentStartLine = 0
        var currentStartRunLeft = 0
        var currentStartRunTop = 0
        var currentEndLine = 0
        var currentEndRunLeft = 0
        var currentEndRunTop = 0
        val runs = ArrayList<Run>(endLayout.lineCount)

        for (i in 0 until textLength) {
            // work out which line this letter is on in the start state
            var startLine = -1
            var startMax = false
            var startMaxEllipsis = false
            if (startLayoutMaxLines != null) {
                val letter = startLayoutMaxLines!!.text[i]
                startMaxEllipsis = letter == '…'
                if (letter != '\uFEFF'              // beyond max lines
                    && !startMaxEllipsis
                ) {     // ellipsize inserted into layout
                    startLine = startLayoutMaxLines.getLineForOffset(i)
                    startMax = true
                }
            }
            if (!startMax) {
                startLine = startLayout.getLineForOffset(i)
            }

            // work out which line this letter is on in the end state
            var endLine = -1
            var endMax = false
            var endMaxEllipsis = false
            if (endLayoutMaxLines != null) {
                val letter = endLayoutMaxLines!!.text[i]
                endMaxEllipsis = letter == '…'
                if (letter != '\uFEFF'              // beyond max lines
                    && !endMaxEllipsis
                ) {       // ellipsize inserted into layout
                    endLine = endLayoutMaxLines.getLineForOffset(i)
                    endMax = true
                }
            }
            if (!endMax) {
                endLine = endLayout.getLineForOffset(i)
            }
            val lastChar = i == textLength - 1

            if (startLine != currentStartLine
                || endLine != currentEndLine
                || lastChar
            ) {
                // at a run boundary, store bounds in both states
                val startRunRight = getRunRight(
                    startLayout, startLayoutMaxLines,
                    currentStartLine, i, startLine, startMax, startMaxEllipsis, lastChar
                )
                val startRunBottom = startLayout.getLineBottom(currentStartLine)
                val endRunRight = getRunRight(
                    endLayout, endLayoutMaxLines, currentEndLine, i,
                    endLine, endMax, endMaxEllipsis, lastChar
                )
                val endRunBottom = endLayout.getLineBottom(currentEndLine)

                val startBound = Rect(
                    currentStartRunLeft, currentStartRunTop, startRunRight, startRunBottom
                )
                startBound.offset(startData.textPosition.x, startData.textPosition.y)
                val endBound = Rect(
                    currentEndRunLeft, currentEndRunTop, endRunRight, endRunBottom
                )
                endBound.offset(endData.textPosition.x, endData.textPosition.y)
                runs.add(
                    Run(
                        startBound,
                        startMax || startRunBottom <= startData.textHeight,
                        endBound,
                        endMax || endRunBottom <= endData.textHeight
                    )
                )
                currentStartLine = startLine
                currentStartRunLeft = (if (startMax)
                    startLayoutMaxLines!!
                        .getPrimaryHorizontal(i)
                else
                    startLayout.getPrimaryHorizontal(i)).toInt()
                currentStartRunTop = startLayout.getLineTop(startLine)
                currentEndLine = endLine
                currentEndRunLeft = (if (endMax)
                    endLayoutMaxLines!!
                        .getPrimaryHorizontal(i)
                else
                    endLayout.getPrimaryHorizontal(i)).toInt()
                currentEndRunTop = endLayout.getLineTop(endLine)
            }
        }
        return runs
    }

    /**
     * Calculate the right boundary for this run (harder than it sounds). As we're a letter ahead,
     * need to grab either current letter start or the end of the previous line. Also need to
     * consider maxLines case, which inserts ellipses at the overflow point – don't include these.
     */
    private fun getRunRight(
        unrestrictedLayout: Layout, maxLinesLayout: Layout?, currentLine: Int, index: Int,
        line: Int, withinMax: Boolean, isMaxEllipsis: Boolean, isLastChar: Boolean
    ): Int {
        val runRight: Int
        if (line != currentLine || isLastChar) {
            if (isMaxEllipsis) {
                runRight = maxLinesLayout!!.getPrimaryHorizontal(index).toInt()
            } else {
                runRight = unrestrictedLayout.getLineMax(currentLine).toInt()
            }
        } else {
            if (withinMax) {
                runRight = maxLinesLayout!!.getPrimaryHorizontal(index).toInt()
            } else {
                runRight = unrestrictedLayout.getPrimaryHorizontal(index).toInt()
            }
        }
        return runRight
    }

    /**
     * Create Animators to transition each run of text from start to end position and size.
     */
    private fun createRunAnimators(
        view: View,
        startData: ReflowData,
        endData: ReflowData,
        startText: Bitmap,
        endText: Bitmap,
        runs: List<Run>
    ): List<Animator> {
        val animators = ArrayList<Animator>(runs.size)
        val dx = startData.bounds.left - endData.bounds.left
        val dy = startData.bounds.top - endData.bounds.top
        var startDelay = 0L
        // move text closest to the destination first i.e. loop forward or backward over the runs
        val upward = startData.bounds.centerY() > endData.bounds.centerY()
        var first = true
        var lastRightward = true
        val linearInterpolator = LinearInterpolator()

        var i = if (upward) 0 else runs.size - 1
        while (upward && i < runs.size || !upward && i >= 0) {
            val run = runs[i]

            // skip text runs which aren't visible in either state
            if (!run.startVisible && !run.endVisible) {
                i += if (upward) 1 else -1
                continue
            }

            // create & position the drawable which displays the run; add it to the overlay.
            val drawable = SwitchDrawable(
                startText, run.start, startData.textSize,
                endText, run.end, endData.textSize
            )
            drawable.setBounds(
                run.start.left + dx,
                run.start.top + dy,
                run.start.right + dx,
                run.start.bottom + dy
            )
            view.overlay.add(drawable)

            val topLeft = PropertyValuesHolder.ofObject<PointF>(
                SwitchDrawable.TOP_LEFT, null,
                pathMotion.getPath(
                    (run.start.left + dx).toFloat(),
                    (run.start.top + dy).toFloat(),
                    run.end.left.toFloat(),
                    run.end.top.toFloat()
                )
            )
            val width = PropertyValuesHolder.ofInt(
                SwitchDrawable.WIDTH, run.start.width(), run.end.width()
            )
            val height = PropertyValuesHolder.ofInt(
                SwitchDrawable.HEIGHT, run.start.height(), run.end.height()
            )
            // the progress property drives the switching behaviour
            val progress = PropertyValuesHolder.ofFloat(
                SwitchDrawable.PROGRESS, 0f, 1f
            )
            val runAnim = ObjectAnimator.ofPropertyValuesHolder(
                drawable, topLeft, width, height, progress
            )

            val rightward = run.start.centerX() + dx < run.end.centerX()
            if (run.startVisible && run.endVisible
                && !first && rightward != lastRightward
            ) {
                // increase the start delay (by a decreasing amount) for the next run
                // (if it's visible throughout) to stagger the movement and try to minimize overlaps
                startDelay += staggerDelay
                staggerDelay *= STAGGER_DECAY.toLong()
            }
            lastRightward = rightward
            first = false

            runAnim.startDelay = startDelay
            val animDuration = Math.max(minDuration, _duration - startDelay / 2)
            runAnim.duration = animDuration
            animators.add(runAnim)

            if (run.startVisible != run.endVisible) {
                // if run is appearing/disappearing then fade it in/out
                val fade = ObjectAnimator.ofInt(
                    drawable,
                    SwitchDrawable.ALPHA,
                    if (run.startVisible) OPAQUE else TRANSPARENT,
                    if (run.endVisible) OPAQUE else TRANSPARENT
                )
                fade.duration = (_duration + startDelay) / 2
                if (!run.startVisible) {
                    drawable.alpha = TRANSPARENT
                    fade.startDelay = (_duration + startDelay) / 2
                } else {
                    fade.startDelay = startDelay
                }
                animators.add(fade)
            } else {
                // slightly fade during transition to minimize movement
                val fade = ObjectAnimator.ofInt(
                    drawable,
                    SwitchDrawable.ALPHA,
                    OPAQUE, OPACITY_MID_TRANSITION, OPAQUE
                )
                fade.startDelay = startDelay
                fade.duration = _duration + startDelay
                fade.interpolator = linearInterpolator
                animators.add(fade)
            }
            i += if (upward) 1 else -1
        }
        return animators
    }

    private fun createLayout(data: ReflowData, context: Context, enforceMaxLines: Boolean): Layout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = data.textSize
        paint.color = data.textColor
        paint.letterSpacing = data.letterSpacing

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val builder = StaticLayout.Builder.obtain(
                data.text!!, 0, data.text.length, paint, data.textWidth
            )
                .setLineSpacing(data.lineSpacingAdd, data.lineSpacingMult)
                .setBreakStrategy(data.breakStrategy)
            if (enforceMaxLines && data.maxLines != -1) {
                builder.setMaxLines(data.maxLines)
                builder.setEllipsize(TextUtils.TruncateAt.END)
            }
            return builder.build()
        } else {
            return StaticLayout(
                data.text,
                paint,
                data.textWidth,
                Layout.Alignment.ALIGN_NORMAL,
                data.lineSpacingMult,
                data.lineSpacingAdd,
                true
            )
        }
    }

    private fun createBitmap(data: ReflowData, layout: Layout): Bitmap {
        val bitmap = Bitmap.createBitmap(
            data.bounds.width(), data.bounds.height(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.translate(data.textPosition.x.toFloat(), data.textPosition.y.toFloat())
        layout.draw(canvas)
        return bitmap
    }

    /**
     * Calculate the duration for the transition depending upon how far the text has to move.
     */
    private fun calculateDuration(startPosition: Rect, endPosition: Rect): Long {
        val distance = Math.hypot(
            (startPosition.exactCenterX() - endPosition.exactCenterX()).toDouble(),
            (startPosition.exactCenterY() - endPosition.exactCenterY()).toDouble()
        ).toFloat()
        val duration = (1000 * (distance / velocity)).toLong()
        return Math.max(minDuration, Math.min(maxDuration, duration))
    }

    /**
     * Holds all data needed to describe a block of text i.e. to be able to re-create the
     * [Layout].
     */
    private class ReflowData : Parcelable {

        internal val text: String?
        internal val textSize: Float
        @ColorInt
        internal val textColor: Int
        internal val bounds: Rect
        internal val lineSpacingAdd: Float
        internal val lineSpacingMult: Float
        internal val textPosition: Point
        internal val textHeight: Int
        internal val textWidth: Int
        internal val breakStrategy: Int
        internal val letterSpacing: Float
        internal val maxLines: Int

        internal constructor(reflowable: Reflowable<*>) {
            text = reflowable.text
            textSize = reflowable.textSize
            textColor = reflowable.textColor
            val view = reflowable.view
            val loc = IntArray(2)
            view.getLocationInWindow(loc)
            bounds = Rect(loc[0], loc[1], loc[0] + view.width, loc[1] + view.height)
            textPosition = reflowable.textPosition
            textHeight = reflowable.textHeight
            lineSpacingAdd = reflowable.lineSpacingAdd
            lineSpacingMult = reflowable.lineSpacingMult
            textWidth = reflowable.textWidth
            breakStrategy = reflowable.breakStrategy
            letterSpacing = reflowable.letterSpacing
            maxLines = reflowable.maxLines
        }

        internal constructor(`in`: Parcel) {
            text = `in`.readString()
            textSize = `in`.readFloat()
            textColor = `in`.readInt()
            bounds = `in`.readValue(Rect::class.java.classLoader) as Rect
            lineSpacingAdd = `in`.readFloat()
            lineSpacingMult = `in`.readFloat()
            textPosition = `in`.readValue(Point::class.java.classLoader) as Point
            textHeight = `in`.readInt()
            textWidth = `in`.readInt()
            breakStrategy = `in`.readInt()
            letterSpacing = `in`.readFloat()
            maxLines = `in`.readInt()
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(text)
            dest.writeFloat(textSize)
            dest.writeInt(textColor)
            dest.writeValue(bounds)
            dest.writeFloat(lineSpacingAdd)
            dest.writeFloat(lineSpacingMult)
            dest.writeValue(textPosition)
            dest.writeInt(textHeight)
            dest.writeInt(textWidth)
            dest.writeInt(breakStrategy)
            dest.writeFloat(letterSpacing)
            dest.writeInt(maxLines)
        }

        companion object {

            @JvmField
            val CREATOR: Parcelable.Creator<ReflowData> = object : Parcelable.Creator<ReflowData> {
                override fun createFromParcel(`in`: Parcel): ReflowData {
                    return ReflowData(`in`)
                }

                override fun newArray(size: Int): Array<ReflowData?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    /**
     * Models the location of a run of text in both start and end states.
     */
    private class Run internal constructor(
        internal val start: Rect,
        internal val startVisible: Boolean,
        internal val end: Rect,
        internal val endVisible: Boolean
    )

    /**
     * A drawable which shows (a portion of) one of two given bitmaps, switching between them once
     * a progress property passes a threshold.
     *
     *
     * This is helpful when animating text size change as small text scaled up is blurry but larger
     * text scaled down has different kerning. Instead we use images of both states and switch
     * during the transition. We use images as animating text size thrashes the font cache.
     */
    private class SwitchDrawable internal constructor(
        private var currentBitmap: Bitmap?,
        private var currentBitmapSrcBounds: Rect?,
        startFontSize: Float,
        private val endBitmap: Bitmap,
        private val endBitmapSrcBounds: Rect,
        endFontSize: Float
    ) : Drawable() {

        private val paint: Paint
        private val switchThreshold: Float
        private var hasSwitched = false
        internal var topLeft: PointF? = null
            set(topLeft) {
                field = topLeft
                updateBounds()
            }
        internal var width: Int = 0
            set(width) {
                field = width
                updateBounds()
            }
        internal var height: Int = 0
            set(height) {
                field = height
                updateBounds()
            }

        init {
            switchThreshold = startFontSize / (startFontSize + endFontSize)
            paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
        }

        override fun draw(canvas: Canvas) {
            canvas.drawBitmap(currentBitmap!!, currentBitmapSrcBounds, bounds, paint)
        }

        override fun getAlpha(): Int {
            return paint.alpha
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getColorFilter(): ColorFilter? {
            return paint.colorFilter
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }

        internal fun setProgress(progress: Float) {
            if (!hasSwitched && progress >= switchThreshold) {
                currentBitmap = endBitmap
                currentBitmapSrcBounds = endBitmapSrcBounds
                hasSwitched = true
            }
        }

        private fun updateBounds() {
            val left = Math.round(this.topLeft!!.x)
            val top = Math.round(this.topLeft!!.y)
            setBounds(left, top, left + this.width, top + this.height)
        }

        companion object {

            internal val TOP_LEFT: Property<SwitchDrawable, PointF> =
                object : Property<SwitchDrawable, PointF>(PointF::class.java, "topLeft") {
                    override fun set(drawable: SwitchDrawable, topLeft: PointF) {
                        drawable.topLeft = topLeft
                    }

                    override fun get(drawable: SwitchDrawable): PointF? {
                        return drawable.topLeft
                    }
                }

            internal val WIDTH: Property<SwitchDrawable, Int> =
                object : Property<SwitchDrawable, Int>(Int::class.java, "width") {
                    override fun set(drawable: SwitchDrawable, width: Int) {
                        drawable.width = width
                    }

                    override fun get(drawable: SwitchDrawable): Int {
                        return drawable.width
                    }
                }

            internal val HEIGHT: Property<SwitchDrawable, Int> =
                object : Property<SwitchDrawable, Int>(Int::class.java, "height") {
                    override fun set(drawable: SwitchDrawable, height: Int) {
                        drawable.height = height
                    }

                    override fun get(drawable: SwitchDrawable): Int {
                        return drawable.height
                    }
                }

            internal val ALPHA: Property<SwitchDrawable, Int> =
                object : Property<SwitchDrawable, Int>(Int::class.java, "alpha") {
                    override fun set(drawable: SwitchDrawable, alpha: Int?) {
                        drawable.alpha = alpha!!
                    }

                    override fun get(drawable: SwitchDrawable): Int {
                        return drawable.alpha
                    }
                }

            internal val PROGRESS: Property<SwitchDrawable, Float> =
                object : Property<SwitchDrawable, Float>(Float::class.java, "progress") {
                    override fun set(drawable: SwitchDrawable, progress: Float?) {
                        drawable.setProgress(progress!!)
                    }

                    override fun get(drawable: SwitchDrawable): Float {
                        return 0f
                    }
                }
        }
    }

    /**
     * Interface describing a view which supports re-flowing i.e. it exposes enough information to
     * construct a [ReflowData] object;
     */
    interface Reflowable<T : View> {

        val view: T
        val text: String
        val textPosition: Point
        val textWidth: Int
        val textHeight: Int
        val textSize: Float
        @get:ColorInt
        val textColor: Int
        val lineSpacingAdd: Float
        val lineSpacingMult: Float
        val breakStrategy: Int
        val letterSpacing: Float
        val maxLines: Int
    }

    /**
     * Wraps a [TextView] and implements [Reflowable].
     */
    class ReflowableTextView(private val textView: TextView) : Reflowable<TextView> {

        override val view: TextView
            get() = textView

        override val text: String
            get() = textView.getText().toString()

        override val textPosition: Point
            get() = Point(textView.getCompoundPaddingLeft(), textView.getCompoundPaddingTop())

        override val textWidth: Int
            get() = (textView.getWidth()
                - textView.getCompoundPaddingLeft() - textView.getCompoundPaddingRight())

        override val textHeight: Int
            get() = if (textView.getMaxLines() !== -1) {
                (textView.getMaxLines() * textView.getLineHeight()) + 1
            } else {
                (textView.getHeight() - textView.getCompoundPaddingTop()
                    - textView.getCompoundPaddingBottom())
            }

        override val lineSpacingAdd: Float
            get() = textView.getLineSpacingExtra()

        override val lineSpacingMult: Float
            get() = textView.getLineSpacingMultiplier()

        override val breakStrategy: Int
            get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.getBreakStrategy()
            } else -1

        override val letterSpacing: Float
            get() = textView.getLetterSpacing()

        override val textSize: Float
            get() = textView.getTextSize()

        override val textColor: Int
            get() = textView.getCurrentTextColor()

        override val maxLines: Int
            get() = textView.getMaxLines()
    }

    companion object {

        private val EXTRA_REFLOW_DATA = "EXTRA_REFLOW_DATA"
        private val PROPNAME_DATA = "plaid:reflowtext:data"
        private val PROPNAME_TEXT_SIZE = "plaid:reflowtext:textsize"
        private val PROPNAME_BOUNDS = "plaid:reflowtext:bounds"
        private val PROPERTIES = arrayOf(PROPNAME_TEXT_SIZE, PROPNAME_BOUNDS)
        private val TRANSPARENT = 0
        private val OPAQUE = 255
        private val OPACITY_MID_TRANSITION = (0.8f * OPAQUE).toInt()
        private val STAGGER_DECAY = 0.8f

        /**
         * Store data about the view which will participate in a reflow transition in `intent`.
         */
        fun addExtras(intent: Intent, reflowableView: Reflowable<*>) {
            intent.putExtra(EXTRA_REFLOW_DATA, ReflowData(reflowableView))
        }

        /**
         * Retrieve data about the reflow from `intent` and store it for later use.
         */
        fun setupReflow(intent: Intent, view: View) {
            view.setTag(R.id.tag_reflow_data, intent.getParcelableExtra(EXTRA_REFLOW_DATA))
        }

        /**
         * Create data about the reflow from `reflowableView` and store it for later use.
         */
        fun setupReflow(reflowableView: Reflowable<*>) {
            reflowableView.view.setTag(R.id.tag_reflow_data, ReflowData(reflowableView))
        }
    }
}


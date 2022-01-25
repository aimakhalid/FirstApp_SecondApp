package org.by9steps.cityplanetapp.helper

import android.content.Context
import android.graphics.*
import androidx.constraintlayout.solver.widgets.WidgetContainer.getBounds
import android.graphics.Paint.Align
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable
import org.techive.onlinecanteenorder.R

class BadgeDrawable(context: Context) : Drawable() {

    private val mBadgePaint: Paint
    private val mBadgePaint1: Paint
    private val mTextPaint: Paint
    private val mTxtRect = Rect()

    private var mCount = ""
    private var mWillDraw: Boolean = false

    init {
        val mTextSize = context.getResources().getDimension(R.dimen.badge_text_size)

        mBadgePaint = Paint()
        mBadgePaint.setColor(Color.RED)
        mBadgePaint.setAntiAlias(true)
        mBadgePaint.setStyle(Paint.Style.FILL)
        mBadgePaint1 = Paint()
        mBadgePaint1.setColor(ContextCompat.getColor(context.getApplicationContext(), android.R.color.holo_red_dark))
        mBadgePaint1.setAntiAlias(true)
        mBadgePaint1.setStyle(Paint.Style.FILL)

        mTextPaint = Paint()
        mTextPaint.setColor(Color.WHITE)
        mTextPaint.setTypeface(Typeface.DEFAULT)
        mTextPaint.setTextSize(mTextSize)
        mTextPaint.setAntiAlias(true)
        mTextPaint.setTextAlign(Paint.Align.CENTER)
    }

    override fun draw(canvas: Canvas) {


        if (!mWillDraw) {
            return
        }
        val bounds = bounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top

        // Position the badge in the top-right quadrant of the icon.

        /*Using Math.max rather than Math.min */

        val radius = Math.max(width, height) / 2 / 2
        val centerX = width - radius - 1f + 5
        val centerY = radius - 5
        if (mCount.length <= 2) {
            // Draw badge circle.
            canvas.drawCircle(centerX, centerY.toFloat(), (radius + 7.5).toInt().toFloat(), mBadgePaint1)
            canvas.drawCircle(centerX, centerY.toFloat(), (radius + 5.5).toInt().toFloat(), mBadgePaint)
        } else {
            canvas.drawCircle(centerX, centerY.toFloat(), (radius + 8.5).toInt().toFloat(), mBadgePaint1)
            canvas.drawCircle(centerX, centerY.toFloat(), (radius + 6.5).toInt().toFloat(), mBadgePaint)
            //	        	canvas.drawRoundRect(radius, radius, radius, radius, 10, 10, mBadgePaint);
        }
        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = mTxtRect.bottom - mTxtRect.top
        val textY = centerY + textHeight / 2f
        if (mCount.length > 2)
            canvas.drawText("99+", centerX, textY, mTextPaint)
        else
            canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: String) {
        mCount = count

        // Only draw a badge if there are notifications.
        mWillDraw = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}
package datacomprojects.com.hint

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import datacomprojects.com.hint.callbacks.TipNeedToDismissTipInterface
import datacomprojects.com.hint.callbacks.TipViewAnimationEndCallBack
import datacomprojects.com.roundbackground.RoundBackgroundLayout
import datacomprojects.com.tip.R
import kotlinx.android.synthetic.main.hips_view.view.*
import kotlin.math.max
import kotlin.math.min


class TipView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnTouchListener{

    companion object {

        private val CORNER_RADIUS = dpToPx(4)
        private val CURSOR_MARGIN = dpToPx(22)
        private val MIN_CURSOR_MARGIN_WITH_RADIUS = dpToPx(8)
        private val MARGIN_BETWEEN_CURSOR_AND_VIEW = dpToPx(5)
        private val CURSOR_HEIGHT = dpToPx(15)
        private val CURSOR_HALF_WIDTH = dpToPx(15)

        private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    private var cornerRadius: Int = 0
    private var cursorMargin: Int = 0
    private var marginBetweenCursorAndView: Int = 0
    private var cursorHeight: Int = 0
    private var cursorHalfWidth: Int = 0

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint1 = Paint()
    private var path = Path()
    private var pointerPositionTop: Boolean = false
    private var view: View? = null

    var tipNeedToDismissTipInterface: TipNeedToDismissTipInterface? = null
    var tipViewAnimationEndCallBack: TipViewAnimationEndCallBack? = null

    init {
        View.inflate(context, R.layout.hips_view, this)

        pointerPositionTop = true

        cornerRadius = CORNER_RADIUS
        cursorMargin = CURSOR_MARGIN
        marginBetweenCursorAndView = MARGIN_BETWEEN_CURSOR_AND_VIEW
        cursorHeight = CURSOR_HEIGHT
        cursorHalfWidth = CURSOR_HALF_WIDTH

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TipView)

            val textView = findViewById<TextView>(R.id.text)
            val roundBackgroundLayout = findViewById<RoundBackgroundLayout>(R.id.roundBackgroundLayout)

            textView.text = typedArray.getString(R.styleable.TipView_text)
            textView.setTextColor(typedArray.getColor(R.styleable.TipView_textColor, Color.WHITE))

            roundBackgroundLayout.setBackground(
                typedArray.getColor(
                    R.styleable.TipView_backgroundColor,
                    Color.BLACK
                )
            )

            paint.color = typedArray.getColor(R.styleable.TipView_backgroundColor, Color.BLACK)

            pointerPositionTop = typedArray.getBoolean(R.styleable.TipView_viewBottomPosition, true)

            cornerRadius = typedArray.getDimensionPixelSize(
                R.styleable.TipView_cornerRadius,
                CORNER_RADIUS
            )

            paint1.color = typedArray.getColor(
                R.styleable.TipView_shadow_color,
                Color.parseColor("#55000000")
            )

            cursorMargin = typedArray.getDimensionPixelSize(
                R.styleable.TipView_cursorMargin,
                CURSOR_MARGIN
            )

            marginBetweenCursorAndView = typedArray.getDimensionPixelSize(
                R.styleable.TipView_marginToView,
                MARGIN_BETWEEN_CURSOR_AND_VIEW
            )

            typedArray.recycle()
        }

        cursorMargin = max(cursorMargin, MIN_CURSOR_MARGIN_WITH_RADIUS - cornerRadius)

        attachToView(null)

        visibility = View.INVISIBLE

        setOnTouchListener(this)
        roundBackgroundLayout.setOnTouchListener(this)

    }

    fun show() {
        animateViewVisibility(this, View.VISIBLE)
    }

    fun hide() {
        animateViewVisibility(this, View.INVISIBLE)
    }

    override fun dispatchDraw(canvas: Canvas) {

        view?.let {

            val height = roundBackgroundLayout.measuredHeight + cursorHeight + marginBetweenCursorAndView

            val viewLocation = IntArray(2)
            it.getLocationInWindow(viewLocation)

            val parentLocation = IntArray(2)
            (parent as ViewGroup).getLocationInWindow(parentLocation)

            drawBackground(canvas, it)

            if (pointerPositionTop) {
                if (viewLocation[1] - parentLocation[1] + it.measuredHeight + height > (parent as ViewGroup).measuredHeight)
                    pointerPositionTop = false
            } else if (viewLocation[1] - parentLocation[1] < height)
                pointerPositionTop = true

            val viewCenter = viewLocation[0] - parentLocation[0] + it.measuredWidth / 2f
            val parentCenter = (parent as ViewGroup).measuredWidth / 2f
            val tipsCenter = roundBackgroundLayout.measuredWidth / 2f

            val padding = cornerRadius + cursorMargin + cursorHalfWidth

            var translationX: Float

            if (viewCenter < parentCenter) {
                translationX =
                    viewCenter - max(padding.toFloat(), viewCenter + tipsCenter - parentCenter)
                // проверка если выходит за левый край
                if (translationX < 0)
                    translationX += min(
                        -translationX,
                        min(
                            cursorMargin,
                            cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS
                        ).toFloat()
                    )
            } else {
                translationX = viewCenter - roundBackgroundLayout.measuredWidth + max(
                    padding.toFloat(),
                    parentCenter - viewCenter + tipsCenter
                )
                // проверка если выходит за правый край
                val goesOverRightEdge = translationX + roundBackgroundLayout.measuredWidth - parentCenter * 2

                if (goesOverRightEdge > 0)
                    translationX -= min(
                        goesOverRightEdge,
                        min(
                            cursorMargin,
                            cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS
                        ).toFloat()
                    )
            }

            roundBackgroundLayout.translationX = translationX

            path.reset()

            if (pointerPositionTop) {

                val rblPaddingTop = cursorHeight + marginBetweenCursorAndView
                val translationY =
                    viewLocation[1] - parentLocation[1] + it.measuredHeight
                roundBackgroundLayout.translationY = (translationY + rblPaddingTop).toFloat()

                path.moveTo(viewCenter, (translationY + marginBetweenCursorAndView).toFloat())
                path.lineTo(
                    viewCenter - cursorHalfWidth,
                    (translationY + cursorHeight + marginBetweenCursorAndView).toFloat()
                )
                path.lineTo(
                    viewCenter + cursorHalfWidth,
                    (translationY + cursorHeight + marginBetweenCursorAndView).toFloat()
                )
            } else {
                val translationY = viewLocation[1] - parentLocation[1] - height
                roundBackgroundLayout.translationY = translationY.toFloat()

                path.moveTo(
                    viewCenter - cursorHalfWidth,
                    (translationY + roundBackgroundLayout.measuredHeight).toFloat()
                )
                path.lineTo(
                    viewCenter + cursorHalfWidth,
                    (translationY + roundBackgroundLayout.measuredHeight).toFloat()
                )
                path.lineTo(
                    viewCenter,
                    (translationY + roundBackgroundLayout.measuredHeight + cursorHeight).toFloat()
                )
            }

            path.close()
            canvas.drawPath(path, paint)
        }


        super.dispatchDraw(canvas)

    }

    fun attachToView(view: View?) {
        this.view = view
    }

    private fun animateViewVisibility(view: View, visibility: Int) {

        view.animate().cancel()
        view.animate().setListener(null)

        if (visibility == View.VISIBLE) {
            val duration: Long = view.animate().duration + 200
            view.alpha = 0.0f
            view.visibility = View.VISIBLE
            view.animate().setDuration(duration).alpha(1f).start()

        } else {
            view.animate().setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = visibility
                    tipViewAnimationEndCallBack?.animationEnd(visibility)
                }
            }).alpha(0f).start()
        }
    }

    private fun drawBackground(canvas: Canvas, view: View) {
        val viewLocation = IntArray(2)
        view.getLocationInWindow(viewLocation)

        val parentLocation = IntArray(2)
        (parent as ViewGroup).getLocationInWindow(parentLocation)

        val left = viewLocation[0].toFloat() - parentLocation[0]
        val top = viewLocation[1].toFloat() - parentLocation[1]

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas1 = Canvas(bitmap)
        view.draw(canvas1)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint1)

        canvas.drawBitmap(bitmap, left, top, paint)

    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            tipNeedToDismissTipInterface?.needToDismiss()
            return v?.id == roundBackgroundLayout.id
        }
        return false
    }

    fun setCloseImage(drawable: Drawable) {
        closeButton.background = drawable
    }

}
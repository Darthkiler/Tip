package datacomprojects.com.hint

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import datacomprojects.com.roundbackground.RoundBackgroundLayout
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.MotionEvent
import datacomprojects.com.hint.callbacks.TipNeedToDismissTipInterface
import datacomprojects.com.hint.callbacks.TipViewAnimationEndCallBack
import datacomprojects.com.tip.R


class TipView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnTouchListener{

    private val CORNER_RADIUS = dpToPx(4)
    private val CURSOR_MARGIN = dpToPx(22)
    private val MIN_CURSOR_MARGIN_WITH_RADIUS = dpToPx(8)
    private val MARGIN_BETWEEN_CURSOR_AND_VIEW = dpToPx(5)
    private val CURSOR_HEIGHT = dpToPx(15)
    private val CURSOR_HALF_WIDTH = dpToPx(15)

    private var cornerRadius: Int = 0
    private var cursorMargin: Int = 0
    private var marginBetweenCursorAndView: Int = 0
    private var cursorHeight: Int = 0
    private var cursorHalfWidth: Int = 0

    private var paint: Paint
    private var path: Path
    private var pointerPositionTop: Boolean = false
    private var view: View? = null

    var tipNeedToDismissTipInterface: TipNeedToDismissTipInterface? = null
    var tipViewAnimationEndCallBack: TipViewAnimationEndCallBack? = null

    init {
        View.inflate(context, R.layout.hips_view, this)

        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.GREEN
        path = Path()

        pointerPositionTop = true

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TipView)

            val textView = findViewById<TextView>(R.id.text)
            val roundBackgroundLayout = findViewById<RoundBackgroundLayout>(R.id.background)

            textView.text = typedArray.getString(R.styleable.TipView_text)
            textView.setTextColor(typedArray.getColor(R.styleable.TipView_textColor, Color.WHITE))

            roundBackgroundLayout.setBackground(
                typedArray.getColor(
                    R.styleable.TipView_backgroundColor,
                    Color.BLACK
                )
            )

            paint.color = typedArray.getColor(R.styleable.TipView_backgroundColor,Color.BLACK)

            typedArray.recycle()
        }

        cornerRadius = CORNER_RADIUS
        cursorMargin = CURSOR_MARGIN
        marginBetweenCursorAndView = MARGIN_BETWEEN_CURSOR_AND_VIEW
        cursorHeight = CURSOR_HEIGHT
        cursorHalfWidth = CURSOR_HALF_WIDTH

        attachToView(null)

        visibility = View.INVISIBLE

        setOnTouchListener(this)

    }

    fun show() {
        animateViewVisibility(this, View.VISIBLE)
        requestFocus()
        setOnFocusChangeListener { v, _ ->
            tipNeedToDismissTipInterface?.needToDismiss()
        }
    }

    fun hide() {
        animateViewVisibility(this, View.INVISIBLE)
        onFocusChangeListener = null
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        view?.let {

            val height =
                measuredHeight + cursorHeight + marginBetweenCursorAndView - paddingTop - paddingBottom

            val viewLocation = IntArray(2)
            view!!.getLocationInWindow(viewLocation)

            val parentLocation = IntArray(2)
            (parent as ViewGroup).getLocationInWindow(parentLocation)

            if (pointerPositionTop) {
                if (viewLocation[1] - parentLocation[1] + view!!.getMeasuredHeight() + height > (parent as ViewGroup).measuredHeight)
                    pointerPositionTop = false
            } else if (viewLocation[1] - parentLocation[1] < height)
                pointerPositionTop = true

            val viewCenter = viewLocation[0] - parentLocation[0] + view!!.getMeasuredWidth() / 2f
            val parentCenter = (parent as ViewGroup).measuredWidth / 2f
            val tipsCenter = measuredWidth / 2f

            val padding = cornerRadius + cursorMargin + cursorHalfWidth

            var translationX = viewCenter - measuredWidth + Math.max(
                padding.toFloat(),
                Math.min(
                    (measuredWidth - padding).toFloat(),
                    Math.abs(viewCenter - parentCenter - tipsCenter)
                )
            )

            val goesOverRightEdge = translationX + measuredWidth - parentCenter * 2

            if (goesOverRightEdge > 0) {
                translationX -= Math.min(
                    goesOverRightEdge,
                    (2 * Math.min(
                        cursorMargin,
                        cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS
                    )).toFloat()
                )
            }

            setTranslationX(translationX)

            val cursorX = viewCenter - translationX

            path.reset()

            if (pointerPositionTop) {
                setPadding(0, cursorHeight + marginBetweenCursorAndView, 0, 0)
                translationY =
                    (viewLocation[1] - parentLocation[1] + view!!.getMeasuredHeight()).toFloat()

                path.moveTo(cursorX, marginBetweenCursorAndView.toFloat())
                path.lineTo(
                    cursorX - cursorHalfWidth,
                    (cursorHeight + marginBetweenCursorAndView).toFloat()
                )
                path.lineTo(
                    cursorX + cursorHalfWidth,
                    (cursorHeight + marginBetweenCursorAndView).toFloat()
                )
            } else {
                setPadding(0, 0, 0, cursorHeight + marginBetweenCursorAndView)
                translationY = (viewLocation[1] - parentLocation[1] - height).toFloat()

                path.moveTo(
                    cursorX - cursorHalfWidth,
                    (measuredHeight - marginBetweenCursorAndView - cursorHeight).toFloat()
                )
                path.lineTo(
                    cursorX + cursorHalfWidth,
                    (measuredHeight - marginBetweenCursorAndView - cursorHeight).toFloat()
                )
                path.lineTo(cursorX, (measuredHeight - marginBetweenCursorAndView).toFloat())
            }

            path.close()
            canvas.drawPath(path, paint)
        }

    }

    private fun dpToPx(dp: Int): Int = (dp * Resources.getSystem().displayMetrics.density).toInt()

    fun attachToView(view: View?) {
        this.view = view
    }

    private fun animateViewVisibility(view: View, visibility: Int) {

        view.animate().cancel()
        view.animate().setListener(null)

        if (visibility == View.VISIBLE) {
            val duration: Long = view.animate().duration + 200;
            view.alpha = 0.0f;
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

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        tipNeedToDismissTipInterface?.needToDismiss()
        return false
    }

}
package datacomprojects.com.hint;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;
import datacomprojects.com.roundbackground.RoundBackgroundLayout;
import datacomprojects.com.tip.R;

public class HipsView extends ConstraintLayout {

    private static final int CORNER_RADIUS = dpToPx(4);
    private static final int CURSOR_MARGIN = dpToPx(12);
    private static final int MIN_CURSOR_MARGIN_WITH_RADIUS = dpToPx(8);
    private static final int MARGIN_BETWEEN_CURSOR_AND_VIEW = dpToPx(5);
    private static final int CURSOR_HEIGHT = dpToPx(15);
    private static final int CURSOR_HALF_WIDTH = dpToPx(15);

    private int cornerRadius;
    private int cursorMargin;
    private int marginBetweenCursorAndView;
    private int cursorHeight;
    private int cursorHalfWidth;

    private Paint paint;
    private Path path;
    private boolean pointerPositionTop;
    private View firstView;
    private RoundBackgroundLayout rbl;


    public HipsView(Context context) {
        super(context);
        init(context);
    }

    public HipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void init (Context context) {
        View.inflate(context, R.layout.hips_view, this);


        rbl = findViewById(R.id.roundBackgroundLayout);

        setOnTouchListener((v, event) -> {
            hide();
            return false;
        });

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        path = new Path();

        pointerPositionTop = true;


        // Тут надо брать значения из XML и ставить эти по дефолту.
        // Надо делать еще и проверку, чтобы cornerRadius + cursorMargin >= MIN_CURSOR_MARGIN_WITH_RADIUS
        cornerRadius = CORNER_RADIUS;
        cursorMargin = CURSOR_MARGIN;
        marginBetweenCursorAndView = MARGIN_BETWEEN_CURSOR_AND_VIEW;
        cursorHeight = CURSOR_HEIGHT;
        cursorHalfWidth = CURSOR_HALF_WIDTH;

        if(false)
            attachToView(null); // если ид указанно в XML, тут надо делать find через ID

        hide();

    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(INVISIBLE);
    }


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if(firstView == null)
            return;

        int height = rbl.getMeasuredHeight() + cursorHeight + marginBetweenCursorAndView;

        int [] viewLocation = new int[2];
        firstView.getLocationInWindow(viewLocation);

        int [] parentLocation = new int[2];
        ((ViewGroup)getParent()).getLocationInWindow(parentLocation);

        if(pointerPositionTop) {
            if (viewLocation[1] - parentLocation[1] + firstView.getMeasuredHeight() + height > ((ViewGroup) getParent()).getMeasuredHeight())
                pointerPositionTop = false;
        } else if (viewLocation[1] - parentLocation[1] < height)
            pointerPositionTop = true;

        float viewCenter = viewLocation[0] - parentLocation[0] + firstView.getMeasuredWidth() / 2f;
        float parentCenter = ((ViewGroup) getParent()).getMeasuredWidth() / 2f;
        float tipsCenter = rbl.getMeasuredWidth() / 2f;

        int padding = cornerRadius + cursorMargin + cursorHalfWidth;

        float translationX;

        if(viewCenter < parentCenter) {
            translationX = viewCenter - Math.max(padding, viewCenter + tipsCenter - parentCenter);
            // проверка если выходит за левый край
            if(translationX < 0)
                translationX += Math.min(-translationX, Math.min(cursorMargin, cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS));
        } else {
            translationX = viewCenter - rbl.getMeasuredWidth() + Math.max(padding, parentCenter - viewCenter + tipsCenter);
            // проверка если выходит за правый край
            float goesOverRightEdge = translationX + rbl.getMeasuredWidth() - parentCenter * 2;

            if (goesOverRightEdge > 0)
                translationX -= Math.min(goesOverRightEdge, Math.min(cursorMargin, cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS));
        }

        rbl.setTranslationX(translationX);


        path.reset();

        if(pointerPositionTop) {

            int rblPaddingTop = cursorHeight + marginBetweenCursorAndView;
            int translationY = viewLocation[1] - parentLocation[1] + firstView.getMeasuredHeight();
            rbl.setTranslationY(translationY + rblPaddingTop);

            path.moveTo(viewCenter, translationY + marginBetweenCursorAndView);
            path.lineTo(viewCenter - cursorHalfWidth, translationY + cursorHeight + marginBetweenCursorAndView);
            path.lineTo(viewCenter + cursorHalfWidth, translationY + cursorHeight + marginBetweenCursorAndView);
        } else {
            int translationY = viewLocation[1] - parentLocation[1] - height;
            rbl.setTranslationY(translationY);

            path.moveTo(viewCenter - cursorHalfWidth, translationY + rbl.getMeasuredHeight());
            path.lineTo(viewCenter + cursorHalfWidth, translationY + rbl.getMeasuredHeight());
            path.lineTo(viewCenter, translationY + rbl.getMeasuredHeight() + cursorHeight);
        }

        path.close();
        canvas.drawPath(path, paint);

    }


    public void attachToView(View firstView) {
        this.firstView = firstView;
    }
}

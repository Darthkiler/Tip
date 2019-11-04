package datacomprojects.com.hint;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import datacomprojects.com.tip.R;

public class HipsView extends ConstraintLayout {

    private static final int CORNER_RADIUS = dpToPx(4);
    private static final int CURSOR_MARGIN = dpToPx(22);
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
    private View view;


    public HipsView(Context context) {
        super(context);
        init(context,null);
    }

    public HipsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public HipsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init (Context context, AttributeSet attrs) {
        View.inflate(context, R.layout.hips_view, this);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.GREEN);
        path = new Path();

        pointerPositionTop = true;


        // Тут надо брать значения из XML и ставить эти по дефолту.
        // Надо делать еще и проверку, чтобы cornerRadius + cursorMargin >= MIN_CURSOR_MARGIN_WITH_RADIUS



        /*if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.HipsView);

            TextView textView = findViewById(R.id.text);
            RoundBackgroundLayout roundBackgroundLayout = findViewById(R.id.background);

            textView.setText(typedArray.getString(R.styleable.HipsView_text));
            textView.setTextColor(typedArray.getColor(R.styleable.HipsView_textColor,Color.WHITE));
            //Typeface typeface = ResourcesCompat.getFont(context,typedArray.getResourceId(R.styleable.HipsView_font_Family,-1));
            //textView.setTypeface(typeface);

            roundBackgroundLayout.setBackground(typedArray.getColor(R.styleable.HipsView_backgroundColor, Color.BLACK));

            typedArray.recycle();
        }*/

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
        requestFocus();

        setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus)
                hide();
        });
    }

    public void hide() {
        setVisibility(INVISIBLE);
        setOnFocusChangeListener(null);
    }


    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if(view == null)
            return;

        int height = getMeasuredHeight() + cursorHeight + marginBetweenCursorAndView - getPaddingTop() - getPaddingBottom();

        int [] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);

        int [] parentLocation = new int[2];
        ((ViewGroup)getParent()).getLocationInWindow(parentLocation);

        if(pointerPositionTop) {
            if (viewLocation[1] - parentLocation[1] + view.getMeasuredHeight() + height > ((ViewGroup) getParent()).getMeasuredHeight())
                pointerPositionTop = false;
        } else if (viewLocation[1] - parentLocation[1] < height)
            pointerPositionTop = true;

        float viewCenter = viewLocation[0] - parentLocation[0] + view.getMeasuredWidth() / 2f;
        float parentCenter = ((ViewGroup) getParent()).getMeasuredWidth() / 2f;
        float tipsCenter = getMeasuredWidth() / 2f;

        int padding = cornerRadius + cursorMargin + cursorHalfWidth;

        float translationX;

        if(viewCenter < parentCenter) {
            translationX = viewCenter - Math.max(padding, viewCenter + tipsCenter - parentCenter);
            // проверка если выходит за левый край
            if(translationX < 0)
                translationX += Math.min(-translationX, Math.min(cursorMargin, cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS));
        } else {
            translationX = viewCenter - getMeasuredWidth() + Math.max(padding, parentCenter - viewCenter + tipsCenter);
            // проверка если выходит за правый край
            float goesOverRightEdge = translationX + getMeasuredWidth() - parentCenter * 2;

            if (goesOverRightEdge > 0)
                translationX -= Math.min(goesOverRightEdge, Math.min(cursorMargin, cornerRadius + cursorMargin - MIN_CURSOR_MARGIN_WITH_RADIUS));
        }

        setTranslationX(translationX);


        float cursorX = viewCenter - translationX;

        path.reset();

        if(pointerPositionTop) {
            setPadding(0, cursorHeight + marginBetweenCursorAndView, 0, 0);
            setTranslationY(viewLocation[1] - parentLocation[1] + view.getMeasuredHeight());

            path.moveTo(cursorX, marginBetweenCursorAndView);
            path.lineTo(cursorX - cursorHalfWidth, cursorHeight + marginBetweenCursorAndView);
            path.lineTo(cursorX + cursorHalfWidth, cursorHeight + marginBetweenCursorAndView);
        } else {
            setPadding(0, 0, 0, cursorHeight + marginBetweenCursorAndView);
            setTranslationY(viewLocation[1] - parentLocation[1] - height);

            path.moveTo(cursorX - cursorHalfWidth, getMeasuredHeight() - marginBetweenCursorAndView - cursorHeight);
            path.lineTo(cursorX + cursorHalfWidth, getMeasuredHeight() - marginBetweenCursorAndView - cursorHeight);
            path.lineTo(cursorX, getMeasuredHeight() - marginBetweenCursorAndView);
        }

        path.close();
        canvas.drawPath(path, paint);

    }

    public void attachToView(View view) {
        this.view = view;
    }

}

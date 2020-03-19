package com.platon.aton.component.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.platon.aton.R;
import com.platon.aton.utils.DensityUtil;

/**
 * @author matrixelement
 */
public class CustomUnderlineEditText extends AppCompatEditText {

    public enum Status {
        UNFOCUSED {
            @Override
            int getStrokeColor() {
                return R.color.color_d5d8df;
            }
        }, FOCUSED {
            @Override
            int getStrokeColor() {
                return R.color.color_0077ff;
            }
        }, ERROR {
            @Override
            int getStrokeColor() {
                return R.color.color_ff3030;
            }
        };

        abstract int getStrokeColor();
    }

    private GradientDrawable gradientDrawable;
    private Context context;
    private int actionX;
    private int actionY;
    private Drawable drawableRight;
    private DrawableClickListener clickListener;

    public CustomUnderlineEditText(Context context) {
        this(context, null);
    }

    public CustomUnderlineEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        // this Contructure required when you are using this view in xml
    }

    public CustomUnderlineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomUnderlineEditText, defStyleAttr, 0);

        int statusValue = typedArray.getInteger(R.styleable.CustomUnderlineEditText_status, Status.FOCUSED.ordinal());

        Drawable drawable = getBackground();
        if (drawable instanceof LayerDrawable) {

            LayerDrawable layerDrawable = (LayerDrawable) drawable;

            gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.shape);

            setStatus(Status.values()[statusValue]);
        }

    }

    public void setStatus(Status status) {
        if (gradientDrawable != null) {
            gradientDrawable.setStroke(DensityUtil.dp2px(context, 1f), ContextCompat.getColor(context, status.getStrokeColor()));
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Rect bounds;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            actionX = (int) event.getX();
            actionY = (int) event.getY();

            drawableRight = getCompoundDrawables()[2];

            if (drawableRight != null) {

                bounds = drawableRight.getBounds();

                int x, y;
                int extraTapArea = 13;
                /**
                 * IF USER CLICKS JUST OUT SIDE THE RECTANGLE OF THE DRAWABLE
                 * THAN ADD X AND SUBTRACT THE Y WITH SOME VALUE SO THAT AFTER
                 * CALCULATING X AND Y CO-ORDINATE LIES INTO THE DRAWBABLE
                 * BOUND. - this process help to increase the tappable area of
                 * the rectangle.
                 */
                x = actionX + extraTapArea;
                y = actionY - extraTapArea;

                /**Since this is right drawable subtract the value of x from the width
                 * of view. so that width - tappedarea will result in x co-ordinate in drawable bound.
                 */
                x = getWidth() - x;

                /*x can be negative if user taps at x co-ordinate just near the width.
                 * e.g views width = 300 and user taps 290. Then as per previous calculation
                 * 290 + 13 = 303. So subtract X from getWidth() will result in negative value.
                 * So to avoid this add the value previous added when x goes negative.
                 */

                if (x <= 0) {
                    x += extraTapArea;
                }

                /* If result after calculating for extra tappable area is negative.
                 * assign the original value so that after subtracting
                 * extratapping area value doesn't go into negative value.
                 */

                if (y <= 0) {
                    y = actionY;
                }

                /**If drawble bounds contains the x and y points then move ahead.*/
                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener
                            .onClick(DrawableClickListener.DrawablePosition.RIGHT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;
                }
                return super.onTouchEvent(event);
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        drawableRight = null;
        super.finalize();
    }

    public interface DrawableClickListener {

        enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}

        void onClick(DrawablePosition target);
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }
}

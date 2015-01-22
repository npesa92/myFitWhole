package com.nicky.myfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by nicholas on 12/15/14.
 */
public class CircularView extends View {

    String letter = "";
    int textColor = android.R.color.white, ovalColor = R.color.yellow_a400;

    public CircularView(Context context) {
        super(context);
    }

    public CircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        canvas.drawOval(rect, mOvalPaint);
        float textHeight = (textPaint.descent() - textPaint.ascent());
        float textOffset = (textHeight / 2) - textPaint.descent();
        canvas.drawText(letter, rect.centerX(), rect.centerY() + textOffset , textPaint);
    }

    public void setLetter(String letter) {
        this.letter = letter;
        invalidate();
    }

    public void setOvalColor(int ovalColor) {
        this.ovalColor = ovalColor;
        invalidate();
    }

    private final Paint mArcPaint = new Paint() {
        {
            setDither(true);
            setStyle(Style.FILL_AND_STROKE);
            setStrokeCap(Paint.Cap.ROUND);
            setStrokeJoin(Paint.Join.ROUND);
            setColor(getResources().getColor(R.color.md_red_a400));
            setStrokeWidth(1.0f);
            setAntiAlias(true);
        }
    };

    private final Paint mOvalPaint = new Paint() {
        {
            setColor(getResources().getColor(ovalColor));
        }
    };

    private final Paint textPaint =  new Paint() {
        {
            setColor(getResources().getColor(textColor));
            setTextAlign(Paint.Align.CENTER);
            setTextSize(156);
            setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Thin.ttf"));
        }
    };
}

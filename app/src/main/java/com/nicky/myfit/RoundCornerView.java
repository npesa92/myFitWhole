package com.nicky.myfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nicholas on 1/3/15.
 */
public class RoundCornerView extends View {

    String letter = "";
    int textColor = R.color.grey_500, ovalColor = R.color.yellow_a400;

    public static float radius = 18.0f;

    public RoundCornerView(Context context) {
        super(context);
    }

    public RoundCornerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundCornerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas c) {
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        c.drawRoundRect(rect, radius, radius, mOvalPaint);
        float textHeight = (textPaint.descent() - textPaint.ascent());
        float textOffset = (textHeight / 2) - textPaint.descent();
        c.drawText(letter, rect.centerX(), rect.centerY() + textOffset, textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        setMeasuredDimension(widthSize, heightSize);
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
            setTextSize(24);
            setTypeface(Typeface.createFromAsset(getContext().getAssets(), "Roboto-Thin.ttf"));
        }
    };
}

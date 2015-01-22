package com.nicky.myfit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by nicholas on 12/12/14.
 */
public class FloatingActionButton extends ImageView {

    public FloatingActionButton(Context context) {
        super(context);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path clipPath = new Path();
        //clipPath.addCircle((this.getWidth() / 2), (this.getHeight() / 2), (this.getWidth() / 2), Path.Direction.CW);
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        canvas.drawOval(rect, mOvalPaint);
        canvas.drawArc(rect, 269, 360, true, mArcPaint);
        canvas.drawText("A", canvas.getHeight()/2, canvas.getWidth()/2, textPaint);

        /*Paint paint = new Paint(Paint.LINEAR_TEXT_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(12.0F);
        canvas.drawText("A", canvas.getHeight(), canvas.getWidth(), paint);*/


    }

    private final Paint mArcPaint = new Paint() {
        {
            setDither(true);
            setStyle(Style.FILL_AND_STROKE);
            setStrokeCap(Paint.Cap.ROUND);
            setStrokeJoin(Paint.Join.ROUND);
            setColor(getResources().getColor(R.color.md_red_a400));
            setStrokeWidth(2.0f);
            setAntiAlias(true);
        }
    };

    private final Paint mOvalPaint = new Paint() {
        {
            setStyle(Paint.Style.FILL);
            setColor(Color.RED);
        }
    };

    private final Paint textPaint =  new Paint() {
        {
            setColor(Color.WHITE);
            setAntiAlias(true);
            setTextSize(56f);
            setTextAlign(Paint.Align.CENTER);
        }
    };
}

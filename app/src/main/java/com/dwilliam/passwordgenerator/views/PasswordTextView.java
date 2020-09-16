package com.dwilliam.passwordgenerator.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dwilliam.passwordgenerator.R;

public class PasswordTextView extends View {

    private static final int DEFAULT_TEXT_SIZE = 20;

    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String mText;
    private final StringBuilder sb = new StringBuilder();
    private final int mMinLines;

    public PasswordTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        float density = getResources().getDisplayMetrics().density;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordTextView, 0, 0);
        int size = a.getDimensionPixelSize(R.styleable.PasswordTextView_android_textSize, (int) (density * DEFAULT_TEXT_SIZE));
        int color = a.getColor(R.styleable.PasswordTextView_android_textColor, Color.BLACK);

        mText = "";

        mMinLines = a.getInt(R.styleable.PasswordTextView_minLines, 0);

        mTextPaint.setTextSize(size);
        mTextPaint.setColor(color);
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    private float getTextWidth(String text) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    private String[] formatLines() {
        sb.setLength(0);
        int width = getWidth();
        int s = 0;
        int[] points = mText.codePoints().toArray();
        for (int i = 0; i < points.length; i++) {
            String str = new String(points, s, i+1-s);
            if (getTextWidth(str) >= width - 4) {
                sb.append('\n').appendCodePoint(points[i]);
                s = i;
            }
            else sb.appendCodePoint(points[i]);
        }
        return sb.toString().split("\n");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height;

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else {
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            height = (int) (mMinLines * (fm.descent + (-fm.ascent)));
            if (heightMode == MeasureSpec.AT_MOST) height = Math.min(height, heightSize);
        }

        setMeasuredDimension(widthSize, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float yPos = getHeight() - fm.bottom;
        for (String line : formatLines()) {
            canvas.drawText(line,0, yPos, mTextPaint);
            yPos = yPos + fm.top;
        }
    }

}

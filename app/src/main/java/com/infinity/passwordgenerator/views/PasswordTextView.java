package com.infinity.passwordgenerator.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.infinity.passwordgenerator.R;

public class PasswordTextView extends View {

    private static final int DEFAULT_TEXT_SIZE = 20;

    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private String mText;
    private StringBuilder sb = new StringBuilder();
    private int mMinLines;

    public PasswordTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        float density = getResources().getDisplayMetrics().density;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PasswordTextView, 0, 0);
        int size = a.getDimensionPixelSize(R.styleable.PasswordTextView_android_textSize, (int) (density * DEFAULT_TEXT_SIZE));
        int color = a.getColor(R.styleable.PasswordTextView_android_textColor, Color.BLACK);
        mText = a.getString(R.styleable.PasswordTextView_android_text);
        if (mText == null) mText = "";

        mMinLines = a.getInt(R.styleable.PasswordTextView_minLines, 0);

        mTextPaint.setTextSize(size);
        mTextPaint.setColor(color);
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    public float getTextWidth(String text) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String[] lines = formatLines(getWidth());
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();

        float yPos = getHeight() - fm.bottom;
        for (String line : lines) {
            canvas.drawText(line,0, yPos, mTextPaint);
            yPos = yPos + fm.top;
        }
    }

    private String[] formatLines(int width) {
        sb.setLength(0);
        int s = 0;
        for (int i = 0; i < mText.length(); i++) {
            String str = mText.substring(s,i+1);
            if (getTextWidth(str) >= width - 4) {
                sb.append('\n').append(mText.charAt(i));
                s = i;
            }
            else sb.append(mText.charAt(i));
        }
        return sb.toString().split("\n");

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) width = widthSize;
        else {
            width = (int) getTextWidth(mText);
            if (widthMode == MeasureSpec.AT_MOST) width = Math.min(width, widthSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) height = heightSize;
        else {
            Paint.FontMetrics fm = mTextPaint.getFontMetrics();
            int lines = Math.max(formatLines(width).length, mMinLines);
            height = (int) (lines * (fm.descent + (-fm.ascent)));
            if (heightMode == MeasureSpec.AT_MOST) height = Math.min(height, heightSize);
        }

        setMeasuredDimension(width, height);
    }
}

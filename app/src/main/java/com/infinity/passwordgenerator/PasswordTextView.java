package com.infinity.passwordgenerator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PasswordTextView extends View {

    private String mText;
    private TextPaint mTextPaint;
    private StringBuilder sb;
    private float density;

    public PasswordTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mText = "";
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.colorClear));
        density = getResources().getDisplayMetrics().density;
        mTextPaint.setTextSize(20 * density);
        sb = new StringBuilder();
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public float getTextHeight() {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(mText, 0, mText.length(), bounds);
        return bounds.height();
    }

    public float getTextWidth(String text) {
        Rect bounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        sb.setLength(0);
        int s = 0;
        for (int i = 0; i < mText.length(); i++) {
            String str = mText.substring(s,i+1);
            sb.append(mText.charAt(i));
            if (getTextWidth(str) >= getWidth() - 20 * density) {
                sb.append('\n');
                s = i + 1;
                if (s < 0) s = 1;
            }
        }

        String[] split = sb.toString().split("\n");
        float yPos = getHeight() - 20;
        for (int i = split.length - 1; i >= 0; i--) {
            canvas.drawText(split[i], 0.0f, yPos, mTextPaint);
            yPos = yPos - getTextHeight() - 10;
        }
    }

}

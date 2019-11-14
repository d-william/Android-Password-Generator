package com.infinity.passwordgenerator;

import android.app.Dialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.lang.reflect.Field;
import java.util.Objects;

public class LengthDialog extends DialogFragment {

    private int length;
    private Listener listener;
    NumberPicker numberPicker;

    public LengthDialog(int length, Listener listener) {
        this.length = length;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()), R.style.AppTheme_Dialog);
        LayoutInflater factory = LayoutInflater.from(getContext());
        View layout = factory.inflate(R.layout.length_dialog_layout, null);
        builder.setMessage(R.string.choose_length)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    listener.onLengthDialogPositiveClick(this.length);
                })
                .setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();

        dialog.show();

        numberPicker = dialog.findViewById(R.id.length);
        numberPicker.setMinValue(8);
        numberPicker.setMaxValue(64);
        numberPicker.setValue(length);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> this.length = newVal);
        color();

        return dialog;
    }

    public interface Listener {
        void onLengthDialogPositiveClick(int length);
    }

    public void color() {

        try {
            Field text = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
            Field divider = numberPicker.getClass().getDeclaredField("mSelectionDivider");
            text.setAccessible(true);
            divider.setAccessible(true);
            ((Paint)text.get(numberPicker)).setColor(getResources().getColor(R.color.colorClear));
            divider.set(numberPicker, getResources().getDrawable(R.drawable.divider));
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) { Log.w("NumberPickerTextColor", e); }

        for (int i = 0; i < numberPicker.getChildCount(); i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) ((EditText)child).setTextColor(getResources().getColor(R.color.colorClear));
        }

        numberPicker.invalidate();
    }

}


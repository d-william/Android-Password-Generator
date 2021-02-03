package com.dwilliam.passwordgenerator.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.dwilliam.passwordgenerator.mediators.ParametersMediator;
import com.dwilliam.passwordgenerator.R;
import com.dwilliam.passwordgenerator.activities.MainActivity;

import java.lang.reflect.Field;
import java.util.Objects;

public class LengthDialog extends DialogFragment {

    private int length;
    private Listener listener;
    private final int min;
    private final int max;
    private NumberPicker numberPicker;

    public LengthDialog() {
        this.min = ParametersMediator.MIN;
        this.max = ParametersMediator.MAX;
        this.length = -1;
    }

    public LengthDialog(int length) {
        this.min = ParametersMediator.MIN;
        this.max = ParametersMediator.MAX;
        this.length = length;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            this.listener = (Listener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (savedInstanceState != null) this.length = savedInstanceState.getInt("length", -1);

        if (this.length == -1) throw new IllegalStateException();

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setView(R.layout.length_dialog_layout);
        builder.setMessage(R.string.choose_length)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> listener.onLengthDialogPositiveClick(this.length))
                .setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = builder.create();

        dialog.show();

        numberPicker = Objects.requireNonNull(dialog.findViewById(R.id.length));
        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);
        numberPicker.setValue(this.length);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> this.length = newVal);
        //fixColor();

        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("length", this.length);
    }

    public interface Listener {
        void onLengthDialogPositiveClick(int length);
    }

    public void fixColor() {

        int color = getResources().getColor(R.color.colorClear, null);
        try {
            Field text = numberPicker.getClass().getDeclaredField("mSelectorWheelPaint");
            Field divider = numberPicker.getClass().getDeclaredField("mSelectionDivider");
            text.setAccessible(true);
            divider.setAccessible(true);
            ((Paint) Objects.requireNonNull(text.get(numberPicker))).setColor(color);
            divider.set(numberPicker, ResourcesCompat.getDrawable(getResources(), R.drawable.divider, null));
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) { Log.w("NumberPickerTextColor", e); }

        for (int i = 0; i < numberPicker.getChildCount(); i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) ((EditText)child).setTextColor(color);
        }

        numberPicker.invalidate();
    }

}


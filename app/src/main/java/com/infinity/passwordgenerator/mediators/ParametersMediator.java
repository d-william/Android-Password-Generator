package com.infinity.passwordgenerator.mediators;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.infinity.passwordgenerator.dialogs.LengthDialog;
import com.infinity.passwordgenerator.views.CustomChooserView;
import com.infinity.utils.RandomString;

import java.util.Arrays;

public class ParametersMediator implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, CustomChooserView.DialogListener {

    public static final int MIN = 8;
    public static final int MAX = 64;
    private static final boolean DEFAULT_REGULAR = false;
    private static final boolean DEFAULT_CUSTOM = true;
    public static final boolean[] DEFAULT_STATES = {DEFAULT_REGULAR, true, true, true, false};
    private static final String DEFAULT_SYMBOLS = RandomString.LOWER_CASE_LETTERS + RandomString.UPPER_CASE_LETTERS + RandomString.DIGITS;

    private final AppCompatActivity context;

    private RandomString random;
    private String symbols;
    private int length;

    private View decrease;
    private View increase;
    private TextView textLength;
    private SeekBar bar;

    private RadioButton regular;
    private RadioButton custom;

    private Switch lower;
    private Switch upper;
    private Switch digit;
    private Switch special;

    private CustomChooserView chooser;

    private OnParametersChangeListener listener;

    private final boolean[] states;

    public ParametersMediator(AppCompatActivity context, String symbols, int length, boolean[] states, int posSymbols, View decrease, View increase, TextView textLength, SeekBar bar, RadioButton regular, RadioButton custom, Switch lower, Switch upper, Switch digit, Switch special, CustomChooserView chooser, OnParametersChangeListener listener) {
        this.context = context;
        this.states = Arrays.copyOf(states, states.length);
        this.decrease = decrease;
        this.increase = increase;
        this.textLength = textLength;
        this.bar = bar;
        this.regular = regular;
        this.custom = custom;
        this.lower = lower;
        this.upper = upper;
        this.digit = digit;
        this.special = special;
        this.chooser = chooser;
        this.listener = listener;
        this.decrease.setOnClickListener(this);
        this.increase.setOnClickListener(this);
        this.textLength.setOnClickListener(this);
        this.bar.setOnSeekBarChangeListener(this);
        this.bar.setMax(MAX - MIN);
        this.regular.setChecked(!states[0]);
        this.custom.setChecked(states[0]);
        this.lower.setChecked(states[1]);
        this.upper.setChecked(states[2]);
        this.digit.setChecked(states[3]);
        this.special.setChecked(states[4]);
        this.chooser.custom(posSymbols);
        verifyStates();
        if (states[0]) {
            enableCustom();
            disableRegular();
        }
        else {
            enableRegular();
            disableCustom();
        }
        this.regular.setOnCheckedChangeListener(this);
        this.custom.setOnCheckedChangeListener(this);
        this.lower.setOnCheckedChangeListener(this);
        this.upper.setOnCheckedChangeListener(this);
        this.digit.setOnCheckedChangeListener(this);
        this.special.setOnCheckedChangeListener(this);
        this.chooser.setOnClickListener(this);
        this.chooser.setDialogListener(this);
        length(length);
        if (symbols == null) this.symbols = DEFAULT_SYMBOLS;
        else this.symbols = symbols;
        random = new RandomString(length, this.symbols);
    }

    public int length() {
        return length;
    }

    public void length(int length) {
        this.length = length;
        this.decrease.setEnabled(length != MIN);
        this.increase.setEnabled(length != MAX);
        this.textLength.setText(String.valueOf(length));
        this.bar.setProgress(length - MIN);
    }

    public boolean[] states() {
        return Arrays.copyOf(states, states.length);
    }

    public int selected() {
        return this.chooser.selected();
    }

    public String symbols() {
        return this.symbols;
    }

    public String nextString() {
        return random.nextString();
    }

    public void determineSymbols() {
        if (this.regular.isChecked()) {
            StringBuilder sb = new StringBuilder();
            if (lower.isChecked()) sb.append(RandomString.LOWER_CASE_LETTERS);
            if (upper.isChecked()) sb.append(RandomString.UPPER_CASE_LETTERS);
            if (digit.isChecked()) sb.append(RandomString.DIGITS);
            if (special.isChecked()) sb.append(RandomString.SPECIAL_CHARACTERS);
            if (listener != null) {
                this.symbols = sb.toString();
                random = new RandomString(length, this.symbols);
                listener.onParameterChanged(this.symbols, length);
            }
        }
        else {
            if (this.chooser.hasSymbols() && listener != null) {
                this.symbols = this.chooser.symbols();
                random = new RandomString(length, this.symbols);
                listener.onParameterChanged(this.symbols, length);
            }
        }
    }

    private void verifyStates() {
        states[1] = lower.isChecked();
        states[2] = upper.isChecked();
        states[3] = digit.isChecked();
        states[4] = special.isChecked();
        lower.setEnabled(!(lower.isChecked() && !upper.isChecked() && !digit.isChecked() && !special.isChecked()));
        upper.setEnabled(!(upper.isChecked() && !lower.isChecked() && !digit.isChecked() && !special.isChecked()));
        digit.setEnabled(!(digit.isChecked() && !upper.isChecked() && !lower.isChecked() && !special.isChecked()));
        special.setEnabled(!(special.isChecked() && !upper.isChecked() && !digit.isChecked() && !lower.isChecked()));
    }

    @Override
    public void onClick(View view) {
        if (view == chooser) {
            this.chooser.getDialog().show(this.context.getSupportFragmentManager(), "custom_menu");
        }
        else if (view == textLength) {
            new LengthDialog(length()).show(this.context.getSupportFragmentManager(), "length");
        }
        else {
            int newLength = length;
            if (view == this.decrease) {
                if (newLength != MIN) newLength--;
                length(newLength);
            } else if (view == this.increase) {
                if (newLength != MAX) newLength++;
                length(newLength);
            }
            determineSymbols();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        verifyStates();
        if (isChecked) {
            if (button == regular) {
                states[0] = DEFAULT_REGULAR;
                custom.setOnCheckedChangeListener(null);
                custom.setChecked(false);
                disableCustom();
                enableRegular();
                custom.setOnCheckedChangeListener(this);
                if (!this.chooser.hasSymbols()) return;
            }
            else if (button == custom) {
                states[0] = DEFAULT_CUSTOM;
                regular.setOnCheckedChangeListener(null);
                regular.setChecked(false);
                disableRegular();
                enableCustom();
                regular.setOnCheckedChangeListener(this);
                if (!this.chooser.hasSymbols()) return;
            }
        }
        determineSymbols();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            length(progress + MIN);
            determineSymbols();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onDialogDismiss() {
        if (this.chooser.hasSymbols()) determineSymbols();
        else {
            if (listener != null) listener.onParameterChanged(null, -1);
        }
    }

    @Override
    public void onDialogCanceled() {
        if (!this.chooser.hasSymbols()) this.regular.setChecked(true);
    }

    private void enableRegular() {
        this.lower.setEnabled(true);
        this.upper.setEnabled(true);
        this.digit.setEnabled(true);
        this.special.setEnabled(true);
    }

    private void enableCustom() {
        this.chooser.setEnabled(true);
        if (!this.chooser.hasSymbols()) this.chooser.performClick();
    }

    private void disableRegular() {
        this.lower.setEnabled(false);
        this.upper.setEnabled(false);
        this.digit.setEnabled(false);
        this.special.setEnabled(false);
    }

    private void disableCustom() {
        this.chooser.setEnabled(false);
    }

    public void regular() {
        this.regular.setChecked(true);
        determineSymbols();
    }

    public boolean reloadChooserSymbols() {
        return this.chooser.loadSymbols();
    }

    public void selectCustoms(String name) {
        this.chooser.custom(this.chooser.indexOf(name));
        determineSymbols();
    }

    public void resetChooser() {
        this.chooser.reset();
    }

    public interface OnParametersChangeListener {
        void onParameterChanged(String newSymbols, int length);
    }



}

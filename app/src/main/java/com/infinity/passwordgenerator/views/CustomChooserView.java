package com.infinity.passwordgenerator.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.infinity.passwordgenerator.CustomSymbols;
import com.infinity.passwordgenerator.R;
import com.infinity.passwordgenerator.dialogs.CustomSymbolsDialog;

import java.util.ArrayList;
import java.util.List;

public class CustomChooserView extends ConstraintLayout implements CustomSymbolsDialog.Listener {

    private List<CustomSymbols> symbols;
    private CustomSymbols customSymbols;

    private DialogListener listener;

    private TextView mText;
    private ImageView mThumb;

    public CustomChooserView(Context context) {
        this(context,null);
    }

    public CustomChooserView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomChooserView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), R.layout.custom_chooser_layout, this);
        mText = findViewById(R.id.choice);
        mThumb = findViewById(R.id.arrow);
        loadSymbols();
    }

    public List<CustomSymbols> list() {
        return new ArrayList<>(this.symbols);
    }

    private void loadSymbols() {
        this.symbols = new ArrayList<>(CustomSymbols.commons());
        // TODO user symbols
    }

    public boolean hasSymbols() {
        return this.customSymbols != null;
    }

    public String symbols() {
        return this.customSymbols == null ? null : this.customSymbols.symbols();
    }

    public void custom(int which) {
        if (which == -1) customSymbols = null;
        else customSymbols = this.symbols.get(which);
        if (customSymbols == null) mText.setText(getContext().getText(R.string.add_custom));
        else mText.setText(customSymbols.name());
    }

    public DialogFragment getDialog() {
        return new CustomSymbolsDialog();
    }

    @Override
    public void onDismiss(int which, boolean canceled) {
        custom(which);
        if (canceled && listener != null) listener.onDialogCanceled();
        else if (listener != null) listener.onDialogDismiss();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mText.setEnabled(enabled);
        mThumb.setEnabled(enabled);
    }

    public void setDialogListener(DialogListener listener) {
        this.listener = listener;
    }

    public int selected() {
        return this.symbols.indexOf(customSymbols);
    }

    public interface DialogListener {
        void onDialogDismiss();
        void onDialogCanceled();
    }
}

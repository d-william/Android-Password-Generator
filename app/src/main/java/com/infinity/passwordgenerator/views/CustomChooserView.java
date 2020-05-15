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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
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

    public void loadSymbols() {
        this.symbols = new ArrayList<>(CustomSymbols.commons());
        File app = getContext().getFilesDir();
        File symbolsDir = new File(app, "symbols");
        symbolsDir.mkdirs();
        for (File f : symbolsDir.listFiles()) {
            BufferedReader buff;
            try {
                buff = new BufferedReader(new FileReader(f));
                this.symbols.add(new CustomSymbols(f.getName(), buff.readLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    public int indexOf(String name) {
        if (name == null) return -1;
        for (int i = 0; i < this.symbols.size(); i++) {
            if (name.equals(this.symbols.get(i).name())) return i;
        }
        return -1;
    }

    public interface DialogListener {
        void onDialogDismiss();
        void onDialogCanceled();
    }
}

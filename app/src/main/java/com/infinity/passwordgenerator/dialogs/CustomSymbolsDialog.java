package com.infinity.passwordgenerator.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.infinity.passwordgenerator.CustomSymbols;
import com.infinity.passwordgenerator.R;
import com.infinity.passwordgenerator.activities.MainActivity;
import com.infinity.passwordgenerator.views.CustomChooserView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CustomSymbolsDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private List<CustomSymbols> symbols;
    private Listener listener;
    private boolean isCanceled = false;
    private int which = -1;
    private MainActivity context;

    public CustomSymbolsDialog() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            this.context = (MainActivity) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CustomChooserView v = this.context.findViewById(R.id.chooser);
        this.listener = v;
        this.symbols = v.list();
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        String[] symbols_names = new String[symbols.size() + 1];
        symbols_names[0] = getResources().getString(R.string.add_custom);
        for (int i = 1; i < symbols_names.length; i++) {
            symbols_names[i] = symbols.get(i-1).name();
        }
        builder.setItems(symbols_names, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_NEGATIVE) {
            isCanceled = true;
        }
        else {
            this.which = which;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDismiss(which != 0 && which != -1 ? which - 1 : -1, isCanceled);
            isCanceled = false;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        isCanceled = true;
        super.onCancel(dialog);
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public interface Listener {
        void onDismiss(int which, boolean canceled);
    }
}


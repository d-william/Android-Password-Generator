package com.dwilliam.passwordgenerator.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.dwilliam.passwordgenerator.R;

import java.util.Objects;

public class BulkDialog extends DialogFragment implements TextWatcher {

    private Listener listener;
    private EditText size;
    private AlertDialog dialog;

    public static BulkDialog newInstance(Listener listener) {
        return new BulkDialog(listener);
    }

    public BulkDialog() {}

    public BulkDialog(Listener listener) {
        this.listener = listener;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        LayoutInflater factory = LayoutInflater.from(getContext());
        View layout = factory.inflate(R.layout.bulk_dialog_layout, null);
        builder.setMessage(R.string.choose_bulk)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> listener.onBulkDialogPositiveClick(getSize()))
                .setNegativeButton(android.R.string.cancel, null);
        dialog = builder.create();
        dialog.show();
        disablePositiveButton();
        size = dialog.findViewById(R.id.size);
        Objects.requireNonNull(size).addTextChangedListener(this);
        return dialog;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if ("".equals(s.toString())) {
            disablePositiveButton();
            return;
        }
        else enablePositiveButton();
        if (Integer.parseInt(s.toString()) <= 0) {
            s.clear();
            s.append('1');
        }
    }

    private void enablePositiveButton() {
        Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setEnabled(true);
        btn.setAlpha(1f);
    }

    private void disablePositiveButton() {
        Button btn = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setEnabled(false);
        btn.setAlpha(0.5f);
    }

    private int getSize() {
        return Integer.parseInt(size.getText().toString());
    }

    public interface Listener {
        void onBulkDialogPositiveClick(int size);
    }

}


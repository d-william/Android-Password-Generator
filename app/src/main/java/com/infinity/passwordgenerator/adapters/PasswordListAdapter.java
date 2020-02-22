package com.infinity.passwordgenerator.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import com.infinity.passwordgenerator.R;
import com.infinity.passwordgenerator.activities.QRCodeActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class PasswordListAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private int r1;
    private int r2;
    private ClipboardManager clipboard;
    private View activityView;
    private boolean mode;
    private LinkedHashSet<Integer> checked;
    private OnCheckedItemLengthChangeListener listener;

    public PasswordListAdapter(@NonNull Context context, int r1, int r2, ClipboardManager clipboard, View activityView) {
        super(context, 0, R.id.password);
        mInflater = LayoutInflater.from(context);
        this.r1 = r1;
        this.r2 = r2;
        this.clipboard = clipboard;
        this.activityView = activityView;
        this.mode = false;
        this.checked = new LinkedHashSet<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (mode) {
            convertView = super.getView(position, mInflater.inflate(r2, parent, false), parent);
            CheckBox select = convertView.findViewById(R.id.select);
            select.setChecked(isChecked(position));
            select.setOnCheckedChangeListener((v, isChecked) -> setChecked(position, isChecked));
            convertView.setOnClickListener(v -> toggleChecked(position));
        }
        else {
            convertView = super.getView(position, mInflater.inflate(r1, parent, false), parent);
            convertView.findViewById(R.id.copy).setOnClickListener(v -> copy(position));
            convertView.findViewById(R.id.share).setOnClickListener(v -> share(position));
            convertView.findViewById(R.id.qrcode).setOnClickListener(v -> qrcode(position));
        }
        return convertView;
    }

    private void copy(int position) {
        ClipData clip = ClipData.newPlainText("password", getItem(position));
        clipboard.setPrimaryClip(clip);
        Snackbar snackbar = Snackbar.make(activityView.findViewById(R.id.activity), "Password copied !", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void share(int position) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getItem(position));
        sendIntent.setType("text/plain");
        getContext().startActivity(sendIntent);
    }

    private void qrcode(int position) {
        Intent intent = new Intent(getContext(), QRCodeActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, getItem(position));
        getContext().startActivity(intent);
    }

    public void setOnCheckedItemLengthChangeListener(OnCheckedItemLengthChangeListener listener) {
        this.listener = listener;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

    public int getCheckedCount() {
        return checked.size();
    }

    public boolean isChecked(int position) {
        return checked.contains(position);
    }

    public void setChecked(int position, boolean state) {
        int oldSize = checked.size();
        if (state) checked.add(position);
        else checked.remove(position);
        listener.onChange(oldSize, checked.size());
    }

    private void toggleChecked(int position) {
        int oldSize = checked.size();
        if (isChecked(position)) checked.add(position);
        else checked.remove(position);
        listener.onChange(oldSize, checked.size());
    }

    public void checkAll() {
        int oldSize = checked.size();
        for (int i = 0; i < getCount(); i++) checked.add(i);
        listener.onChange(oldSize, checked.size());
    }


    public void uncheckAll() {
        int oldSize = checked.size();
        checked.clear();
        listener.onChange(oldSize, checked.size());
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> result = new ArrayList<>(checked.size());
        for (int i : checked) result.add(getItem(i));
        return result;
    }

    public void removeChecked() {
        ArrayList<String> toRemove = new ArrayList<>(checked.size());
        for (int i : checked) toRemove.add(getItem(i));
        for (String s : toRemove) remove(s);
        checked.clear();
    }

    public interface OnCheckedItemLengthChangeListener {
        void onChange(int oldSize, int newSize);
    }

}

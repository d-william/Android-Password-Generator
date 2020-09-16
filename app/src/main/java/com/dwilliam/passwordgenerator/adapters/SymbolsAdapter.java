package com.dwilliam.passwordgenerator.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dwilliam.passwordgenerator.R;
import com.dwilliam.passwordgenerator.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class SymbolsAdapter extends ArrayAdapter<String> {

    public interface OnClickListener {
        void onClick(String name);
    }

    private final OnClickListener listener;

    private final File symbolsFolder;

    public SymbolsAdapter(@NonNull Context context, ArrayList<String> datas, OnClickListener listener) {
        super(context, R.layout.item_edit_symbols, R.id.tvName, datas);
        this.listener = listener;
        this.symbolsFolder = FileUtils.getSymbolsFolder(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ImageView delete = v.findViewById(R.id.delete);
        v.setOnClickListener(view -> this.listener.onClick(getItem(position)));
        delete.setOnClickListener(view -> {
            String name = getItem(position);
            FileUtils.deleteSymbols(this.symbolsFolder, name);
            remove(name);
            notifyDataSetChanged();
        });
        return v;
    }

}

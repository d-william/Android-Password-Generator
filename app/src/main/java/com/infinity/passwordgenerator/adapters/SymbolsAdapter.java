package com.infinity.passwordgenerator.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.infinity.passwordgenerator.R;
import com.infinity.passwordgenerator.activities.CustomSymbolsActivity;
import com.infinity.passwordgenerator.activities.CustomSymbolsListActivity;

import java.io.File;
import java.util.ArrayList;

public class SymbolsAdapter extends ArrayAdapter<String> {

    public interface OnClickListener {
        void onClick(String name);
    }

    private OnClickListener listener;

    public SymbolsAdapter(@NonNull Context context, ArrayList<String> datas, OnClickListener listener) {
        super(context, R.layout.item_edit_symbols, R.id.textView, datas);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ImageView delete = v.findViewById(R.id.delete);
        v.setOnClickListener(view -> this.listener.onClick(getItem(position)));
        delete.setOnClickListener(view -> {
            File app = getContext().getFilesDir();
            File symbolsDir = new File(app, "symbols");
            symbolsDir.mkdirs();
            String name = getItem(position);
            for (File f : symbolsDir.listFiles()) {
                if (f.getName().equals(name)) {
                    f.delete();
                    remove(name);
                    notifyDataSetChanged();
                    return;
                }
            }
        });
        return v;
    }
}

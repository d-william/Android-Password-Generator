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

    public SymbolsAdapter(@NonNull Context context, ArrayList<String> datas) {
        super(context, R.layout.item_edit_symbols, R.id.textView, datas);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        ImageView edit = v.findViewById(R.id.edit);
        ImageView delete = v.findViewById(R.id.delete);
        View.OnClickListener listener = view -> {
            Intent intent = new Intent(getContext(), CustomSymbolsActivity.class);
            intent.putExtra("name", getItem(position));
            getContext().startActivity(intent);
        };
        v.setOnClickListener(listener);
        edit.setOnClickListener(listener);
        delete.setOnClickListener(view -> {
            File app = getContext().getFilesDir();
            File symbolsDir = new File(app, "symbols");
            symbolsDir.mkdirs();
            String name = getItem(position);
            for (File f : symbolsDir.listFiles()) {
                if (f.getName().equals(name)) {
                    f.delete();
                    return;
                }
            }
            notifyDataSetChanged();
        });
        return v;
    }
}

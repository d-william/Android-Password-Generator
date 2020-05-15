package com.infinity.passwordgenerator.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.infinity.passwordgenerator.R;
import com.infinity.passwordgenerator.adapters.SymbolsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomSymbolsListActivity extends AppCompatActivity {

    private ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_symbols_list);

        ListView list = findViewById(R.id.symbols);
        File app = getFilesDir();
        File symbolsDir = new File(app, "symbols");
        symbolsDir.mkdirs();
        names = new ArrayList<>();
        for (File f : symbolsDir.listFiles()) {
            names.add(f.getName());
        }
        SymbolsAdapter adapter = new SymbolsAdapter(this, names);
        list.setAdapter(adapter);
    }
}

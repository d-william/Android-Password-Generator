package com.infinity.passwordgenerator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.infinity.passwordgenerator.CustomSymbols;
import com.infinity.passwordgenerator.R;
import com.infinity.passwordgenerator.adapters.CheckboxExpandableListAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class CustomSymbolsActivity extends AppCompatActivity implements CheckboxExpandableListAdapter.Listener, TextWatcher {

    public static final String ACTION_ADD_CUSTOM = "android.intent.action.ADD_CUSTOM";
    public static final String ACTION_EDIT_CUSTOM = "android.intent.action.EDIT_CUSTOM";

    private EditText name;
    private EditText symbols;

    private MenuItem item;
    private CheckboxExpandableListAdapter adapter;

    private boolean editMode = false;
    private String old_name;

    private ArrayList<String> names;
    private CustomSymbols cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_symbols);

        Intent intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
            if (ACTION_ADD_CUSTOM.equals(intent.getAction())) actionBar.setTitle(R.string.add_custom);
            else if (ACTION_EDIT_CUSTOM.equals(intent.getAction())) {
                File app = getFilesDir();
                File symbolsDir = new File(app, "symbols");
                symbolsDir.mkdirs();
                old_name = intent.getStringExtra("name");
                for (File f : symbolsDir.listFiles()) {
                    if (f.getName().equals(old_name)) {
                        BufferedReader buff;
                        try {
                            buff = new BufferedReader(new FileReader(f));
                            cs = new CustomSymbols(f.getName(), buff.readLine());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                if (cs == null) finish();

                actionBar.setTitle(cs.name());
                editMode = true;
            }
            else finish();
        }

        name = findViewById(R.id.name);
        if (editMode) name.setText(cs.name());
        symbols = findViewById(R.id.symbols);
        ExpandableListView selection = findViewById(R.id.selection);

        File app = getFilesDir();
        File symbolsDir = new File(app, "symbols");
        symbolsDir.mkdirs();
        names = new ArrayList<>();
        for (File f : symbolsDir.listFiles()) {
            names.add(f.getName());
        }

        /// ---------------------------------

        adapter = new CheckboxExpandableListAdapter(this);
        selection.setAdapter(adapter);
        adapter.setListener(this);
        if (editMode) adapter.setSymbols(cs.symbols());
        else symbols.setText(adapter.getSymbols());
        name.addTextChangedListener(this);
        symbols.addTextChangedListener(this);

        /// ---------------------------------

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item = menu.findItem(R.id.check);
        item.setEnabled(!name.getText().toString().equals(""));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        if (id == R.id.check) add();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        invalidateOptionsMenu();
    }

    private void add() {
        File app = getFilesDir();
        File symbolsDir = new File(app, "symbols");
        symbolsDir.mkdirs();
        String new_name = name.getText().toString();
        File symbolsFile = new File(symbolsDir, new_name);
        try {
            symbolsFile.createNewFile();
            try (PrintStream out = new PrintStream(new FileOutputStream(symbolsFile))) {
                out.print(symbols.getText().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent data = new Intent();
        data.putExtra("name", name.getText().toString());
        setResult(RESULT_OK, data);
        if (editMode) {
            if (!old_name.equals(new_name)) {
                File oldFile = new File(symbolsDir, old_name);
                oldFile.delete();
            }
        }
        finish();
    }

    @Override
    public void onSymbolsChange() {
        symbols.setText(adapter.getSymbols());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (item == null) return;
        item.setEnabled(name.getText().length() > 0 && symbols.getText().length() > 0);
        if (!editMode && names.contains(name.getText().toString())) {
            item.setEnabled(false);
            name.setError("Name already used");
        }
    }

}

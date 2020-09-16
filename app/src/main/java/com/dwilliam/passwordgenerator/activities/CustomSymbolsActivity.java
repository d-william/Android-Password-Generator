package com.dwilliam.passwordgenerator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.dwilliam.passwordgenerator.models.CustomSymbols;
import com.dwilliam.passwordgenerator.R;
import com.dwilliam.passwordgenerator.utils.FileUtils;
import com.dwilliam.passwordgenerator.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class CustomSymbolsActivity extends AppCompatActivity {

    public static final String ACTION_ADD_CUSTOM = "android.intent.action.ADD_CUSTOM";
    public static final String ACTION_EDIT_CUSTOM = "android.intent.action.EDIT_CUSTOM";

    private EditText name;
    private EditText symbols;

    private MenuItem item;

    private boolean editMode = false;
    private String old_name;

    private ArrayList<String> names;
    private CustomSymbols cs;

    private File symbolsFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_symbols);

        Intent intent = getIntent();

        symbolsFolder = FileUtils.getSymbolsFolder(getApplicationContext());
        File[] files = symbolsFolder.listFiles();
        Objects.requireNonNull(files);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear);
            if (ACTION_ADD_CUSTOM.equals(intent.getAction())) actionBar.setTitle(R.string.add_custom);
            else if (ACTION_EDIT_CUSTOM.equals(intent.getAction())) {
                old_name = intent.getStringExtra("name");
                for (File f : files) {
                    if (f.getName().equals(old_name)) {
                        cs = FileUtils.getSymbols(f);
                        break;
                    }
                }
                if (cs == null) finish();

                actionBar.setTitle(cs.name());
                editMode = true;
            }
            else finish();
        }
        else finish();

        name = findViewById(R.id.name);
        symbols = findViewById(R.id.symbols);

        if (editMode) {
            name.setText(cs.name());
            symbols.setText(cs.symbols());
        }

        names = new ArrayList<>();
        for (File f : files) names.add(f.getName());
        if (editMode) names.remove(cs.name());

        /// ---------------------------------

        BaseTextWatcher base = new BaseTextWatcher();

        name.addTextChangedListener(base);
        symbols.addTextChangedListener(base);
        symbols.addTextChangedListener(new SymbolsTextWatcher());

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
        String new_name = name.getText().toString();
        FileUtils.createSymbolsFile(symbolsFolder, new_name, symbols.getText().toString());
        Intent data = new Intent();
        data.putExtra("name", new_name);
        setResult(RESULT_OK, data);
        if (editMode) {
            if (!old_name.equals(new_name)) {
                FileUtils.deleteSymbols(symbolsFolder, old_name);
            }
        }
        finish();
    }

    private class BaseTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (item == null) return;
            item.setEnabled(name.getText().length() > 0 && symbols.getText().length() > 0);
            name.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.colorAccent));
            if (names.contains(name.getText().toString())) {
                item.setEnabled(false);
                name.setError("Name already used");
                name.setBackgroundTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.red));
            }
        }

    }

    private static class SymbolsTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (StringUtils.hasDuplicates(s.toString())) {
                String str = StringUtils.removeDuplicates(s.toString());
                s.clear();
                s.append(str);
            }
        }

    }


}

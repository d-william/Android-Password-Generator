package com.infinity.passwordgenerator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.infinity.passwordgenerator.CheckboxExpandableListAdapter;
import com.infinity.passwordgenerator.R;
import com.infinity.utils.RandomString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSymbolsActivity extends AppCompatActivity {

    public static final String ACTION_ADD_CUSTOM = "android.intent.action.ADD_CUSTOM";

    private EditText name;
    private EditText symbols;

    private MenuItem item;
    private ExpandableListView selection;

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
            else actionBar.setTitle(intent.getStringExtra("name"));
        }

        name = findViewById(R.id.name);
        name.addTextChangedListener(new NameTextWatcher());

        symbols = findViewById(R.id.symbols);
        symbols.setText(RandomString.LOWER_CASE_LETTERS + RandomString.UPPER_CASE_LETTERS + RandomString.DIGITS);
        symbols.addTextChangedListener(new NameTextWatcher());

        selection = findViewById(R.id.selection);

        /// ---------------------------------

        selection.setAdapter(new CheckboxExpandableListAdapter(this));

        /// ---------------------------------

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
        item = menu.findItem(R.id.check);
        item.setEnabled(!name.getText().toString().equals(""));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        if (id == R.id.check) add();
        return super.onOptionsItemSelected(item);
    }

    private void add() {
        // TODO save in storage
        Intent data = new Intent();
        data.putExtra("name", name.getText().toString());
        data.putExtra("symbols", symbols.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    private class NameTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            item.setEnabled(!s.toString().equals(""));
        }
    }

    private class SymbolsTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            item.setEnabled(!s.toString().equals(""));
        }
    }


    /// ---------------------------------


    /// ---------------------------------

}

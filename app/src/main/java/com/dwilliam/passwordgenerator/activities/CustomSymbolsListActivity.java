package com.dwilliam.passwordgenerator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.dwilliam.passwordgenerator.R;
import com.dwilliam.passwordgenerator.adapters.SymbolsAdapter;
import com.dwilliam.passwordgenerator.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class CustomSymbolsListActivity extends AppCompatActivity implements SymbolsAdapter.OnClickListener {

    private ArrayList<String> names;
    private SymbolsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_symbols_list);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Edit symbols");
        }

        ListView list = findViewById(R.id.symbols);
        loadSymbols();
        adapter = new SymbolsAdapter(this, names, this);
        list.setAdapter(adapter);
    }

    private void loadSymbols() {
        File symbolsFolder = FileUtils.getSymbolsFolder(getApplicationContext());
        names = new ArrayList<>();
        for (File f : Objects.requireNonNull(symbolsFolder.listFiles())) names.add(f.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_symbols_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        if (id == R.id.add) add();
        else return super.onOptionsItemSelected(item);
        return true;
    }

    private void add() {
        Intent intent = new Intent(this, CustomSymbolsActivity.class);
        intent.setAction(CustomSymbolsActivity.ACTION_ADD_CUSTOM);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            loadSymbols();
            adapter.clear();
            adapter.addAll(names);
            adapter.notifyDataSetChanged();
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                loadSymbols();
                adapter.clear();
                adapter.addAll(names);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(String name) {
        Intent intent = new Intent(this, CustomSymbolsActivity.class);
        intent.setAction(CustomSymbolsActivity.ACTION_EDIT_CUSTOM);
        intent.putExtra("name", name);
        startActivityForResult(intent, 0);
    }

}

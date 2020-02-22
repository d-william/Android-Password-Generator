package com.infinity.passwordgenerator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.infinity.passwordgenerator.R;

public class CustomSymbolsActivity extends AppCompatActivity {

    public static final String ACTION_ADD_CUSTOM = "android.intent.action.ADD_CUSTOM";

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu, menu);
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
        data.putExtra("name", "New Custom"); // TODO
        data.putExtra("symbols", "abc"); // TODO
        setResult(RESULT_OK, data);
        finish();
    }

}

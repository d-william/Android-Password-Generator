package com.dwilliam.passwordgenerator.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.dwilliam.passwordgenerator.R;
import com.dwilliam.passwordgenerator.adapters.PasswordListAdapter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;

public class PasswordListActivity extends AppCompatActivity implements PasswordListAdapter.OnCheckedItemLengthChangeListener {

    public static final String ACTION_HISTORY = "android.intent.action.HISTORY";
    public static final String ACTION_BULK = "android.intent.action.BULK";
    public static final String ACTION_CLEAR_HISTORY = "android.intent.action.CLEAR_HISTORY";
    public static final String ACTION_REMOVE_FROM_HISTORY = "android.intent.action.REMOVE_FROM_HISTORY";

    private ClipboardManager clipboard;
    private PasswordListAdapter adapter;
    private boolean mode;
    private ArrayList<String> toRemove;

    private ImageView copy;
    private ImageView share;
    private ImageView delete;
    private ImageView check;
    private View tools;
    private MenuItem clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        Intent intent = getIntent();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (ACTION_BULK.equals(intent.getAction())) actionBar.setTitle(R.string.bulk);
            else actionBar.setTitle(R.string.history);
        }

        ArrayList<String> list = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
        setResult(RESULT_CANCELED, new Intent());
        if (list == null) finish();

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        toRemove = new ArrayList<>();

        copy = findViewById(R.id.copy);
        share = findViewById(R.id.share);
        delete = findViewById(R.id.delete);
        check = findViewById(R.id.check);
        tools = findViewById(R.id.tools_container);

        adapter = new PasswordListAdapter(this, clipboard, findViewById(R.id.activity));
        adapter.addAll(Objects.requireNonNull(list));

        if (savedInstanceState != null) {
            mode = savedInstanceState.getBoolean("mode", false);
            if (mode) {
                enableButton();
                adapter.setCheckedSet(new LinkedHashSet<>(savedInstanceState.getIntegerArrayList("checkedStates")));
                enableEditMode();
            }
            else {
                disableButton();
            }
        }
        else {
            mode = false;
        }

        adapter.setOnCheckedItemLengthChangeListener(this);
        ListView history = findViewById(R.id.history);
        history.setAdapter(adapter);
        history.setOnItemLongClickListener((parent, view, position, id) -> {
            enableEditMode(position);
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        outState.putBoolean("mode", mode);
        outState.putIntegerArrayList("checkedStates", new ArrayList<>(adapter.getCheckedSet()));
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        clear = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) if (mode) disableEditMode(); else finish();
        else if (id == R.id.clear) clearHistory();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void clearHistory() {
        Intent data = new Intent();
        data.setAction(ACTION_CLEAR_HISTORY);
        setResult(RESULT_OK, data);
        adapter.clear();
    }

    private void enableEditMode(int position) {
        if (mode) return;
        mode = true;
        vibrate();
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_clear);
        clear.setVisible(false);
        adapter.setMode(mode);
        adapter.setChecked(position, true);
        adapter.notifyDataSetChanged();
        tools.setVisibility(View.VISIBLE);
    }

    private void enableEditMode() {
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_clear);
        //clear.setVisible(false);
        adapter.setMode(mode);
        adapter.notifyDataSetChanged();
        tools.setVisibility(View.VISIBLE);
    }

    private void disableEditMode() {
        if (!mode) return;
        mode = false;
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_back);
        clear.setVisible(true);
        adapter.setMode(mode);
        adapter.uncheckAll();
        adapter.notifyDataSetChanged();
        tools.setVisibility(View.GONE);
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        else ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
    }

    @Override
    public void onChange(int newSize) {
        if (newSize == 0) disableButton();
        else enableButton();
        if (newSize == adapter.getCount()) check.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_uncheck_all));
        else check.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_check_all));
    }

    private void enableButton() {
        copy.setAlpha(1f);
        share.setAlpha(1f);
        delete.setAlpha(1f);
        copy.setClickable(true);
        share.setClickable(true);
        delete.setClickable(true);
    }

    private void disableButton() {
        copy.setAlpha(.5f);
        share.setAlpha(.5f);
        delete.setAlpha(.5f);
        copy.setClickable(false);
        share.setClickable(false);
        delete.setClickable(false);
    }

    public void onCopyButtonClick(View v) {
        ArrayList<String> checkedItems = adapter.getCheckedItems();
        StringBuilder sb = new StringBuilder();
        int max = checkedItems.size() - 1;
        for (int i = 0;; i++) {
            sb.append(checkedItems.get(i));
            if (i == max) break;
            sb.append('\n');
        }
        copy(sb.toString());
    }

    public void onShareButtonClick(View v) {
        ArrayList<String> checkedItems = adapter.getCheckedItems();
        StringBuilder sb = new StringBuilder();
        int max = checkedItems.size() - 1;
        for (int i = 0;; i++) {
            sb.append(checkedItems.get(i));
            if (i == max) break;
            sb.append('\n');
        }
        share(sb.toString());
    }

    public void onDeleteButtonClick(View v) {
        toRemove.addAll(adapter.getCheckedItems());
        adapter.removeChecked();
        adapter.notifyDataSetChanged();
        Intent data = new Intent();
        data.setAction(ACTION_REMOVE_FROM_HISTORY);
        data.putStringArrayListExtra(Intent.EXTRA_TEXT, toRemove);
        setResult(RESULT_OK, data);
    }

    public void onCheckButtonClick(View v) {
        if (adapter.getCheckedCount() == adapter.getCount()) adapter.uncheckAll();
        else adapter.checkAll();
        adapter.notifyDataSetChanged();
    }

    private void copy(String text) {
        ClipData clip = ClipData.newPlainText("passwords", text);
        clipboard.setPrimaryClip(clip);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), "Password(s) copied !", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void share(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}

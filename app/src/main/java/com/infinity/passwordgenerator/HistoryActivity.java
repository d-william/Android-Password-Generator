package com.infinity.passwordgenerator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnCheckedItemLengthChangeListener {

    private ClipboardManager clipboard;
    private HistoryAdapter adapter;
    private boolean mode;
    private float density;
    private ArrayList<String> toRemove;

    private ListView history;
    private ImageView copy;
    private ImageView share;
    private ImageView delete;
    private ImageView check;
    private View tools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String historySerialized = intent.getStringExtra(Intent.EXTRA_TEXT);

        setResult(RESULT_CANCELED, new Intent());

        if (historySerialized == null) finish();

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        density = getResources().getDisplayMetrics().density;
        toRemove = new ArrayList<>();

        copy = findViewById(R.id.copy);
        share = findViewById(R.id.share);
        delete = findViewById(R.id.delete);
        check = findViewById(R.id.check);

        tools = findViewById(R.id.tools_container);

        disableButton();

        mode = false;
        adapter = new HistoryAdapter(this, R.layout.history_item, R.layout.history_item_mode , R.id.password, clipboard, findViewById(R.id.activity));
        adapter.addAll(deserialize(historySerialized));
        adapter.setOnCheckedItemLengthChangeListener(this);
        history = findViewById(R.id.history);
        history.setAdapter(adapter);
        history.setOnItemLongClickListener((parent, view, position, id) -> {
            enableEditMode(position);
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
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
        data.setAction(MainActivity.ACTION_CLEAR_HISTORY);
        setResult(RESULT_OK, data);
        adapter.clear();
    }

    private void enableEditMode(int position) {
        if (mode) return;
        mode = true;
        vibrate();
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_clear);
        adapter.setMode(mode);
        adapter.setChecked(position, true);
        adapter.notifyDataSetChanged();
        history.setPadding(history.getPaddingLeft(), history.getPaddingTop(), history.getPaddingRight(), 64 * (int) density);
        tools.setVisibility(View.VISIBLE);
    }

    private void disableEditMode() {
        if (!mode) return;
        mode = false;
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(com.google.android.material.R.drawable.abc_ic_ab_back_material);
        adapter.setMode(mode);
        adapter.uncheckAll();
        adapter.notifyDataSetChanged();
        history.setPadding(history.getPaddingLeft(), history.getPaddingTop(), history.getPaddingRight(), 8 * (int) density);
        tools.setVisibility(View.GONE);
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
        else ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(150);
    }

    private String serialize(ArrayList<String> passwords) {
        String result = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(passwords);
            oos.close();
            result = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        }
        catch (Exception e) {

        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> deserialize(String historySerialized) {
        Collection<String> history = null;
        try {
            byte[] decode = Base64.decode(historySerialized, Base64.DEFAULT);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decode));
            history = (Collection<String>) ois.readObject();
            ois.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return history;
    }

    @Override
    public void onChange(int oldSize, int newSize) {
        if (newSize == 0) disableButton();
        else enableButton();

        if (newSize == adapter.getCount()) check.setImageDrawable(getDrawable(R.drawable.ic_uncheck_all));
        else check.setImageDrawable(getDrawable(R.drawable.ic_check_all));
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
        data.setAction(MainActivity.ACTION_REMOVE_FROM_HISTORY);
        data.putExtra(Intent.EXTRA_TEXT, serialize(toRemove));
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

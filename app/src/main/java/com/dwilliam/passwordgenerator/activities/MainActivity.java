package com.dwilliam.passwordgenerator.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.dwilliam.passwordgenerator.R;
import com.dwilliam.passwordgenerator.dialogs.BulkDialog;
import com.dwilliam.passwordgenerator.dialogs.LengthDialog;
import com.dwilliam.passwordgenerator.mediators.ParametersMediator;
import com.dwilliam.passwordgenerator.views.PasswordTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BulkDialog.Listener, LengthDialog.Listener, ParametersMediator.OnParametersChangeListener {

    private String currentPassword;
    private ArrayList<String> history;

    private ClipboardManager clipboard;

    private PasswordTextView passwordTextView;

    private ParametersMediator mediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState (@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("password", currentPassword);
        outState.putStringArrayList("history", history);
        outState.putInt("length", mediator.length());
        outState.putBooleanArray("states", mediator.states());
        outState.putInt("custom", mediator.selected());
        outState.putString("symbols", mediator.symbols());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.history) history();
        else if (id == R.id.bulk) bulk();
        else if (id == R.id.add_custom) createSymbols();
        else if (id == R.id.edit_custom) editCustom();
        return super.onOptionsItemSelected(item);
    }

    private void init(Bundle savedInstanceState) {
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        passwordTextView = findViewById(R.id.password);
        findViewById(R.id.new_password).setOnClickListener(v -> reload());
        findViewById(R.id.new_password).setOnLongClickListener(this::reloadContinually);
        findViewById(R.id.copy).setOnClickListener(v -> copy());
        findViewById(R.id.share).setOnClickListener(v -> share());
        findViewById(R.id.qrcode).setOnClickListener(v -> qrcode());

        int length = ParametersMediator.MIN;
        boolean[] states = ParametersMediator.DEFAULT_STATES;
        int custom = -1;
        String symbols = null;
        if (savedInstanceState != null) {
            history = savedInstanceState.getStringArrayList("history");
            currentPassword = savedInstanceState.getString("password");
            length = savedInstanceState.getInt("length");
            states = savedInstanceState.getBooleanArray("states");
            custom = savedInstanceState.getInt("custom");
            symbols = savedInstanceState.getString("symbols");
        }
        if (history == null) history = new ArrayList<>();

        mediator = new ParametersMediator(
                    this, symbols, length, states, custom,
                    findViewById(R.id.decrease),
                    findViewById(R.id.increase),
                    findViewById(R.id.length),
                    findViewById(R.id.lengthBar),
                    findViewById(R.id.regular),
                    findViewById(R.id.custom),
                    findViewById(R.id.lower),
                    findViewById(R.id.upper),
                    findViewById(R.id.digit),
                    findViewById(R.id.special),
                    findViewById(R.id.chooser),
                    this
        );
        if (currentPassword != null) passwordTextView.setText(currentPassword);
        else mediator.determineSymbols();
    }

    private void reload() {
        currentPassword = mediator.nextString();
        passwordTextView.setText(currentPassword);
        history.add(currentPassword);
    }

    private boolean reloadContinually(View v) {
        new Thread(() -> {
            Runnable runnable = this::reload;
            while (v.isPressed()) {
                runOnUiThread(runnable);
                try { Thread.sleep(50); }
                catch (InterruptedException e) { Log.w("PasswordGenerator", "Interrupted thread"); }
            }
        }).start();
        return true;
    }

    private void copy() {
        clipboard.setPrimaryClip(ClipData.newPlainText("password", currentPassword));
        Snackbar.make(findViewById(R.id.activity), "Password copied !", Snackbar.LENGTH_LONG).show();
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, currentPassword);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void qrcode() {
        Intent intent = new Intent(MainActivity.this, QRCodeActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, currentPassword);
        startActivity(intent);
    }

    @Override
    public void onParameterChanged(String symbols, int length) {
        if (symbols == null || length == -1) addSymbols();
        else if (!symbols.equals("")) reload();
    }

    private void history() {
        Intent intent = new Intent(MainActivity.this, PasswordListActivity.class);
        intent.setAction(PasswordListActivity.ACTION_HISTORY);
        intent.putStringArrayListExtra(Intent.EXTRA_TEXT, history);
        startActivityForResult(intent, 0);
    }

    private void bulk() {
        DialogFragment newFragment = BulkDialog.newInstance(this);
        newFragment.show(getSupportFragmentManager(), "bulk");
    }

    private void addSymbols() {
        Intent intent = new Intent(MainActivity.this, CustomSymbolsActivity.class);
        intent.setAction(CustomSymbolsActivity.ACTION_ADD_CUSTOM);
        startActivityForResult(intent, 1);
    }

    private void createSymbols() {
        Intent intent = new Intent(MainActivity.this, CustomSymbolsActivity.class);
        intent.setAction(CustomSymbolsActivity.ACTION_ADD_CUSTOM);
        startActivityForResult(intent, 2);
    }

    private void editCustom() {
        Intent intent = new Intent(MainActivity.this, CustomSymbolsListActivity.class);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (PasswordListActivity.ACTION_CLEAR_HISTORY.equals(data.getAction()))
                        clearHistory();
                    else if (PasswordListActivity.ACTION_REMOVE_FROM_HISTORY.equals(data.getAction())) {
                        ArrayList<String> toRemove = data.getStringArrayListExtra(Intent.EXTRA_TEXT);
                        if (toRemove != null) history.removeAll(toRemove);
                    }
                }
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mediator.reloadChooserSymbols();
                mediator.selectCustoms(data.getStringExtra("name"));
            }
            else {
                mediator.regular();
            }
        }
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                mediator.reloadChooserSymbols();
            }
        }
        else if (requestCode == 3) {
            if(!mediator.reloadChooserSymbols()) {
                mediator.regular();
                mediator.resetChooser();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearHistory() {
        history.clear();
    }

    @Override
    public void onBulkDialogPositiveClick(int size) {
        new Thread(() -> {
            ArrayList<String> passwords = new ArrayList<>(size);
            for (int i = 0; i < size; i++) passwords.add(mediator.nextString());
            Intent intent = new Intent(MainActivity.this, PasswordListActivity.class);
            intent.setAction(PasswordListActivity.ACTION_BULK);
            intent.putStringArrayListExtra(Intent.EXTRA_TEXT, passwords);
            startActivity(intent);
        }).start();
    }

    @Override
    public void onLengthDialogPositiveClick(int length) {
        mediator.length(length);
        mediator.determineSymbols();
    }

}

package com.infinity.passwordgenerator.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.infinity.passwordgenerator.ParametersMediator;
import com.infinity.passwordgenerator.dialogs.BulkDialog;
import com.infinity.passwordgenerator.dialogs.LengthDialog;
import com.infinity.passwordgenerator.views.PasswordTextView;
import com.infinity.passwordgenerator.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import android.util.Base64;
import android.view.View;

public class MainActivity extends AppCompatActivity implements BulkDialog.Listener, LengthDialog.Listener, ParametersMediator.OnParametersChangeListener {

    private String currentPassword;
    private ArrayList<String> history;

    private ClipboardManager clipboard;

    private PasswordTextView passwordTextView;

    public ParametersMediator mediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
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
        ClipData clip = ClipData.newPlainText("password", currentPassword);
        clipboard.setPrimaryClip(clip);
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity), "Password copied !", Snackbar.LENGTH_LONG);
        snackbar.show();
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
        String extra = serialize(history);
        Intent intent = new Intent(MainActivity.this, PasswordListActivity.class);
        intent.setAction(PasswordListActivity.ACTION_HISTORY);
        intent.putExtra(Intent.EXTRA_TEXT, extra);
        startActivityForResult(intent, 0);
    }

    private void bulk() {
        DialogFragment newFragment = BulkDialog.newInstance();
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
        startActivity(intent);
    }

    private void editCustom() {
        // TODO
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (PasswordListActivity.ACTION_CLEAR_HISTORY.equals(data.getAction())) clearHistory();
                    else if (PasswordListActivity.ACTION_REMOVE_FROM_HISTORY.equals(data.getAction())) history.removeAll(deserialize(data.getStringExtra(Intent.EXTRA_TEXT)));
                }
            }
        }
        else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // TODO
            }
            else {
                // TODO
            }
        }
    }

    private void clearHistory() {
        history.clear();
    }

    @Override
    public void onBulkDialogPositiveClick(int size) {
        new Thread(() -> {
            ArrayList<String> passwords = new ArrayList<>(size);
            for (int i = 0; i < size; i++) passwords.add(mediator.nextString());
            String extra = serialize(passwords);
            Intent intent = new Intent(MainActivity.this, PasswordListActivity.class);
            intent.setAction(PasswordListActivity.ACTION_BULK);
            intent.putExtra(Intent.EXTRA_TEXT, extra);
            startActivity(intent);
        }).start();
    }

    @Override
    public void onLengthDialogPositiveClick(int length) {
        mediator.length(length);
        mediator.determineSymbols();
    }

}

package com.infinity.passwordgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.infinity.utils.RandomString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import android.util.Base64;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, LengthDialog.Listener, BulkDialog.Listener {

    public static final String ACTION_CLEAR_HISTORY = "android.intent.action.CLEAR_HISTORY";
    public static final String ACTION_REMOVE_FROM_HISTORY = "android.intent.action.REMOVE_FROM_HISTORY";

    private RandomString random;
    private String currentSymbols;
    private String currentPassword;
    private int currentLength;
    private ArrayList<String> history;

    private ClipboardManager clipboard;

    private PasswordTextView passwordTextView;
    private TextView lengthTextView;
    private Switch lower;
    private Switch upper;
    private Switch digit;
    private Switch special;
    private ImageView decrease;
    private ImageView increase;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        init();
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
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        history = new ArrayList<>();
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        passwordTextView = findViewById(R.id.password);
        lengthTextView = findViewById(R.id.length);
        lengthTextView.setOnClickListener(v -> lengthDialog());
        findViewById(R.id.new_password).setOnClickListener(v -> reload());
        findViewById(R.id.new_password).setOnLongClickListener(v -> {
            new Thread(() -> {
                Runnable runnable = this::reload;
                while (v.isPressed()) {
                    runOnUiThread(runnable);
                    try { Thread.sleep(50); }
                    catch (InterruptedException e) { Log.w("PasswordGenerator", "Interrupted thread"); }
                }
            }).start();
            return true;
        });
        findViewById(R.id.copy).setOnClickListener(v -> copy());
        findViewById(R.id.share).setOnClickListener(v -> share());
        findViewById(R.id.qrcode).setOnClickListener(v -> qrcode());
        lower = findViewById(R.id.lower);
        upper = findViewById(R.id.upper);
        digit = findViewById(R.id.digit);
        special = findViewById(R.id.special);
        lower.setOnCheckedChangeListener(this);
        upper.setOnCheckedChangeListener(this);
        digit.setOnCheckedChangeListener(this);
        special.setOnCheckedChangeListener(this);
        decrease = findViewById(R.id.decrease);
        increase = findViewById(R.id.increase);
        decrease.setOnClickListener(v -> decreaseLength());
        increase.setOnClickListener(v -> increaseLength());
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        currentLength = 8;
        symbols();
        update();
        newRandom();
    }

    private void newRandom() {
        random = new RandomString(currentLength, currentSymbols);
        reload();
    }

    private void reload() {
        currentPassword = random.nextString();
        passwordTextView.setText(currentPassword);
        history.add(currentPassword);
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

    private void decreaseLength() {
        if (currentLength != 8) currentLength--;
        update();
        newRandom();
    }

    private void increaseLength() {
        if (currentLength != 64) currentLength++;
        update();
        newRandom();
    }

    private void update() {
        decrease.setClickable(currentLength != 8);
        increase.setClickable(currentLength != 64);
        lengthTextView.setText(String.valueOf(currentLength));
        seekBar.setProgress(currentLength - 8);
    }

    private void symbols() {
        lower.setClickable(!(lower.isChecked() && !upper.isChecked() && !digit.isChecked() && !special.isChecked()));
        upper.setClickable(!(upper.isChecked() && !lower.isChecked() && !digit.isChecked() && !special.isChecked()));
        digit.setClickable(!(digit.isChecked() && !upper.isChecked() && !lower.isChecked() && !special.isChecked()));
        special.setClickable(!(special.isChecked() && !upper.isChecked() && !digit.isChecked() && !lower.isChecked()));
        StringBuilder sb = new StringBuilder();
        if (lower.isChecked()) sb.append(RandomString.LOWER_CASE_LETTERS);
        if (upper.isChecked()) sb.append(RandomString.UPPER_CASE_LETTERS);
        if (digit.isChecked()) sb.append(RandomString.DIGITS);
        if (special.isChecked()) sb.append(RandomString.SPECIAL_CHARACTERS);
        currentSymbols = sb.toString();
    }

    private void lengthDialog() {
        DialogFragment newFragment = new LengthDialog(currentLength, this);
        newFragment.show(getSupportFragmentManager(), "length");
    }

    private void history() {
        String extra = serialize(history);
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, extra);
        startActivityForResult(intent, 0);
    }

    private void bulk() {
        DialogFragment newFragment = new BulkDialog(this);
        newFragment.show(getSupportFragmentManager(), "bulk");
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
                    if (ACTION_CLEAR_HISTORY.equals(data.getAction())) clearHistory();
                    else if (ACTION_REMOVE_FROM_HISTORY.equals(data.getAction())) history.removeAll(deserialize(data.getStringExtra(Intent.EXTRA_TEXT)));
                }
            }
        }
    }

    private void clearHistory() {
        history.clear();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            currentLength = progress + 8;
            update();
            newRandom();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        symbols();
        newRandom();
    }

    @Override
    public void onLengthDialogPositiveClick(int length) {
        currentLength = length;
        update();
        newRandom();
    }

    @Override
    public void onBulkDialogPositiveClick(int size) {
        new Thread(() -> {
            ArrayList<String> passwords = new ArrayList<>(size);
            for (int i = 0; i < size; i++) passwords.add(random.nextString());
            String extra = serialize(passwords);
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, extra);
            startActivity(intent);
        }).start();
    }
}

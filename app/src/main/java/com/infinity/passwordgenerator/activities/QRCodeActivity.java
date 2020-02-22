package com.infinity.passwordgenerator.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.infinity.passwordgenerator.views.PasswordTextView;
import com.infinity.passwordgenerator.R;

import java.util.EnumMap;
import java.util.Map;

public class QRCodeActivity extends AppCompatActivity {

    private String currentPassword;

    private ClipboardManager clipboard;

    ImageView qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.qrcode);
        }

        Intent intent = getIntent();
        currentPassword = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (currentPassword == null) finish();

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        PasswordTextView passwordTextView = findViewById(R.id.password);
        passwordTextView.setText(currentPassword);
        qrcode = findViewById(R.id.qrcode);
        findViewById(R.id.copy).setOnClickListener(v -> copy());
        findViewById(R.id.share).setOnClickListener(v -> share());

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        int width = point.x - (144 * (int) density);

        try { qrcode.setImageBitmap(qrcode(width)); }
        catch (WriterException e) { e.printStackTrace(); }

    }

    private Bitmap qrcode(int dimension) throws WriterException {
        Map<EncodeHintType, Object> hints;
        hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(currentPassword, BarcodeFormat.QR_CODE, dimension, dimension, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++)
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) finish();
        else return super.onOptionsItemSelected(item);
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

}

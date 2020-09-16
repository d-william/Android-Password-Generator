package com.dwilliam.passwordgenerator.utils;

import android.content.Context;

import com.dwilliam.passwordgenerator.models.CustomSymbols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public final class FileUtils {

    private FileUtils() {}

    public static File getSymbolsFolder(Context context) {
        File folder = new File(context.getFilesDir(), "symbols");
        folder.mkdirs();
        return folder;
    }

    public static void createSymbolsFile(File folder, String name, String symbols) {
        File file = new File(folder, name);
        try {
            file.createNewFile();
            PrintStream out = new PrintStream(new FileOutputStream(file));
            out.print(symbols);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteSymbols(File folder, String name) {
        File oldFile = new File(folder, name);
        oldFile.delete();
    }

    public static CustomSymbols getSymbols(File file) {
        try {
            BufferedReader buff = new BufferedReader(new FileReader(file));
            return new CustomSymbols(file.getName(), buff.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

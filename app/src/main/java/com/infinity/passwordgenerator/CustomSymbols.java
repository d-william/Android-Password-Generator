package com.infinity.passwordgenerator;

import com.infinity.utils.RandomString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CustomSymbols implements Serializable {

    private String name;
    private String symbols;

    public CustomSymbols(String name, String symbols) {
        this.name = name;
        this.symbols = symbols;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String symbols() {
        return symbols;
    }

    public void symbols(String symbols) {
        this.symbols = symbols;
    }

    public static List<CustomSymbols> commons() {
        List<CustomSymbols> res = new ArrayList<>();
        res.add(new CustomSymbols("Lower letters", RandomString.LOWER_CASE_LETTERS));
        res.add(new CustomSymbols("Upper letters", RandomString.UPPER_CASE_LETTERS));
        res.add(new CustomSymbols("Lower & Upper", RandomString.LOWER_CASE_LETTERS + RandomString.UPPER_CASE_LETTERS));
        res.add(new CustomSymbols("Digits", RandomString.DIGITS));
        res.add(new CustomSymbols("Lower & Upper & Digits", RandomString.LOWER_CASE_LETTERS + RandomString.UPPER_CASE_LETTERS + RandomString.DIGITS));
        res.add(new CustomSymbols("Special characters", RandomString.SPECIAL_CHARACTERS));
        return res;
    }

}

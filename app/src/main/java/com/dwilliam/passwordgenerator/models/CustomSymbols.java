package com.dwilliam.passwordgenerator.models;

import com.dwilliam.utils.strings.RandomString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomSymbols implements Serializable {

    private final String name;
    private final String symbols;

    public CustomSymbols(String name, String symbols) {
        this.name = name;
        this.symbols = symbols;
    }

    public String name() {
        return name;
    }

    public String symbols() {
        return symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomSymbols symbols1 = (CustomSymbols) o;
        return Objects.equals(name, symbols1.name) &&
                Objects.equals(symbols, symbols1.symbols);
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

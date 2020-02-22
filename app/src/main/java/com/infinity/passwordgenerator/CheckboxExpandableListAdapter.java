package com.infinity.passwordgenerator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.infinity.utils.RandomString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckboxExpandableListAdapter extends SimpleExpandableListAdapter {

    private static final String NAME = "NAME";

    private static final String[] group = {"Lower letters" , "Upper letters", "Digits", "Special characters"};
    private static final String[] child = { RandomString.LOWER_CASE_LETTERS, RandomString.UPPER_CASE_LETTERS, RandomString.DIGITS, RandomString.SPECIAL_CHARACTERS };

    private static final List<Map<String, String>> GROUP_DATA;
    private static final List<List<Map<String, String>>> CHILD_DATA;

    static {
        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData = new ArrayList<>();
        for (int i = 0; i < group.length; i++) {
            Map<String, String> curGroupMap = new HashMap<>();
            groupData.add(curGroupMap);
            curGroupMap.put(NAME, group[i]);

            List<Map<String, String>> children = new ArrayList<>();
            for (int j = 0; j < child[i].length(); j++) {
                Map<String, String> curChildMap = new HashMap<>();
                children.add(curChildMap);
                curChildMap.put(NAME, String.valueOf(child[i].charAt(j)));
            }
            childData.add(children);
        }

        GROUP_DATA = Collections.unmodifiableList(groupData);
        CHILD_DATA = Collections.unmodifiableList(childData);
    }

    private boolean[] checks = Collections.nCopies(26 + 26 + 10, false).toArray(new Boolean[1]);

    public CheckboxExpandableListAdapter(Context context) {
        super(context, GROUP_DATA, R.layout.exp_list_group_item, new String[] { NAME }, new int[] { R.id.text1 }, CHILD_DATA, R.layout.exp_list_group_item, new String[] { NAME }, new int[] { R.id.text1 });
    }

    public void setSymbols(String symbols) {

    }

    public String getSymbols() {

    }

}

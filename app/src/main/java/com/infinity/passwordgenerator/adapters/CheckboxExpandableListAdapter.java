package com.infinity.passwordgenerator.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleExpandableListAdapter;

import com.infinity.passwordgenerator.R;
import com.infinity.utils.RandomString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckboxExpandableListAdapter extends SimpleExpandableListAdapter {

    public static final String ALL = RandomString.LOWER_CASE_LETTERS + RandomString.UPPER_CASE_LETTERS + RandomString.DIGITS + RandomString.SPECIAL_CHARACTERS;

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

    public interface Listener {
        void onSymbolsChange();
    }

    private boolean[] checks_lower = new boolean[RandomString.LOWER_CASE_LETTERS.length()];
    private boolean[] checks_upper = new boolean[RandomString.UPPER_CASE_LETTERS.length()];
    private boolean[] checks_digit = new boolean[RandomString.DIGITS.length()];
    private boolean[] checks_special = new boolean[RandomString.SPECIAL_CHARACTERS.length()];

    private Listener listener;

    public CheckboxExpandableListAdapter(Context context) {
        super(context, GROUP_DATA, R.layout.exp_list_group_item, new String[] { NAME }, new int[] { R.id.text1 }, CHILD_DATA, R.layout.exp_list_group_item, new String[] { NAME }, new int[] { R.id.text1 });
        Arrays.fill(checks_lower, true);
        Arrays.fill(checks_upper, true);
        Arrays.fill(checks_digit, true);
        Arrays.fill(checks_special, true);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View groupView = super.getGroupView(groupPosition, isExpanded, convertView, parent);
        CheckBox cb = groupView.findViewById(R.id.checkBox);
        cb.setOnCheckedChangeListener(null);
        if (groupPosition == 0) {
            cb.setChecked(allTrue(checks_lower));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Arrays.fill(checks_lower, isChecked);
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        else if (groupPosition == 1) {
            cb.setChecked(allTrue(checks_upper));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Arrays.fill(checks_upper, isChecked);
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        else if (groupPosition == 2) {
            cb.setChecked(allTrue(checks_digit));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Arrays.fill(checks_digit, isChecked);
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        else if (groupPosition == 3) {
            cb.setChecked(allTrue(checks_special));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Arrays.fill(checks_special, isChecked);
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        return groupView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View childView = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
        CheckBox cb = childView.findViewById(R.id.checkBox);
        if (groupPosition == 0) {
            cb.setChecked(checks_lower[childPosition]);
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checks_lower[childPosition] = isChecked;
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        else if (groupPosition == 1) {
            cb.setChecked(checks_upper[childPosition]);
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checks_upper[childPosition] = isChecked;
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        else if (groupPosition == 2) {
            cb.setChecked(checks_digit[childPosition]);
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checks_digit[childPosition] = isChecked;
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        else if (groupPosition == 3) {
            cb.setChecked(checks_special[childPosition]);
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                checks_special[childPosition] = isChecked;
                notifyDataSetChanged();
                if (listener != null) listener.onSymbolsChange();
            });
        }
        return childView;
    }

    public String getSymbols() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < checks_lower.length; i++)
            if (checks_lower[i]) sb.append(RandomString.LOWER_CASE_LETTERS.charAt(i));
        for (int i = 0; i < checks_upper.length; i++)
            if (checks_upper[i]) sb.append(RandomString.UPPER_CASE_LETTERS.charAt(i));
        for (int i = 0; i < checks_digit.length; i++)
            if (checks_digit[i]) sb.append(RandomString.DIGITS.charAt(i));
        for (int i = 0; i < checks_special.length; i++)
            if (checks_special[i]) sb.append(RandomString.SPECIAL_CHARACTERS.charAt(i));
        return sb.toString();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    private static boolean allTrue(boolean[] array) {
        for (boolean b : array) if(!b) return false;
        return true;
    }

}

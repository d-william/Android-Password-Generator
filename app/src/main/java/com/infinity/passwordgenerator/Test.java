package com.infinity.passwordgenerator;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleExpandableListAdapter;

import com.infinity.utils.RandomString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test extends SimpleExpandableListAdapter {

    public Test(Context context, List<? extends Map<String, ?>> groupData, int groupLayout, String[] groupFrom, int[] groupTo, List<? extends List<? extends Map<String, ?>>> childData, int childLayout, String[] childFrom, int[] childTo) {
        super(context, groupData, groupLayout, groupFrom, groupTo, childData, childLayout, childFrom, childTo);
    }
}

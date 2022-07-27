package com.rockstargames.hal;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class andDropDownList extends andView {
    static int staticCount;
    int selectedItem = -1;
    List<listData> listElements = new ArrayList();

    /* loaded from: classes.dex */
    public class listData {
        String data;
        String label;

        public listData() {
        }

        public String toString() {
            return this.label;
        }
    }

    public andDropDownList(int i) {
        super(i);
        setView(new andDropDownListImpl());
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andDropDownList createView(int i) {
        return new andDropDownList(i);
    }

    public int getWidth() {
        return getContainer().getWidth();
    }

    public int getHeight() {
        return getContainer().getHeight();
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        super.setBounds(f, f2, f3, f4, f5, f6);
    }

    protected Spinner GetSpinner() {
        return (Spinner) this.view;
    }

    public void AddListItem(String str, String str2) {
        listData listdata = new listData();
        listdata.label = str;
        listdata.data = str2;
        this.listElements.add(listdata);
    }

    public void BuildList() {
        Spinner GetSpinner = GetSpinner();
        ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityWrapper.getActivity(), 17367048, this.listElements);
        arrayAdapter.setDropDownViewResource(17367049);
        GetSpinner.setAdapter((SpinnerAdapter) arrayAdapter);
        GetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.rockstargames.hal.andDropDownList.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                andDropDownList.this.selectedItem = i;
            }
        });
    }

    public String GetSelectedItemData() {
        int i = this.selectedItem;
        if (i != -1) {
            String str = this.listElements.get(i).data;
            Log.i("data", "data code: " + str);
            return str;
        }
        return null;
    }

    /* loaded from: classes.dex */
    protected class andDropDownListImpl extends Spinner {
        public andDropDownListImpl() {
            super(ActivityWrapper.getActivity());
            setId(andViewManager.genID());
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            andDropDownList anddropdownlist = andDropDownList.this;
            anddropdownlist.onAttachedToWindow(anddropdownlist.getHandle());
        }
    }
}

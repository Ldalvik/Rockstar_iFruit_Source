package com.rockstargames.hal;

import android.database.DataSetObserver;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/* loaded from: classes.dex */
public class andTable extends andView {
    static int staticCount;
    protected int count = 0;

    public native andView getCell(int i, int i2, int i3);

    public andTable(int i) {
        super(i);
        setView(new andTableImpl());
        staticCount++;
    }

    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andTable createView(int i) {
        return new andTable(i);
    }

    protected andTableImpl getTable() {
        return (andTableImpl) this.view;
    }

    public void setCellCount(int i) {
        this.count = i;
        getTable().notifyObservers();
        getTable().invalidate();
    }

    /* loaded from: classes.dex */
    public class andTableImpl extends ListView implements ListAdapter {
        private HashMap<View, andView> nativeToHalMap = new HashMap<>();
        private HashSet<DataSetObserver> observers = new HashSet<>();

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return true;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return null;
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int i) {
            return true;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andTableImpl() {
            super(ActivityWrapper.getActivity());
            andTable.this = r2;
            setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
            setAdapter((ListAdapter) this);
        }

        @Override // android.widget.AdapterView, android.widget.Adapter
        @ViewDebug.CapturedViewProperty
        public int getCount() {
            return andTable.this.count;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            andView andview = view != null ? this.nativeToHalMap.get(view) : null;
            andTable andtable = andTable.this;
            andView cell = andtable.getCell(andtable.handle, i, andview == null ? 0 : andview.getHandle());
            if (cell == null) {
                Log.e("andTable", "*** *** Returned cell was null! *** ***");
            }
            if (andview != null && cell != andview) {
                this.nativeToHalMap.remove(view);
            } else {
                this.nativeToHalMap.put(cell.getOuterView(), cell);
            }
            return cell.getOuterView();
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            this.observers.add(dataSetObserver);
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            this.observers.remove(dataSetObserver);
        }

        public void notifyObservers() {
            Iterator it = new HashSet(this.observers).iterator();
            while (it.hasNext()) {
                ((DataSetObserver) it.next()).onChanged();
            }
        }
    }
}

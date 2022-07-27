package com.rockstargames.hal;

import android.app.Activity;
import android.graphics.Color;
import android.support.p000v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.rockstargames.ifruit.C0532R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class andColourPicker extends andView {
    static int staticCount;
    private andColourPickerImpl picker;
    private int selectedPlatform = -1;
    private boolean isMultiplayer = false;
    private float priceMod = 1.0f;
    private List<SelectorGroup> dataSource = new ArrayList();
    private int colourIndex = 0;
    private String LockedString = "LOCKED";
    private String FittedString = "FITTED";
    private String NoneString = "NONE";

    public native void onChildClick(int i, int i2, int i3, int i4, int i5, int i6);

    public native void onTryLocked(int i);

    static /* synthetic */ int access$908(andColourPicker andcolourpicker) {
        int i = andcolourpicker.colourIndex;
        andcolourpicker.colourIndex = i + 1;
        return i;
    }

    static /* synthetic */ int access$910(andColourPicker andcolourpicker) {
        int i = andcolourpicker.colourIndex;
        andcolourpicker.colourIndex = i - 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SelectorItem {
        public int Colour;
        public int Cost;
        public int CostMP;
        public boolean Fitted;
        public int Index;
        public boolean Locked;
        public String Name;
        public boolean Selected;

        private SelectorItem() {
            this.Locked = false;
            this.Selected = false;
            this.Fitted = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class SelectorGroup extends ArrayList<SelectorItem> {
        public String DisplayName;
        public String GroupName;
        public boolean Locked;

        private SelectorGroup() {
            this.Locked = false;
        }
    }

    public andColourPicker(int i) {
        super(i);
        this.picker = null;
        this.picker = new andColourPickerImpl(this, this.dataSource);
        this.picker.setAdapter(new expandableListAdapter(ActivityWrapper.getActivity(), this.dataSource));
        this.picker.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // from class: com.rockstargames.hal.andColourPicker.1
            @Override // android.widget.ExpandableListView.OnGroupClickListener
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i2, long j) {
                return true;
            }
        });
        this.picker.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // from class: com.rockstargames.hal.andColourPicker.2
            @Override // android.widget.ExpandableListView.OnChildClickListener
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i2, int i3, long j) {
                ((andColourPickerImpl) expandableListView).onChildClick(i2, i3);
                return false;
            }
        });
        setView(this.picker);
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andColourPicker createView(int i) {
        return new andColourPicker(i);
    }

    public void SetLocalisedStrings(String str, String str2, String str3) {
        this.LockedString = str;
        this.FittedString = str2;
        this.NoneString = str3;
    }

    public void AddGroup(String str, String str2) {
        SelectorGroup selectorGroup = new SelectorGroup();
        selectorGroup.GroupName = str;
        selectorGroup.DisplayName = str2;
        this.dataSource.add(selectorGroup);
    }

    public void AddItem(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6) {
        this.picker.AddItem(str, str2, i, i2, i3, i4, i5, i6);
    }

    public void SetFittedItem(int i, int i2) {
        this.picker.SetFittedItem(i, i2);
    }

    public void SetSelectedItem(int i, int i2, boolean z, int i3, float f) {
        this.picker.SetSelectedItem(i, i2);
        this.isMultiplayer = z;
        this.selectedPlatform = i3;
        this.priceMod = f;
    }

    public boolean FindItem(String str) {
        return this.picker.FindItem(str);
    }

    public void RemoveItem(String str) {
        this.picker.RemoveItem(str);
    }

    public void ResetAllToUnLocked() {
        for (int i = 0; i < this.dataSource.size(); i++) {
            SelectorGroup selectorGroup = this.dataSource.get(i);
            selectorGroup.Locked = false;
            for (int i2 = 0; i2 < selectorGroup.size(); i2++) {
                selectorGroup.get(i2).Locked = false;
            }
        }
    }

    public void SetItemToLocked(int i) {
        int i2 = 0;
        SelectorGroup selectorGroup = this.dataSource.get(0);
        while (true) {
            SelectorGroup selectorGroup2 = selectorGroup;
            if (i >= selectorGroup2.size()) {
                i -= selectorGroup2.size();
                i2++;
                selectorGroup = this.dataSource.get(i2);
            } else {
                selectorGroup2.get(i).Locked = true;
                return;
            }
        }
    }

    /* loaded from: classes.dex */
    protected class andColourPickerImpl extends ExpandableListView {
        private List<SelectorGroup> dataSource;
        private andColourPicker parentPicker;
        private int previousSelectedGroup = -1;
        private int previousSelectedColour = -1;
        private int previousFittedGroup = -1;
        private int previousFittedColour = -1;

        public andColourPickerImpl(andColourPicker andcolourpicker, List<SelectorGroup> list) {
            super(ActivityWrapper.getActivity());
            this.parentPicker = null;
            this.dataSource = new ArrayList();
            this.parentPicker = andcolourpicker;
            this.dataSource = list;
            setChoiceMode(1);
            setGroupIndicator(null);
            setSoundEffectsEnabled(false);
        }

        public void onChildClick(int i, int i2) {
            try {
                SelectorItem selectorItem = this.dataSource.get(i).get(i2);
                if (selectorItem.Locked) {
                    this.parentPicker.onTryLocked(this.parentPicker.handle);
                } else {
                    this.parentPicker.onChildClick(this.parentPicker.handle, Color.red(selectorItem.Colour), Color.green(selectorItem.Colour), Color.blue(selectorItem.Colour), Color.alpha(selectorItem.Colour), selectorItem.Index);
                }
            } catch (Exception e) {
                ActivityWrapper.logError("andColourPicker", "Exception in onChildClick " + i + " " + i2 + "\n" + ReportPickerStatus(), e);
            }
        }

        public void AddItem(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6) {
            ((expandableListAdapter) andColourPicker.this.picker.getExpandableListAdapter()).AddItem(str, str2, i, i2, i3, i4, i5, i6);
        }

        public void RemoveItem(String str) {
            for (int i = 0; i < this.dataSource.size(); i++) {
                SelectorGroup selectorGroup = this.dataSource.get(i);
                int size = selectorGroup.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (selectorGroup.get(i2).Name.equals(str)) {
                        ((expandableListAdapter) andColourPicker.this.picker.getExpandableListAdapter()).removeItem(i, i2);
                        return;
                    }
                }
            }
        }

        public boolean FindItem(String str) {
            for (int i = 0; i < this.dataSource.size(); i++) {
                SelectorGroup selectorGroup = this.dataSource.get(i);
                int size = selectorGroup.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (selectorGroup.get(i2).Name.equals(str)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void SetSelectedItem(int i, int i2) {
            try {
                if (this.previousSelectedColour >= 0) {
                    this.dataSource.get(this.previousSelectedGroup).get(this.previousSelectedColour).Selected = false;
                }
            } catch (Exception e) {
                ActivityWrapper.logError("andColourPicker", "Exception resetting selected item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e);
            }
            try {
                this.dataSource.get(i).get(i2).Selected = true;
                this.previousSelectedGroup = i;
                this.previousSelectedColour = i2;
                invalidateViews();
            } catch (Exception e2) {
                ActivityWrapper.logError("andColourPicker", "Exception setting selected item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e2);
            }
        }

        public void SetFittedItem(int i, int i2) {
            try {
                if (this.previousFittedColour >= 0) {
                    this.dataSource.get(this.previousFittedGroup).get(this.previousFittedColour).Fitted = false;
                }
            } catch (Exception e) {
                ActivityWrapper.logError("andColourPicker", "Exception resetting fitted item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e);
            }
            try {
                this.dataSource.get(i).get(i2).Fitted = true;
                this.previousFittedGroup = i;
                this.previousFittedColour = i2;
            } catch (Exception e2) {
                ActivityWrapper.logError("andColourPicker", "Exception setting fitted item " + this.previousFittedGroup + " " + this.previousFittedColour + "\n" + ReportPickerStatus(), e2);
            }
        }

        private String ReportPickerStatus() {
            String concat = "ColourPicker Status - \n".concat("dataSource.size() == " + this.dataSource.size() + "\n");
            for (int i = 0; i < this.dataSource.size(); i++) {
                concat = concat.concat("group " + i + " size() == " + this.dataSource.get(i).size() + "\n");
            }
            return concat;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class expandableListAdapter extends BaseExpandableListAdapter {
        private Activity context;
        private List<SelectorGroup> dataSource;

        @Override // android.widget.ExpandableListAdapter
        public long getChildId(int i, int i2) {
            return i2;
        }

        @Override // android.widget.ExpandableListAdapter
        public long getGroupId(int i) {
            return i;
        }

        @Override // android.widget.ExpandableListAdapter
        public boolean hasStableIds() {
            return true;
        }

        @Override // android.widget.ExpandableListAdapter
        public boolean isChildSelectable(int i, int i2) {
            return true;
        }

        public expandableListAdapter(Activity activity, List<SelectorGroup> list) {
            this.context = null;
            this.dataSource = null;
            this.context = activity;
            this.dataSource = list;
        }

        @Override // android.widget.ExpandableListAdapter
        public Object getGroup(int i) {
            return this.dataSource.get(i);
        }

        @Override // android.widget.ExpandableListAdapter
        public Object getChild(int i, int i2) {
            return this.dataSource.get(i).get(i2);
        }

        @Override // android.widget.ExpandableListAdapter
        public View getGroupView(int i, boolean z, View view, ViewGroup viewGroup) {
            SelectorGroup selectorGroup = (SelectorGroup) getGroup(i);
            if (view == null) {
                view = ((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(C0532R.layout.colourpickergroupitem, (ViewGroup) null);
            }
            TextView textView = (TextView) view.findViewById(C0532R.C0534id.colourgroup);
            textView.setTypeface(null, 1);
            textView.setText(selectorGroup.DisplayName);
            ((ExpandableListView) viewGroup).expandGroup(i);
            view.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            return view;
        }

        /* JADX WARN: Removed duplicated region for block: B:30:0x013f A[Catch: Exception -> 0x0173, TryCatch #0 {Exception -> 0x0173, blocks: (B:2:0x0000, B:4:0x000e, B:5:0x0020, B:8:0x007f, B:9:0x009e, B:10:0x00bc, B:12:0x00e0, B:13:0x00e8, B:14:0x00ef, B:16:0x00f6, B:17:0x00fa, B:18:0x00fd, B:20:0x0101, B:21:0x010a, B:23:0x0115, B:26:0x011a, B:27:0x011e, B:28:0x012a, B:30:0x013f, B:31:0x014d), top: B:36:0x0000 }] */
        /* JADX WARN: Removed duplicated region for block: B:31:0x014d A[Catch: Exception -> 0x0173, TRY_LEAVE, TryCatch #0 {Exception -> 0x0173, blocks: (B:2:0x0000, B:4:0x000e, B:5:0x0020, B:8:0x007f, B:9:0x009e, B:10:0x00bc, B:12:0x00e0, B:13:0x00e8, B:14:0x00ef, B:16:0x00f6, B:17:0x00fa, B:18:0x00fd, B:20:0x0101, B:21:0x010a, B:23:0x0115, B:26:0x011a, B:27:0x011e, B:28:0x012a, B:30:0x013f, B:31:0x014d), top: B:36:0x0000 }] */
        @Override // android.widget.ExpandableListAdapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public android.view.View getChildView(int r6, int r7, boolean r8, android.view.View r9, android.view.ViewGroup r10) {
            /*
                Method dump skipped, instructions count: 385
                To view this dump change 'Code comments level' option to 'DEBUG'
            */
            throw new UnsupportedOperationException("Method not decompiled: com.rockstargames.hal.andColourPicker.expandableListAdapter.getChildView(int, int, boolean, android.view.View, android.view.ViewGroup):android.view.View");
        }

        @Override // android.widget.ExpandableListAdapter
        public int getGroupCount() {
            return this.dataSource.size();
        }

        @Override // android.widget.ExpandableListAdapter
        public int getChildrenCount(int i) {
            return this.dataSource.get(i).size();
        }

        public void AddItem(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6) {
            for (int i7 = 0; i7 < this.dataSource.size(); i7++) {
                SelectorGroup selectorGroup = this.dataSource.get(i7);
                if (selectorGroup.GroupName.equals(str)) {
                    SelectorItem selectorItem = new SelectorItem();
                    selectorItem.Name = str2;
                    selectorItem.Cost = i;
                    selectorItem.CostMP = i2;
                    selectorItem.Colour = Color.argb(i6, i3, i4, i5);
                    selectorItem.Index = andColourPicker.access$908(andColourPicker.this);
                    selectorGroup.add(selectorItem);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        public boolean removeItem(int i, int i2) {
            if (i >= this.dataSource.size() || i2 >= this.dataSource.get(i).size()) {
                return false;
            }
            this.dataSource.get(i).remove(i2);
            andColourPicker.access$910(andColourPicker.this);
            notifyDataSetChanged();
            return true;
        }
    }
}

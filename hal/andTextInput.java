package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class andTextInput extends andLabel {
    static int staticCount;
    Bitmap bitmap;
    boolean setProgrammatically = false;
    private boolean isPasswordInput = false;
    private boolean clearOnFocus = false;
    private ArrayList<InputFilter> filters = new ArrayList<>();

    /* loaded from: classes.dex */
    enum TextAlignment {
        TEXT_ALIGNMENT_CENTER,
        TEXT_ALIGNMENT_LEFT,
        TEXT_ALIGNMENT_RIGHT,
        TEXT_ALIGNMENT_JUSTIFY,
        TEXT_ALIGNMENT_CENTER_BOTH
    }

    public native void onEnterKey(int i, String str);

    public native void onFocusGained(int i, String str);

    public native void onFocusLost(int i, String str);

    public native void textChanged(int i, String str);

    public andTextInput(int i) {
        super(i, false);
        setView(new andTextInputImpl());
        getView().setFocusable(true);
        setRemoveContextMenuFromInput();
        staticCount++;
    }

    public void setNextFocusView(andView andview) {
        int id = andview.getView().getId();
        getView().setNextFocusLeftId(id);
        getView().setNextFocusUpId(id);
        getView().setNextFocusDownId(id);
        getView().setNextFocusRightId(id);
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    @Override // com.rockstargames.hal.andLabel
    public void setText(String str) {
        this.setProgrammatically = true;
        super.setText(str);
    }

    @Override // com.rockstargames.hal.andLabel
    public void setTextSize(float f) {
        super.setTextSize(f);
        ((andTextInputImpl) this.view).setSingleLine(true);
        if (this.isPasswordInput) {
            ((andTextInputImpl) this.view).setInputType(129);
            ((andTextInputImpl) this.view).setTypeface(Typeface.DEFAULT);
        }
    }

    @Override // com.rockstargames.hal.andLabel
    public void setBackgroundTransparent() {
        ((andTextInputImpl) this.view).setBackgroundColor(0);
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void setBounds(float f, float f2, float f3, float f4, float f5, float f6) {
        super.setBounds(f, f2, f3, f4, 0.0f, 0.0f);
    }

    public static andTextInput createView(int i) {
        return new andTextInput(i);
    }

    public void setEmail(boolean z) {
        this.setProgrammatically = true;
        ((andTextInputImpl) this.view).setInputType(524321);
    }

    public void setPassword(boolean z) {
        this.setProgrammatically = true;
        this.isPasswordInput = true;
        ((andTextInputImpl) this.view).setInputType(129);
        ((andTextInputImpl) this.view).setTypeface(Typeface.DEFAULT);
    }

    public void setNumberInput() {
        ((andTextInputImpl) this.view).setInputType(2);
    }

    public void setLicensePlateInput() {
        InputFilter inputFilter = new InputFilter() { // from class: com.rockstargames.hal.andTextInput.1
            @Override // android.text.InputFilter
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                if (charSequence instanceof SpannableStringBuilder) {
                    SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
                    for (int i5 = i2 - 1; i5 >= i; i5--) {
                        char charAt = charSequence.charAt(i5);
                        if ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && (charAt < '0' || charAt > '9'))) {
                            spannableStringBuilder.delete(i5, i5 + 1);
                        }
                    }
                    for (CharacterStyle characterStyle : (CharacterStyle[]) spannableStringBuilder.getSpans(i, i2, CharacterStyle.class)) {
                        spannableStringBuilder.removeSpan(characterStyle);
                    }
                    return charSequence;
                }
                StringBuilder sb = new StringBuilder();
                boolean z = true;
                while (i < i2) {
                    char charAt2 = charSequence.charAt(i);
                    if ((charAt2 < 'a' || charAt2 > 'z') && ((charAt2 < 'A' || charAt2 > 'Z') && (charAt2 < '0' || charAt2 > '9'))) {
                        z = false;
                    } else {
                        sb.append(charAt2);
                    }
                    i++;
                }
                if (z) {
                    return null;
                }
                return (z || sb.length() != 0) ? sb.toString() : "";
            }
        };
        InputFilter.LengthFilter lengthFilter = new InputFilter.LengthFilter(8);
        this.filters.add(inputFilter);
        this.filters.add(lengthFilter);
        ((andTextInputImpl) this.view).updateFilters();
        ((andTextInputImpl) this.view).setInputType(528385);
    }

    private void setRemoveContextMenuFromInput() {
        ((andTextInputImpl) this.view).setLongClickable(false);
        ((andTextInputImpl) this.view).setTextIsSelectable(false);
        if (Build.VERSION.SDK_INT < 11) {
            ((andTextInputImpl) this.view).setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() { // from class: com.rockstargames.hal.andTextInput.2
                @Override // android.view.View.OnCreateContextMenuListener
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.clear();
                }
            });
        } else {
            ((andTextInputImpl) this.view).setCustomSelectionActionModeCallback(new ActionMode.Callback() { // from class: com.rockstargames.hal.andTextInput.3
                @Override // android.view.ActionMode.Callback
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    return false;
                }

                @Override // android.view.ActionMode.Callback
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override // android.view.ActionMode.Callback
                public void onDestroyActionMode(ActionMode actionMode) {
                }

                @Override // android.view.ActionMode.Callback
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }
            });
        }
    }

    public void setWatermarkText(String str) {
        ((andTextInputImpl) this.view).setHint(str);
    }

    public void setBackgroundImage(andImage andimage) {
        ((andTextInputImpl) this.view).setPackedImage(andimage.getPackedImage());
    }

    public void setMaxLength(int i) {
        ((andTextInputImpl) this.view).setMaxLength(i);
    }

    public void setClearOnFocus(boolean z) {
        this.clearOnFocus = z;
    }

    /* renamed from: com.rockstargames.hal.andTextInput$4 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C05204 {
        static final /* synthetic */ int[] $SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment;

        static {
            int[] iArr = new int[TextAlignment.values().length];
            $SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment = iArr;
            try {
                iArr[TextAlignment.TEXT_ALIGNMENT_CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment[TextAlignment.TEXT_ALIGNMENT_LEFT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment[TextAlignment.TEXT_ALIGNMENT_RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment[TextAlignment.TEXT_ALIGNMENT_JUSTIFY.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment[TextAlignment.TEXT_ALIGNMENT_CENTER_BOTH.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    @Override // com.rockstargames.hal.andLabel
    public void setTextAlignment(int i) {
        int i2 = C05204.$SwitchMap$com$rockstargames$hal$andTextInput$TextAlignment[TextAlignment.values()[i].ordinal()];
        if (i2 == 1) {
            ((andTextInputImpl) this.view).setGravity(1);
        } else if (i2 == 2) {
            ((andTextInputImpl) this.view).setGravity(3);
        } else if (i2 == 3) {
            ((andTextInputImpl) this.view).setGravity(5);
        } else if (i2 == 4) {
            Log.e("andTextInput", "WARNING: Justified alignment not implemented yet.");
        } else if (i2 != 5) {
        } else {
            ((andTextInputImpl) this.view).setGravity(17);
        }
    }

    /* loaded from: classes.dex */
    public class andTextInputImpl extends EditText {
        PackedImage packedImage = null;
        boolean attached = false;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public andTextInputImpl() {
            super(ActivityWrapper.getActivity());
            andTextInput.this = r2;
            setId(andViewManager.genID());
            setOnKeyListener(new View.OnKeyListener() { // from class: com.rockstargames.hal.andTextInput.andTextInputImpl.1
                @Override // android.view.View.OnKeyListener
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == 0 && i == 66) {
                        andTextInput.this.onEnterKey(andTextInput.this.handle, andTextInputImpl.this.getText().toString());
                        TextView textView = (TextView) view;
                        textView.clearComposingText();
                        textView.setCursorVisible(false);
                        return true;
                    }
                    return false;
                }
            });
            setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: com.rockstargames.hal.andTextInput.andTextInputImpl.2
                @Override // android.view.View.OnFocusChangeListener
                public void onFocusChange(View view, boolean z) {
                    ((TextView) view).setCursorVisible(z);
                }
            });
            setOnTouchListener(new View.OnTouchListener() { // from class: com.rockstargames.hal.andTextInput.andTextInputImpl.3
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == 0) {
                        if (andTextInput.this.clearOnFocus) {
                            andTextInputImpl.this.setText("");
                        }
                        andTextInput.this.onFocusGained(andTextInput.this.handle, andTextInputImpl.this.getText().toString());
                    } else if (motionEvent.getAction() == 1) {
                        andTextInput.this.onFocusGained(andTextInput.this.handle, andTextInputImpl.this.getText().toString());
                    }
                    ((TextView) view).setCursorVisible(true);
                    return false;
                }
            });
            addTextChangedListener(new TextWatcher() { // from class: com.rockstargames.hal.andTextInput.andTextInputImpl.4
                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    if (andTextInput.this.setProgrammatically) {
                        andTextInput.this.setProgrammatically = false;
                    } else {
                        andTextInput.this.textChanged(andTextInput.this.handle, charSequence.toString());
                    }
                }
            });
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
            if (i == 4 && keyEvent.getAction() == 1) {
                andTextInput andtextinput = andTextInput.this;
                andtextinput.onFocusLost(andtextinput.handle, getText().toString());
                ((InputMethodManager) ActivityWrapper.getActivity().getSystemService("input_method")).hideSoftInputFromWindow(getWindowToken(), 0);
                return false;
            }
            return super.onKeyPreIme(i, keyEvent);
        }

        @Override // android.widget.TextView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
        }

        public boolean isAttached() {
            return this.attached;
        }

        public void setPackedImage(PackedImage packedImage) {
            this.packedImage = packedImage;
        }

        @Override // android.widget.TextView, android.view.View
        public void onDraw(Canvas canvas) {
            PackedImage packedImage = this.packedImage;
            if (packedImage != null) {
                packedImage.Draw(canvas, getWidth(), getHeight(), false);
            }
            super.onDraw(canvas);
        }

        public void setMaxLength(int i) {
            andTextInput.this.filters.add(new InputFilter.LengthFilter(i));
            updateFilters();
        }

        public void updateFilters() {
            if (andTextInput.this.filters.size() > 0) {
                setFilters((InputFilter[]) andTextInput.this.filters.toArray(new InputFilter[andTextInput.this.filters.size()]));
            }
        }
    }
}

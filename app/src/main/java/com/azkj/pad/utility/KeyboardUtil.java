package com.azkj.pad.utility;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.azkj.pad.activity.R;

/*键盘处理类*/
@SuppressWarnings("unused")
public class KeyboardUtil {
	private Context ctx;
	private Activity act;
	private KeyboardView keyboardView;
	private Keyboard symbolskey;// 数字键盘
	private EditText ed;
	private Button btn_delete;
	
	public KeyboardUtil(Activity act, Context ctx, KeyboardView keyboardView, EditText edit, Button btn_delete) {
		this.act = act;
		this.ctx = ctx;
		this.keyboardView = keyboardView;
		this.ed = edit;
		this.btn_delete = btn_delete;
		symbolskey = new Keyboard(ctx, R.xml.symbols_plate);
		keyboardView.setKeyboard(symbolskey);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(false);
		keyboardView.setOnKeyboardActionListener(listener);
		this.btn_delete.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Editable editable = ed.getText();
				int start = ed.getSelectionStart();
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			}});
	}
	
	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Editable editable = ed.getText();
			int start = ed.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				hideKeyboard();
			}
			else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			}
			else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 数字键盘切换
				keyboardView.setKeyboard(symbolskey);
			}
			else if (primaryCode == -1) {
				editable.insert(start, "*");
			}
			else if (primaryCode == 58) {
				editable.insert(start, "#");
			}
			else if (primaryCode == 48) {
				editable.insert(start, "0");
			}
			else if (primaryCode == 49) {
				editable.insert(start, "1");
			}
			else if (primaryCode == 50) {
				editable.insert(start, "2");
			}
			else if (primaryCode == 51) {
				editable.insert(start, "3");
			}
			else if (primaryCode == 52) {
				editable.insert(start, "4");
			}
			else if (primaryCode == 53) {
				editable.insert(start, "5");
			}
			else if (primaryCode == 54) {
				editable.insert(start, "6");
			}
			else if (primaryCode == 55) {
				editable.insert(start, "7");
			}
			else if (primaryCode == 56) {
				editable.insert(start, "8");
			}
			else if (primaryCode == 57) {
				editable.insert(start, "9");
			}
			else {
				editable.insert(start, Character.toString((char) primaryCode));
			}
		}
	};
	
	public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }
    
    public void hideKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.VISIBLE) {
            keyboardView.setVisibility(View.INVISIBLE);
        }
    }
}

package com.shgr.mkeyboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

import com.shgr.mkeyboard.R;import com.shgr.mkeyboard.keyboard.MKeyboard;
import java.util.List;


public class MKeyboardView extends KeyboardView {
    public MKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getKeyboard() != null) {
            List<Keyboard.Key> keys = getKeyboard().getKeys();
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == Keyboard.KEYCODE_DONE) {

                    if (MKeyboard.Flags.isIMEActionDone()) {
                        Drawable dr = (Drawable) getContext().getResources().getDrawable(R.drawable.done_key_selector);
                        int[] drawableState = key.getCurrentDrawableState();
                        dr.setState(drawableState);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
                        Log.d("keyboard", "done");
                    } else {
                        Drawable dr = (Drawable) getContext().getResources().getDrawable(R.drawable.enter_key_selector);
                        int[] drawableState = key.getCurrentDrawableState();
                        dr.setState(drawableState);
                        dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                        dr.draw(canvas);
                        Log.d("keyboard", "enter");
                    }
                }

                if (key.codes[0] == Keyboard.KEYCODE_SHIFT && MKeyboard.Flags.isCaps()) {
                    Drawable dr = (Drawable) getContext().getResources().getDrawable(R.drawable.caps_key_selector);
                    int[] drawableState = key.getCurrentDrawableState();
                    dr.setState(drawableState);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                }
            }
        }
    }
}
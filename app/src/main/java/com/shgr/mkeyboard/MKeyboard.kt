package com.shgr.mkeyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData

class MKeyboard() : InputMethodService(), KeyboardView.OnKeyboardActionListener {

    companion object Flags {
        var isCaps = false
        var isBackClicked = MutableLiveData<Boolean>(false)
        var isIMEActionDone = false
        var isIMEActionNext = MutableLiveData<Boolean>(false)
        var currentView: View? = null
    }

    private lateinit var kv: MKeyboardView
    private lateinit var keyboard: Keyboard
    private lateinit var v: View

    private var isNumeric: Boolean = false

    override fun onCreateInputView(): View {

        val li = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = li.inflate(R.layout.m_keyboard, null)
        kv = v.findViewById<View>(R.id.keyboard) as MKeyboardView
        kv.keyboard = Keyboard(this, R.xml.numeric_layout)
        kv.isPreviewEnabled = false
        kv.setOnKeyboardActionListener(this)
        return v

    }

    override fun onStartInputView(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(attribute, restarting)

        val edittext = currentView?.findFocus() as EditText?

        if (attribute?.inputType == InputType.TYPE_CLASS_NUMBER || attribute?.inputType == InputType.TYPE_CLASS_PHONE) {
            kv.keyboard = Keyboard(this, R.xml.numeric_layout)
        } else {
            kv.keyboard = Keyboard(this, R.xml.qwerty_layout)

            if (edittext?.text?.isEmpty() == true) {
                enableCaps(true)
            } else {
                enableCaps(false)
            }
        }

        edittext?.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isEmpty()) {
                    enableCaps(true)
                }
            }
        }

        isIMEActionDone = edittext?.imeOptions == EditorInfo.IME_ACTION_DONE
    }

    override fun onPress(p0: Int) {
    }


    override fun onRelease(p0: Int) {
        if (currentView?.findFocus() != null) {
            val edittext = currentView?.findFocus() as EditText
            val editable = edittext.text
            val start = edittext.selectionStart
            when (p0) {
                Keyboard.KEYCODE_MODE_CHANGE -> {
                    keyboardLayoutHandler()
                }
                Keyboard.KEYCODE_DELETE -> {
                    if (!isBackClicked.value!!) isBackClicked.value = true
                    if (editable != null && start > 0) editable.delete(start - 1, start)
                    isBackClicked.value = false
                }
                Keyboard.KEYCODE_SHIFT -> {
                    switchCapsMode()
                }
                Keyboard.KEYCODE_DONE -> {
                    when (edittext.imeOptions) {
                        EditorInfo.IME_ACTION_DONE -> {
                            //NavigationHelper.navigate()
                        }
                        EditorInfo.IME_ACTION_NONE -> {
                            return
                        }
                        EditorInfo.IME_ACTION_NEXT -> {
                            isIMEActionNext.value = true
                            return
                        }
                        else -> {
                            edittext.onEditorAction(EditorInfo.IME_ACTION_DONE)
                        }
                    }
                }
                else -> {
                    var code: Char = p0.toChar()
                    if (Character.isLetter(code) && isCaps) {
                        code = Character.toUpperCase(code)
                        switchCapsMode()
                    }
                    editable.insert(start, code.toString())
                }
            }
        }
    }

    private fun enableCaps(isCaps: Boolean) {
        Flags.isCaps = isCaps
        kv.keyboard.isShifted = Flags.isCaps
        kv.invalidateAllKeys()
    }

    private fun switchCapsMode() {
        isCaps = !isCaps
        kv.keyboard.isShifted = isCaps
        kv.invalidateAllKeys()
    }

    private fun keyboardLayoutHandler() {
        isNumeric = !isNumeric
        if (isNumeric) {
            kv.keyboard = Keyboard(this, R.xml.numeric_layout)
        } else {
            kv.keyboard = Keyboard(this, R.xml.qwerty_layout)
        }
    }

    private fun hideKeyboard() {
        currentView?.findFocus()?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }

    override fun onKey(p0: Int, p1: IntArray?) {
    }

    override fun onText(p0: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    override fun swipeUp() {
    }
}
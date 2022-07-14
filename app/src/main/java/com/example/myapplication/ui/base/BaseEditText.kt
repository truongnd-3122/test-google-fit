package com.example.myapplication.ui.base

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import com.example.myapplication.R
import com.example.myapplication.databinding.LayoutBaseEditTextBinding

class BaseEditText(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private val binding: LayoutBaseEditTextBinding
    private var backgroundRes = 0

    init {
        val inflater = LayoutInflater.from(context)
        binding = LayoutBaseEditTextBinding.inflate(inflater)
        addView(binding.root)
        initControls(attrs)
    }

    @SuppressLint("ResourceAsColor")
    private fun initControls(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BaseEditText)
        var hint: String? = ""
        var text: String? = ""
        var inputType = EditorInfo.TYPE_CLASS_TEXT
        var textSize = 0f
        var gravity = 0
        var maxLength = 0
        if (attrs != null) {
            hint = typedArray.getString(R.styleable.BaseEditText_android_hint)
            text = typedArray.getString(R.styleable.BaseEditText_android_text)
            inputType = typedArray.getInt(
                R.styleable.BaseEditText_android_inputType,
                EditorInfo.TYPE_CLASS_TEXT
            )
            textSize = typedArray.getDimension(R.styleable.BaseEditText_android_textSize, 0f)
            gravity = typedArray.getInt(R.styleable.BaseEditText_android_gravity, 0)
            maxLength = typedArray.getInt(R.styleable.BaseEditText_android_maxLength, 0)
            backgroundRes = typedArray.getResourceId(
                R.styleable.BaseEditText_background,
                R.drawable.bg_corner_radius_edit_text
            )
        }
        with(binding){
            et.setText(text)
            et.hint = hint
            et.inputType = inputType
            viewEt.setBackgroundResource(backgroundRes)
//            et.setHintTextColor(resources.getColor(R.color.black))

            if (textSize != 0f) {
                et.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
            if (gravity != 0) {
                et.gravity = gravity
            }
            if (maxLength != 0) {
                et.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
            }




            et.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                    ivClear.visibility = (if (charSequence.isNotEmpty()) VISIBLE else INVISIBLE)

                    et.setOnFocusChangeListener { view, b ->
                        ivClear.visibility = (if (b) VISIBLE else INVISIBLE)
                    }
                }

                override fun afterTextChanged(editable: Editable) {}
            })


            ivClear.setOnClickListener {et.setText("") }

//        if (!isInEditMode()) {
//            Typeface tf = ResourcesCompat.getFont(getContext(),R.font.sf_pro_display_regular);
//            binding.et.setTypeface(tf);
//        }
        }


        typedArray.recycle()
    }

    val text: Editable
        get() = binding.et.text


    fun addTextChangedListener(textWatcher: TextWatcher?) {
        binding.et.addTextChangedListener(textWatcher)
    }

    fun requestFocus(isFocus: Boolean) {
        if (isFocus) {
            binding.et.requestFocus()
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.et, InputMethodManager.SHOW_IMPLICIT)
        } else {
            binding.et.clearFocus()
        }
    }

    fun clearFocus(isClearFocus: Boolean){
        binding.et.clearFocus()
    }
}
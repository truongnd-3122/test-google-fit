package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.databinding.LayoutBaseEditTextBinding;

public class BaseEditText extends FrameLayout {
    private LayoutBaseEditTextBinding binding;
    private int backgroundRes;

    public BaseEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        binding = LayoutBaseEditTextBinding.inflate(inflater);
        addView(binding.getRoot());
        initControls(attrs);
    }

    @SuppressLint("ResourceAsColor")
    private void initControls(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BaseEditText);
        String hint = "";
        String text = "";
        int inputType = EditorInfo.TYPE_CLASS_TEXT;
        float textSize = 0;
        int gravity = 0;
        int maxLength = 0;

        if (attrs != null) {
            hint = typedArray.getString(R.styleable.BaseEditText_android_hint);
            text = typedArray.getString(R.styleable.BaseEditText_android_text);
            inputType = typedArray.getInt(R.styleable.BaseEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
            textSize = typedArray.getDimension(R.styleable.BaseEditText_android_textSize, 0);
            gravity = typedArray.getInt(R.styleable.BaseEditText_android_gravity, 0);
            maxLength = typedArray.getInt(R.styleable.BaseEditText_android_maxLength, 0);
            backgroundRes = typedArray.getResourceId(R.styleable.BaseEditText_background, R.drawable.bg_corner_radius_edit_text);
        }

        binding.et.setText(text);
        binding.et.setHint(hint);
        binding.et.setInputType(inputType);
//        binding.et.setOnFocusChangeListener(this);
        binding.et.setHintTextColor(getResources().getColor(R.color.black));
        binding.viewEt.setBackgroundResource(backgroundRes);

        if (textSize != 0) {
            binding.et.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        if (gravity != 0) {
            binding.et.setGravity(gravity);
        }

        if (maxLength != 0) {
            binding.et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
//        if (!isInEditMode()) {
//            Typeface tf = ResourcesCompat.getFont(getContext(),R.font.sf_pro_display_regular);
//            binding.et.setTypeface(tf);
//        }

        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.ivClearEmail.setVisibility(charSequence.length() > 0 ? VISIBLE : INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.ivClearEmail.setOnClickListener(v-> {
            binding.et.setText("");
        });

        typedArray.recycle();
    }

    public Editable getText() {
        return binding.et.getText();
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        binding.et.addTextChangedListener(textWatcher);
    }

    public void requestFocus(boolean isFocus) {
        if (isFocus) {
            binding.et.requestFocus();
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(binding.et, InputMethodManager.SHOW_IMPLICIT);
        } else {
            binding.et.clearFocus();
        }
    }


}

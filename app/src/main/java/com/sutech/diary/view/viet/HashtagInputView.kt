package com.sutech.diary.view.viet

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.sutech.diary.util.closeKeyboard
import com.sutech.diary.util.showSoftKeyboard
import com.sutech.journal.diary.diarywriting.lockdiary.databinding.ItemHashtagInputBinding

class InputHashtagView : ConstraintLayout {

    private lateinit var binding: ItemHashtagInputBinding

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    interface HashtagInputViewListener {
        fun onInputEmptyHashtag(view: InputHashtagView)
    }

    private var hashtagViewInputListener: HashtagInputViewListener? = null

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ItemHashtagInputBinding.inflate(inflater, this, true)


        binding.edtHashtag.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty() || !s.startsWith("#")) {
                    s?.insert(0, "#")
                }
            }
        })

        binding.edtHashtag.setOnFocusChangeListener { view, isFocus ->
            if(!isFocus && (binding.edtHashtag.text?.length ?: 1) == 1){
                hashtagViewInputListener?.onInputEmptyHashtag(this)
                binding.edtHashtag.closeKeyboard()
            }
        }

    }

    fun setRequestHashtagInput(isRequest: Boolean) {
        if (isRequest) {
            binding.edtHashtag.requestFocus()
            binding.edtHashtag.showSoftKeyboard()
        } else {
            binding.edtHashtag.clearFocus()
        }
    }

    fun setEnableHashtagInput(isEnable: Boolean) {
        binding.edtHashtag.isEnabled = isEnable
    }

    fun setCustomViewListener(listener: HashtagInputViewListener) {
        hashtagViewInputListener = listener
    }

    fun setTextForHashtag(value: String) {
        binding.edtHashtag.setText(value)
    }

    fun getCurrentHashtagContent(): String {
        return binding.edtHashtag.text.toString()
    }

}

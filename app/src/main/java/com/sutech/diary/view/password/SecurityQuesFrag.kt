package com.sutech.diary.view.password

import android.os.Bundle
import android.provider.ContactsContract.Data
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.util.Constant
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_security_ques.edt_ans
import kotlinx.android.synthetic.main.fragment_security_ques.edt_ques
import kotlinx.android.synthetic.main.fragment_security_ques.tvCancel
import kotlinx.android.synthetic.main.fragment_security_ques.tv_confirm_security
import kotlinx.android.synthetic.main.fragment_security_ques.tv_ques_des
import kotlinx.android.synthetic.main.fragment_security_ques.tv_wrong_security_ans

class SecurityQuesFrag : BaseFragment(R.layout.fragment_security_ques) {

    companion object {
        const val TYPE_INPUT_NEW_SECURITY = 0
        const val TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD = 1
    }

    private var typeSecurityScreen = 0

    override fun initView() {
        getDataBundle()
        setUpUi()
        setClick()
    }

    private fun setUpUi() {
        edt_ques.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_wrong_security_ans.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        edt_ans.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_wrong_security_ans.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        if (typeSecurityScreen == TYPE_INPUT_NEW_SECURITY) {
            tv_ques_des.text = getString(R.string.security_question_des)
        } else {
            edt_ques.setText(DataStore.getQues())
            tv_ques_des.text = getString(R.string.enter_a_security_question)
        }
    }

    private fun getDataBundle() {
        val bundle = arguments
        if (bundle != null) {
            val type = bundle.getInt(Constant.TYPE_SECURITY, 0)
            typeSecurityScreen = type
        }
    }

    private fun setClick() {
        tvCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        tv_confirm_security.setOnClickListener {
            val ques = edt_ques.text.toString()
            val ans = edt_ans.text.toString()
            if (typeSecurityScreen == TYPE_INPUT_NEW_SECURITY) {
                if (ques.isNotBlank() && ans.isNotBlank()) {
                    DataStore.saveQues(ques)
                    DataStore.saveAns(ans)
                    val bundle = Bundle()
                    bundle.putInt(Constant.TYPE_PASSWORD, 0)
                    bundle.putBoolean(Constant.COME_FROM_SECURITY, true)
                    gotoFrag(
                        R.id.securityQuesFrag,
                        R.id.action_securityQuesFrag_to_passWordFrag,
                        bundle
                    )
                }
            } else {
                if (ans == DataStore.getAns()) {
                    val bundle = Bundle()
                    bundle.putInt(Constant.TYPE_PASSWORD, 0)
                    bundle.putBoolean(Constant.COME_FROM_SECURITY, true)
                    gotoFrag(
                        R.id.securityQuesFrag,
                        R.id.action_securityQuesFrag_to_passWordFrag,
                        bundle
                    )
                } else {
                    tv_wrong_security_ans.isVisible = true
                }
            }
        }
    }
}
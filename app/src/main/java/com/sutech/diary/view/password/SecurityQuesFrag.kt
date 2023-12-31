package com.sutech.diary.view.password

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.util.Constant
import com.sutech.diary.util.ImageUtil
import com.sutech.journal.diary.diarywriting.lockdiary.R
import kotlinx.android.synthetic.main.fragment_security_ques.edt_ans
import kotlinx.android.synthetic.main.fragment_security_ques.iv_theme
import kotlinx.android.synthetic.main.fragment_security_ques.tvCancel
import kotlinx.android.synthetic.main.fragment_security_ques.tv_confirm_security
import kotlinx.android.synthetic.main.fragment_security_ques.tv_ques
import kotlinx.android.synthetic.main.fragment_security_ques.tv_ques_des
import kotlinx.android.synthetic.main.fragment_security_ques.tv_wrong_security_ans

class SecurityQuesFrag : BaseFragment(R.layout.fragment_security_ques) {

    companion object {
        const val TYPE_INPUT_NEW_SECURITY = 0
        const val TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD = 1
    }

    private var typeSecurityScreen = 0
    private var isComeFromChangePassword = false

    override fun initView() {
        ImageUtil.setThemeForImageView(iv_theme, requireContext())
        logEvent("SecurityQuestion_Show")
        getDataBundle()
        setUpUi()
        setClick()
    }

    private fun setUpUi() {
        edt_ans.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                tv_wrong_security_ans?.isVisible = false
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        if (typeSecurityScreen == TYPE_INPUT_NEW_SECURITY) {
            tv_ques_des.text = getString(R.string.security_question_des)
            Glide.with(requireContext()).load(R.drawable.ic_cancel).into(tvCancel)
        } else {
            Glide.with(requireContext()).load(R.drawable.ic_back).into(tvCancel)
            tv_ques_des.text = getString(R.string.enter_a_security_question)
        }
    }

    private fun getDataBundle() {
        val bundle = arguments
        if (bundle != null) {
            val type = bundle.getInt(Constant.TYPE_SECURITY, 0)
            typeSecurityScreen = type
            isComeFromChangePassword = bundle.getBoolean(Constant.GO_TO_SECURITY_FROM_CHANGE_PASSWORD, false)
        }
    }

    private fun setClick() {
        tvCancel.setOnClickListener {
            if (typeSecurityScreen == TYPE_INPUT_NEW_SECURITY) {
                logEvent("SecurityQuestion_IconX_Clicked")
            } else {
                logEvent("SecurityQuestion_IconBack_Clicked")
            }
            findNavController().popBackStack()
        }

        tv_confirm_security.setOnClickListener {
            logEvent("SecurityQuestion_Confirm_Clicked")
            val ans = edt_ans.text.toString()
            if (typeSecurityScreen == TYPE_INPUT_NEW_SECURITY) {
                if (ans.isNotBlank()) {
                    DataStore.saveAns(ans)
                    val bundle = Bundle()
                    bundle.putInt(Constant.TYPE_PASSWORD, 0)
                    bundle.putBoolean(Constant.COME_FROM_SECURITY, true)
                    gotoFrag(
                        R.id.securityQuesFrag,
                        R.id.action_securityQuesFrag_to_mainFrag,
                        bundle
                    )
                }
            } else {
                if (ans == DataStore.getAns()) {
                    val bundle = Bundle()
                    bundle.putInt(Constant.TYPE_PASSWORD, 0)
                    bundle.putBoolean(Constant.COME_FROM_SECURITY, true)
                    if (isComeFromChangePassword) {
                        gotoFrag(
                            R.id.securityQuesFrag,
                            R.id.action_securityQuesFrag_to_passWordFrag,
                            bundle
                        )
                    } else {
                        gotoFrag(
                            R.id.securityQuesFrag,
                            R.id.action_securityQuesFrag_to_mainFrag,
                            bundle
                        )
                    }

                } else {
                    tv_wrong_security_ans?.isVisible = true
                }
            }
        }
    }
}
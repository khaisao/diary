package com.sutech.diary.view.password

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.sutech.common.PassCodeView
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.util.Constant
import com.sutech.diary.util.Constant.COME_FROM_SECURITY
import com.sutech.diary.util.Constant.TYPE_PASSWORD
import com.sutech.diary.util.show
import com.sutech.diary.view.password.SecurityQuesFrag.Companion.TYPE_INPUT_NEW_SECURITY
import com.sutech.diary.view.password.SecurityQuesFrag.Companion.TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.test.dialognew.setPreventDoubleClick
import kotlinx.android.synthetic.main.fragment_pass_word.*

class PassWordFrag : BaseFragment(R.layout.fragment_pass_word) {

    var passwordBefore = ""
    var passwordAfter = ""
    var oldPassword = ""

    /**
     * $param 0: create`
     * $param 1: update
     * $param 2: check
     * $param 3: check splash
     */
    var isTypePassword = -1

    override fun initView() {
        getDataBundle()
        if (isTypePassword == -1) {
            if (DataStore.getPassword().isNullOrBlank()) {
                gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
            } else {
                isTypePassword = 2
            }
        } else if (isTypePassword == 1) {
            tvPasscode.text = getString(R.string.enter_old_password)
            logEvent("EnterCuPassword_Show")

        } else if (isTypePassword == 0) {
            logEvent("SetpassScr_Show")
            tvPasscode.text = getString(R.string.set_password)
        }
        if (isTypePassword != 3) {
            ll_forgot_password.isVisible = false
            Glide.with(requireContext()).load(R.drawable.ic_back).into(tvCancel)
        } else {
            layoutAdsPassword.visibility = View.VISIBLE
            showAdsWithLayout("native_password", layoutAdsPassword)
            Glide.with(requireContext()).load(R.drawable.ic_cancel).into(tvCancel)
        }
        setClick()
    }

    private fun getDataBundle() {
        val bundle = arguments
        if (bundle != null) {
            val type = bundle.getInt(TYPE_PASSWORD, -1)
            val comeFromSecurity = bundle.getBoolean(COME_FROM_SECURITY, false)
            if (type != -1) {
                isTypePassword = if (type == 3) {
                    3
                } else {
                    if (DataStore.getPassword().isNullOrEmpty() || comeFromSecurity) {
                        0
                    } else {
                        1
                    }
                }
            }
        }
        if(isTypePassword == 3){
            logEvent("UnlockPass_Show")
        }
    }

    private fun setClick() {
        activity?.onBackPressedDispatcher?.addCallback(this, true) {
            if (isTypePassword == 2 || isTypePassword == 3) {
                activity?.finish()
            } else {
                onBackPress(R.id.passWordFrag)
            }
        }
        btn1.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(1)
        }
        btn2.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(2)
        }
        btn3.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(3)
        }
        btn4.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(4)
        }
        btn5.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(5)
        }
        btn6.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(6)
        }
        btn7.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(7)
        }
        btn8.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(8)
        }
        btn9.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(9)
        }
        btn0.setOnClickListener {
            tvWrongPass.isVisible = false
            passCodeView.addPassCode(0)
        }

        tvCancel.setOnClickListener {
            if (tvPasscode.text.toString() == getString(R.string.enter_new_password)) {
                logEvent("EnterNewPassword_IconBack_Clicked")
            } else if (tvPasscode.text.toString() == getString(R.string.enter_old_password)) {
                logEvent("OldPassword_Back_Clicked")
            } else if (tvPasscode.text.toString() == getString(R.string.set_password)
            ) {
                logEvent("SetpassScr_IconBack_Clicked")
                DataStore.setUsePassword(false)
            } else if (tvPasscode.text.toString() == getString(R.string.confirm_password)
            ) {
                DataStore.setUsePassword(false)
                logEvent("ConfirmPassword_IconBack_Clicked")
            } else if (tvPasscode.text.toString() == getString(
                    R.string.confirm_new_password
                )) {
                logEvent("ConfirmNewScr_IconBack_Clicked")
            } else if(tvPasscode.text.toString() == getString(
                    R.string.enter_pass_word
                )){
                logEvent("UnlockPass_IconBack_Clicked")
            } else {
                logEvent("SetpassScr_IconSkip_Clicked")
            }
            if (isTypePassword == 2 || isTypePassword == 3) {
                activity?.finish()
            } else {
                onBackPress(R.id.passWordFrag)
            }
        }
        btnReset.setPreventDoubleClick(300) {
            passCodeView.clearPassCode()
        }
        btnDelete.setPreventDoubleClick(300) {
            passCodeView.removePassCode()
        }

        ll_forgot_password.setOnClickListener {
            goToSecurityScreen()
        }

        passCodeView.setOnDoneListener(object : PassCodeView.PassCodeViewListener {
            override fun onPassCodeDone(passCode: String) {
                when (isTypePassword) {
                    0 -> {
//                        create
                        if (passwordBefore.isEmpty()) {
                            passwordBefore = passCode
                            passCodeView.clearPassCode()
                            tvPasscode.text = getString(R.string.confirm_password)
                            logEvent("ConfirmPassword_Show")
                        } else {
                            passwordAfter = passCode
                            if (passwordAfter == passwordBefore) {
                                DataStore.savePassword(passCode)
                                DataStore.setUsePassword(true)
                                if (!DataStore.getAns().isNullOrEmpty() && !DataStore.getQues()
                                        .isNullOrEmpty()
                                ) {
                                    gotoFrag(
                                        R.id.passWordFrag,
                                        R.id.action_passWordFrag_to_mainFrag
                                    )
                                } else {
                                    goToSecurityScreen()
                                }
                            } else {
                                tvWrongPass.text = getString(R.string.re_password_wrong)
                                passCodeWrong()
                            }
                        }
                    }
                    1 -> {
//                        update
                        if (oldPassword.isEmpty()) {
                            if (DataStore.checkPassword(passCode)) {
                                oldPassword = passCode
                                tvPasscode.text = getString(R.string.enter_new_password)
                                logEvent("EnterNewPassword_Show")
                            } else {
                                tvWrongPass.text = getString(R.string.old_password_wrong)
                                passCodeWrong()
                            }
                            passCodeView.clearPassCode()
                        } else if (passwordBefore.isEmpty()) {
                            passwordBefore = passCode
                            passCodeView.clearPassCode()
                            tvPasscode.text = getString(R.string.confirm_new_password)
                            logEvent("ConfirmNewScr_Show")
                        } else {
                            passwordAfter = passCode
                            passCodeView.clearPassCode()
                            if (passwordAfter == passwordBefore) {
                                DataStore.savePassword(passCode)
                                DataStore.setUsePassword(true)
                                onBackPress(R.id.passWordFrag)
                            } else {
                                tvWrongPass.text = getString(R.string.re_password_wrong)
                                passCodeWrong()
                            }
                        }
                    }
                    2 -> {
//                        check
                        if (DataStore.checkPassword(passCode)) {
                            findNavController().popBackStack()
//                            gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
                        } else {
                            tvWrongPass.text = getString(R.string.wrong_password)
                            passCodeWrong()
                        }

                    }
                    3 -> {
//                        check splash
                        if (DataStore.checkPassword(passCode)) {
//                            findNavController().popBackStack()
                            gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
                        } else {
                            tvWrongPass.text = getString(R.string.wrong_password)
                            passCodeWrong()
                        }

                    }
                }
            }

        })
    }

    private fun goToSecurityScreen(){
        logEvent("UnlockPass_Forgotpassword")
        val ques = DataStore.getQues()
        val ans = DataStore.getAns()
        val bundle = Bundle()
        if (ques.isNullOrBlank() || ans.isNullOrBlank()) {
            bundle.putInt(Constant.TYPE_SECURITY, TYPE_INPUT_NEW_SECURITY)
        } else {
            bundle.putInt(Constant.TYPE_SECURITY, TYPE_INPUT_SECURITY_TO_CHANGE_PASSWORD)
        }
        bundle.putBoolean(Constant.GO_TO_SECURITY_FROM_CHANGE_PASSWORD,true)
        gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_securityQuesFrag, bundle)
    }


    private fun passCodeWrong() {
        tvWrongPass.show()
        passCodeView.clearPassCode()
        passCodeView.playWrongPassAnimation()
    }
}
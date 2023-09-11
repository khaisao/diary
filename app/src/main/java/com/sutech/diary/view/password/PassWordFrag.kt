package com.sutech.diary.view.password

import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.sutech.common.PassCodeView
import com.sutech.diary.base.BaseFragment
import com.sutech.diary.database.DataStore
import com.sutech.diary.util.Constant.CREATE_FROM_SPLASH
import com.sutech.diary.util.Constant.TYPE_PASSWORD
import com.sutech.diary.util.show
import com.sutech.journal.diary.diarywriting.lockdiary.R
import com.test.dialognew.setPreventDoubleClick
import kotlinx.android.synthetic.main.fragment_pass_word.*

class PassWordFrag : BaseFragment(R.layout.fragment_pass_word) {

    var passwordBefore = ""
    var passwordAfter = ""
    var oldPassword = ""

    /**
     * $param 0: create
     * $param 1: update
     * $param 2: check
     * $param 3: check splash
     */
    var isTypePassword = -1
    var createFromSplash = -1


    override fun initView() {
        getDataBundle()
        showBanner("banner_password", layoutAdsPassword)
        if (isTypePassword == -1) {
            if (DataStore.getPassword().isNullOrBlank()) {
                gotoFrag(R.id.passWordFrag, R.id.action_passWordFrag_to_mainFrag)
            } else {
                isTypePassword = 2
            }
            tvCancel.text = getString(R.string.cancel)
        } else if (isTypePassword == 1) {
            tvPasscode.text = getString(R.string.enter_old_password)
            tvCancel.text = getString(R.string.cancel)
            logEvent("OldPassword_Show")

        } else if (isTypePassword == 0) {
            logEvent("SetpassScr_Show")
            tvPasscode.text = getString(R.string.set_password)
            tvCancel.text = getString(R.string.skip)
        }
        setClick()
    }

    private fun getDataBundle() {
        val bundle = arguments
        if (bundle != null) {
            val type = bundle.getInt(TYPE_PASSWORD, -1)
            createFromSplash = bundle.getInt(CREATE_FROM_SPLASH, -1)
            if (type != -1) {
                if (type == 3) {
                    isTypePassword = 3
                } else {

                    isTypePassword = if (DataStore.getPassword().isNullOrEmpty()) {
                        0
                    } else {
                        1
                    }
                }

            }
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
            tvWrongPass.text = ""
            passCodeView.addPassCode(1)
        }
        btn2.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(2)
        }
        btn3.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(3)
        }
        btn4.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(4)
        }
        btn5.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(5)
        }
        btn6.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(6)
        }
        btn7.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(7)
        }
        btn8.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(8)
        }
        btn9.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(9)
        }
        btn0.setOnClickListener {
            tvWrongPass.text = ""
            passCodeView.addPassCode(0)
        }

        tvCancel.setOnClickListener {
            if (tvPasscode.text.toString() == getString(R.string.enter_new_password)) {
                logEvent("NewPassword_IconCancel_Clicked")
            } else if (tvPasscode.text.toString() == getString(R.string.enter_old_password)) {
                logEvent("OldPassword_Cancel_Clicked")
            } else if (tvPasscode.text.toString() == getString(R.string.set_password) || tvPasscode.text.toString() == getString(
                    R.string.confirm_password
                )
            ) {
                DataStore.setUsePassword(false)
                logEvent("ConfirmpassScr_IconSkip_Clicked")
            } else {
                logEvent("SetpassScr_IconSkip_Clicked")
            }


            if (isTypePassword == 2 || isTypePassword == 3) {
                activity?.finish()
            } else {
                if (isTypePassword == 0 && createFromSplash != -1) {
                    gotoFrag(
                        R.id.passWordFrag,
                        R.id.action_passWordFrag_to_mainFrag
                    )
                }
                onBackPress(R.id.passWordFrag)
            }
        }
        btnReset.setPreventDoubleClick(300) {
            passCodeView.clearPassCode()
        }
        btnDelete.setPreventDoubleClick(300) {
            passCodeView.removePassCode()
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
                            logEvent("ConfirmpassScr_Show")
                        } else {
                            passwordAfter = passCode
                            if (passwordAfter == passwordBefore) {
                                DataStore.savePassword(passCode)
                                DataStore.setUsePassword(true)
                                if (createFromSplash == -1) {
                                    onBackPress(R.id.passWordFrag)
                                } else {
                                    gotoFrag(
                                        R.id.passWordFrag,
                                        R.id.action_passWordFrag_to_mainFrag
                                    )
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
                                logEvent("NewPassword_Show")
                            } else {
                                tvWrongPass.text = getString(R.string.old_password_wrong)
                                passCodeWrong()
                            }
                            passCodeView.clearPassCode()
                        } else if (passwordBefore.isEmpty()) {
                            passwordBefore = passCode
                            passCodeView.clearPassCode()
                            tvPasscode.text = getString(R.string.confirm_password)
                            logEvent("ConfirmScr_Show")
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

    private fun passCodeWrong() {
        tvWrongPass.show()
        passCodeView.clearPassCode()
        passCodeView.playWrongPassAnimation()
    }
}
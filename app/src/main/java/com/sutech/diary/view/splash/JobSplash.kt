package com.sutech.diary.view.splash

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class JobSplash {

    private var progress = -1
    private val max = 100
    private var isShowAds = false
    private var delay = 70L

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var loopingFlowJob: Job? = null
    private var loopingFlow = flow {
        while (true) {
            emit(Unit)
            delay(delay)
        }
    }

    fun setDelay(delay: Long) {
        this.delay = delay
    }

    fun startJob(jobProgress: JobProgress?) {
        if (isShowAds()) {
            return
        }
        stopJob()
        if (isProgressMax()) {
            jobProgress?.onProgress(progress)
            return
        }
        loopingFlowJob = coroutineScope.launch(Dispatchers.Main) {
            loopingFlow.collect {
                progress++
                jobProgress?.onProgress(progress)
                if (progress > max) {
                    stopJob()
                }
            }
        }
    }

    fun stopJob() {
        loopingFlowJob?.cancel()
    }

    fun isShowAds(): Boolean {
        return isShowAds
    }

    fun isProgressMax(): Boolean {
        return progress >= max
    }

    fun setShowAds() {
        isShowAds = true
    }

    interface JobProgress {
        /**
        * [count] 0 .. 100
        * */
        fun onProgress(count: Int)
    }

}
package com.example.timer

import android.os.Handler
import android.view.View
import com.example.timer.databinding.ActivityMainBinding

class Timer(val binding: ActivityMainBinding) {

    private var status: Boolean = false
    private var countdownMode: Boolean = false
    private var timer: Int = 0
    private lateinit var thread: Thread
    private lateinit var handler: Handler


    fun start(arg: Int) {
        status = true
        thread = Thread {
            while (status) {
                if (!countdownMode) {
                    increaseValue(arg)
                    changeTimerText()
                } else {
                    decreasingValue(arg)
                    changeTimerText()
                }
                binding.progress.progress = timer
                Thread.sleep(1000)
            }
        }
        thread.start()
    }

    private fun increaseValue(arg: Int) {
        if (timer < arg && timer >= 0) {
            timer++
        } else {
            reset()
        }
    }

    private fun decreasingValue(arg: Int) {
        if (timer <= arg && timer > 0) {
            timer--
        } else {
            reset()
        }
    }

    private fun changeTimerText() {
        handler.post {
            binding.timer.text = timer.toString()
        }
    }

    private fun setTimerValue(arg: Int) {
        timer = if (!countdownMode) {
            0
        } else {
            arg
        }
    }

    fun stop() {
        status = false
        timer = 0
        changeTimerText()
    }

    fun pause() {
        status = false
    }

    fun getStatus(): Boolean {
        return status
    }

    fun setCountdownMode(timerValue: Int, isChecked: Boolean) {
        countdownMode = isChecked
        if (!countdownMode) {
            setTimerValue(timerValue)
        } else {
            setTimerValue(timerValue)
        }
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

    private fun reset(){
        status = false
        stop()
        handler.post {
            binding.start.setText(R.string.start)  //text = "start"
            binding.bar.isEnabled = true
            binding.switchTimer.isEnabled = true
            binding.stop.setVisibility(View.GONE)
        }
    }
}
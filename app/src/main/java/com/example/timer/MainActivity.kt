package com.example.timer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.timer.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import kotlin.concurrent.thread

private const val TIMER_DEFAULT = 30

class MainActivity : stop, AppCompatActivity() {
    private var handler = Handler(Looper.getMainLooper())
    private lateinit var titleText: CharSequence
    private lateinit var timerText: TextView
    private lateinit var buttonStart: Button
    private lateinit var buttonStop: Button
    private lateinit var switch: SwitchCompat
    private lateinit var progressBar: ProgressBar
    private lateinit var slider: Slider
    private var status: Boolean = false
    private var timerValue = TIMER_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        titleText = binding.title.text
        timerText = binding.timer
        buttonStart = binding.start
        buttonStop = binding.stop
        switch = binding.switchTimer
        progressBar = binding.progress
        progressBar.max = TIMER_DEFAULT

        slider = binding.bar
        slider.value = TIMER_DEFAULT.toFloat()


        val timer = Timer(binding)
        timer.setHandler(handler)

        buttonStart.setOnClickListener { v ->
            if (!status) {
                start(timer, v)
            } else {
                pause(timer, v)
            }
        }

        buttonStop.setOnClickListener { v ->
            stopTimer(timer)
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            timer.setCountdownMode(timerValue, isChecked)
        }

        handler.post {
            slider.addOnChangeListener { _, value, _ ->
                timerValue = slider.value.toInt()
                progressBar.max = timerValue
            }
        }

    }


    fun start(timer: Timer, v: View) {
        buttonStop.setVisibility(View.VISIBLE)
        slider.isEnabled = false
        switch.isEnabled = false
        thread {
            timer.start(timerValue)
            status = timer.getStatus()
            handler.post {
                (v as Button).text = "pause"
            }
        }
    }

    fun stopTimer(timer: Timer) {
        timer.stop()
        status = timer.getStatus()
        stop()
    }

    fun pause(timer: Timer, v: View) {
        thread {
            handler.post {
                (v as Button).text = "start"
            }
        }
        timer.pause()
        status = timer.getStatus()
    }


    override fun stop() {
        slider.isEnabled = true
        switch.isEnabled = true
        buttonStop.setVisibility(View.GONE)
        buttonStart.text = "start"
        progressBar.progress = 0
    }
}
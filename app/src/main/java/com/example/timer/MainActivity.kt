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
import java.util.Locale
import kotlin.concurrent.thread

private const val TIMER_DEFAULT = 30

class MainActivity : AppCompatActivity() {
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

    /**
     * метод start
     * запускает таймер
     * меняет текст кнопки на "pause".
     */
    private fun start(timer: Timer, v: View) {
        lockScreen()
        thread {
            timer.start(timerValue)
            status = timer.getStatus()
            handler.post {
                (v as Button).setText(R.string.pause) //text = "pause"
            }
        }
    }


    /**
     * Метод stopTimer
     * останавливает таймер,
     * сбрасывает его статус
     * сбрасывает все элементы интерфейса.
     */
    private fun stopTimer(timer: Timer) {
        timer.stop()
        status = timer.getStatus()
        reset()
    }


    /**
     * Метод pause
     * приостанавливает таймер
     * меняет текст кнопки на "continue".
     */
    private fun pause(timer: Timer, v: View) {
        thread {
            handler.post {
                (v as Button).setText(R.string.cont)   //text = "continue"
            }
        }
        timer.pause()
        status = timer.getStatus()
    }

    /**
     * Метод reset переводит интерфейс в исходное состояние.
     * сбрасывает таймер,
     * делая слайдер и переключатель снова доступными,
     * скрывает кнопку остановки
     * меняет текст кнопки начала на "start".
     */
    private fun reset() {
        handler.post {
            slider.isEnabled = true
            switch.isEnabled = true
            buttonStop.setVisibility(View.GONE)
            buttonStart.setText((R.string.start))   //text = "start"
            progressBar.progress = 0
        }
    }

    /**
     * Этот метод блокирует экран,
     * делая слайдер и переключатель недоступными
     * делая кнопку остановки видимой.
     */
    private fun lockScreen(){
        handler.post {
            buttonStop.setVisibility(View.VISIBLE)
            slider.isEnabled = false
            switch.isEnabled = false
        }
    }
}
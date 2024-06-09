package com.example.timer.Presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.timer.Domain.Timer
import com.example.timer.Domain.UseCasePause
import com.example.timer.Domain.UseCaseStart
import com.example.timer.Domain.UseCaseStop
import com.example.timer.MainActivity
import com.example.timer.databinding.FragmentTimerBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.concurrent.timer

class TimerFragment : Fragment() {
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private val DEFAULT_VALUE = 30F

    private val viewModel by lazy { ViewModelProvider(this)[TimerViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formatter = DateTimeFormatter.ofPattern("mm:ss")

        val timer = binding.timer
        val buttonStart = binding.start
        val buttonStop = binding.stop
        val switch = binding.switchTimer
        val minutesMode = binding.minutesMode
        val progressBar = binding.progress
        val bar = binding.bar
        bar.value = DEFAULT_VALUE
        timer.text = DEFAULT_VALUE.toInt().toString()


        bar.addOnChangeListener { slider, value, _ ->

            viewModel.timer.maxValue = value.toInt()
            if (viewModel.timer.minutesMode){
                val localTime = LocalTime.ofSecondOfDay(value.toLong())
                timer.text = formatter.format(localTime)
            }else{
                timer.text = value.toInt().toString()
            }
        }



        buttonStart.setOnClickListener {
            if (!viewModel.timer.activate){
                UseCaseStart(timer = viewModel.timer).start()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        progressBar.max = bar.value.toInt()
                        if (viewModel.timer.minutesMode){
                            val localTime = LocalTime.ofSecondOfDay(it.toLong())
                            timer.text = formatter.format(localTime)
                        }else{
                            timer.text = it.toString()
                        }
                        progressBar.progress = it
                    }
                buttonStart.text = "Pause"
            }else{
                UseCasePause(timer = viewModel.timer).pause()
                buttonStart.text = "Resume"
            }
            minutesMode.isEnabled = false
            buttonStop.setVisibility(View.VISIBLE)
            bar.isEnabled = false
            switch.isEnabled = false
        }

        buttonStop.setOnClickListener {

            UseCaseStop(timer = viewModel.timer).stop()

            buttonStart.text = "Start"
            minutesMode.isEnabled = true
            buttonStart.setVisibility(View.VISIBLE)
            buttonStop.setVisibility(View.GONE)
            bar.isEnabled = true
            switch.isEnabled = true
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.timer.countdownMode = isChecked
        }

        minutesMode.setOnCheckedChangeListener{_, mode ->
            viewModel.timer.minutesMode = mode
            val localTime = LocalTime.ofSecondOfDay(30L)
            timer.text = formatter.format(localTime)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
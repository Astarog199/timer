package com.example.timer.Presentation

import androidx.lifecycle.ViewModel
import com.example.timer.Domain.Timer


class TimerViewModel(
//    val useCaseStart: UseCaseStart,
//    val useCaseStop: UseCaseStop,
//    val useCaseReset: UseCaseReset
) : ViewModel() {


    var timer: Timer

    init {
        timer = Timer()
    }



}
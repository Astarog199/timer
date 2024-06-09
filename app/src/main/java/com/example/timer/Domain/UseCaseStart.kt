package com.example.timer.Domain

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class UseCaseStart (val timer: Timer) {
    fun start(): Observable<Int> = Observable.create<Int> {
        timer.activate = true
        if (timer.minutesMode){
            timer.maxValue = timer.maxValue*60
        }

        while (timer.activate) {
            if (!timer.countdownMode) {
                increaseValue()
            } else {
                decreasingValue()
            }
            it.onNext(timer.value)
            Thread.sleep(1000)
        }
    }.subscribeOn(Schedulers.io())

    private fun increaseValue() {
        if (timer.value < timer.maxValue && timer.value >= 0) {
            timer.value++
        } else {
            timer.activate = false
        }
    }

    private fun decreasingValue() {
        if (timer.maxValue >= 0) {
            timer.value = timer.maxValue--
        } else {
            timer.activate = false
        }
    }
}
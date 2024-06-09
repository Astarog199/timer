package com.example.timer.Domain

class UseCaseStop(val timer: Timer)  {

    fun stop() {
        timer.activate = false
        timer.value = 0
    }
}
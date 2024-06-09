package com.example.timer.Domain

class UseCasePause(val timer: Timer) {
    fun pause(){
        timer.activate = false
    }
}
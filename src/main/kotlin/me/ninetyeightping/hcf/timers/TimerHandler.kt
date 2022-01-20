package me.ninetyeightping.hcf.timers

import me.ninetyeightping.hcf.timers.impl.EnderpearlTimer

class TimerHandler {

    lateinit var enderpearlTimer: EnderpearlTimer

    init {
        enderpearlTimer = EnderpearlTimer
    }
}
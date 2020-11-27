package com.example.boleboka

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
 * Her legges informasjon som skal deles mellom fragment
 * Nå brukes den bare til å dele info fra workouts til exersise
 */
class Communicator : ViewModel(){
    val message = MutableLiveData<Any>()
    val position = MutableLiveData<Int>()

    fun setMsgCommunicator(msg: String) {
        message.value = msg
    }

    fun positionCommunicator(pos: Int) {
        position.value = pos
    }
}
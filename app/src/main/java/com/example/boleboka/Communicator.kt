package com.example.boleboka

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/* Erlend:
 * Her legges informasjon som skal deles mellom fragment
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
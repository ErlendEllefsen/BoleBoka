package com.example.boleboka

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
 * Her legges informasjon som skal deles mellom fragment
 * Nå brukes den bare til å dele info fra workouts til exersise
 */

/*
 * TODO: Jon, her kan man dele ID fra workout til exersise. Da vet exersise
 *  hvilken exersiseliste den skal displaye.
 */
class Communicator : ViewModel(){
    val message = MutableLiveData<Any>()

    fun setMsgCommunicator(msg:String){
        message.value = msg
    }
}
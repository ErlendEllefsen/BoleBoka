package com.example.boleboka

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

@IgnoreExtraProperties
data class Post (
    var adresse: String? = "",
    var epost: String = "",
    var hobby: String = "",
    var navn: String = "",

) {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "Adresse" to adresse,
            "Epost" to epost,
            "Hobby" to hobby,
            "Navn" to navn
        )
    }

}
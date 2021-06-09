package com.fagundes.projetosaude.model

data class NewDataReadBody(
    var IMEI: String,
    var heartHate: Int? = null,
    var bloodPressure: BloodPressure? = null
)

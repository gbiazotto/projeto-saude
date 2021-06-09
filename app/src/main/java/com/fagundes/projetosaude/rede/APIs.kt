package com.fagundes.projetosaude.rede

import com.fagundes.projetosaude.model.NewDataReadBody
import com.fagundes.projetosaude.model.NewDeviceBody
import com.fagundes.projetosaude.model.NewLocationBody
import com.fagundes.projetosaude.model.dtos.DTOTransmissao
import com.fagundes.projetosaude.model.dtos.RespostaAPI
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body

interface APIs {

//    @POST("/api/postMedidas")
//    fun enviaMedidas(@Body dtoTransmissao: DTOTransmissao): Call<RespostaAPI>

    @POST("/devices")
    fun registerNewDevice(@Body newDevice: NewDeviceBody): Call<Response<Any?>?>

    @POST("/healths")
    fun registerNewDataRead(@Body newDataRead: NewDataReadBody): Call<Any?>

    @POST("/locations")
    fun registerNewLocation(@Body newLocation: NewLocationBody): Call<Any?>
}

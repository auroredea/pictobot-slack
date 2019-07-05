package fr.auroredea.pictobot

import fr.auroredea.pictobot.model.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface Slack {

    @POST
    fun postAMessage(
        @Url url: String,
        @Body body: Message
    ): Call<String>

}
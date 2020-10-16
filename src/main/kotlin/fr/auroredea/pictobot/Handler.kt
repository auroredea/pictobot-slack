package fr.auroredea.pictobot

import fr.auroredea.pictobot.model.Message
import mu.KotlinLogging
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom

private val logger = KotlinLogging.logger {}

class Handler:RequestHandler<Map<String, Any>, String> {
    override fun handleRequest(p0: Map<String, Any>?, p1: Context?): String {
        val client = Retrofit.Builder()
                .baseUrl(System.getenv("baseHookUrl") ?: "https://hooks.slack.com")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(Slack::class.java)


        val wordsResources = Handler::class.java.getResource("/words.txt") ?: return "Resource empty"

        val words = wordsResources.readText().split("\n").map { it.trim() }

        if(words.isEmpty()) {
            logger.debug { "Dictionary empty with resource : $wordsResources" }
            return "Dictionary empty"
        }

        val secureRandom = SecureRandom()
        val random = secureRandom.nextInt(words.size)
        val wordOfDay = words[random]

        val channel = System.getenv("channelId") ?: System.getenv("channel") ?: ""
        val channelUrl = System.getenv("channelHookUrl") ?: ""

        val message = Message.builder(
                """
            Picto de la semaine !

            *$wordOfDay* - 20 secondes - ✏ à poster dans <#$channel>
        """.trimIndent()
        ).build()

        logger.debug { "Creating message for Slack : $message" }

        val response = client.postAMessage(channelUrl, message).execute()

        return if (response.isSuccessful) {
            logger.debug { "Message sent" }
            "Done"
        } else {
            logger.debug { "Error message : ${response.errorBody()?.string()}" }
            "Done with errors"
        }
    }

    companion object HandlerLocal {
        fun handleRequest(): String {
            return Handler().handleRequest(null, null)
        }
    }
}

fun main() {
    Handler.handleRequest()
}


package fr.auroredea.pictobot.model

data class Message(val text: String, val type: String = "mrkdwn") {

    companion object {
        fun builder(
            text: String
        ) = Builder(text)
    }

    class Builder(private val text: String) {
        fun build() = Message(text)
    }
}
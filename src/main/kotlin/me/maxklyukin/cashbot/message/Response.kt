package me.maxklyukin.cashbot.message

sealed class RespondTo {
    data class Id(val id: String) : RespondTo()
    object Default : RespondTo()
}

sealed class Response {
    data class WithText(val text: String, val respondTo: RespondTo) : Response()
    object Empty : Response()
}

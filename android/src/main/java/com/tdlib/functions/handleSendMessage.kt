package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleSendMessage(chatId: String, message: String, promise: Promise) {
    try {
        val chatIdLong = chatId.toLongOrNull()
        if (chatIdLong == null) {
            promise.reject("SEND_MESSAGE_ERROR", "Invalid chat ID")
            return
        }

        val sendMessage = TdApi.SendMessage().apply {
            this.chatId = chatIdLong
            this.messageThreadId = 0
            this.replyTo = null
            this.options = null
            this.replyMarkup = null
            this.inputMessageContent = TdApi.InputMessageText().apply {
                this.text = TdApi.FormattedText(message, null)
                this.linkPreviewOptions = null
                this.clearDraft = false
            }
        }

        client?.send(sendMessage) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve("Message sended successfully")
                is TdApi.Error -> promise.reject("SEND_MESSAGE_ERROR", obj.message)
            }
        }
    } catch (e: Exception) {
        promise.reject("SEND_MESSAGE_EXCEPTION", e.message)
    }
}
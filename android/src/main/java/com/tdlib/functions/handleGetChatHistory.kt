package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleGetChatHistory(chatId: Double, fromMessageId: Double, offset: Double, limit: Double, onlyLocal: Boolean, promise: Promise) {
    try {
        val request = TdApi.GetChatHistory().apply {
            this.chatId = chatId.toLong()
            this.fromMessageId = fromMessageId.toLong()
            this.offset = offset.toInt()
            this.limit = limit.toInt()
            this.onlyLocal = onlyLocal
        }

        client?.send(request) { obj ->
            when (obj) {
                is TdApi.Messages -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("EXCEPTION", e.message)
    }
}
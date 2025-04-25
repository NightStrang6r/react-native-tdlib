package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleGetChat(chatId: Double, promise: Promise) {
    try {
        val request = TdApi.GetChat().apply {
            this.chatId = chatId.toLong()
        }

        client?.send(request) { obj ->
            when (obj) {
                is TdApi.Chat -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("EXCEPTION", e.message)
    }
}
package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi

fun TdlibModule.handleGetChats(limit: Double, promise: Promise) {
    try {
        val request = TdApi.GetChats().apply {
            this.chatList = null
            this.limit = limit.toInt()
        }

        client?.send(request) { obj ->
            when (obj) {
                is TdApi.Chats -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("GET_CHATS_ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("GET_CHATS_EXCEPTION", e.message)
    }
}
package com.tdlib.functions

import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import org.drinkless.tdlib.TdApi
import com.facebook.react.bridge.ReadableArray

fun TdlibModule.handleDeleteMessages(chatId: Double, messageIds: ReadableArray, revoke: Boolean?, promise: Promise) {
    try {
        val request = TdApi.DeleteMessages().apply {
            this.chatId = chatId.toLong()
            this.messageIds = LongArray(messageIds.size()) { messageIds.getDouble(it).toLong() }
            this.revoke = revoke ?: true
        }

        client?.send(request) { obj ->
            when (obj) {
                is TdApi.Ok -> promise.resolve(gson.toJson(obj))
                is TdApi.Error -> promise.reject("ERROR", gson.toJson(obj))
            }
        }
    } catch (e: Exception) {
        promise.reject("EXCEPTION", e.message)
    }
}
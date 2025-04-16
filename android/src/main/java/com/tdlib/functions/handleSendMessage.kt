package com.tdlib.functions

import android.net.Uri
import android.provider.OpenableColumns
import com.tdlib.TdlibModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import org.drinkless.tdlib.TdApi
import java.io.File
import java.io.FileOutputStream

fun TdlibModule.handleSendMessage(context: ReactApplicationContext, chatId: String, message: String, file: String?, promise: Promise) {
    var copiedFile: File? = null

    try {
        val chatIdLong = chatId.toLongOrNull()
        if (chatIdLong == null) {
            promise.reject("SEND_MESSAGE_ERROR", "Invalid chat ID")
            return
        }

        var inputMessageContent: TdApi.InputMessageContent? = null

        if (file != null) {
            val realFilePath = if (file?.startsWith("content://") == true) {
                val copied = copyContentUriToCache(context, file)
                copiedFile = copied?.let { File(it) }
                copied
            } else {
                file
            }
    
            if (realFilePath == null) {
                promise.reject("SEND_MESSAGE_ERROR", "Unable to process file path")
                return
            }
    
            val inputFile = TdApi.InputFileLocal(realFilePath)
            inputMessageContent = TdApi.InputMessageDocument().apply {
                this.document = inputFile
                this.caption = TdApi.FormattedText(message, null)
                this.thumbnail = null
                this.disableContentTypeDetection = false
            }
        } else {
            if (message.isEmpty()) {
                promise.reject("SEND_MESSAGE_ERROR", "Message cannot be empty")
                return
            }

            inputMessageContent = TdApi.InputMessageText().apply {
                this.text = TdApi.FormattedText(message, null)
                this.linkPreviewOptions = null
                this.clearDraft = false
            }
        }

        val sendMessage = TdApi.SendMessage().apply {
            this.chatId = chatIdLong
            this.messageThreadId = 0
            this.replyTo = null
            this.options = null
            this.replyMarkup = null
            this.inputMessageContent = inputMessageContent
        }

        val safePromise = promise

        client?.send(sendMessage) { obj ->
            when (obj) {
                is TdApi.Message -> safePromise.resolve(gson.toJson(obj))
                is TdApi.Error -> safePromise.reject("SEND_MESSAGE_ERROR", obj.message)
            }
        }
    } catch (e: Exception) {
        promise.reject("SEND_MESSAGE_EXCEPTION", e.message)
        copiedFile?.delete()
    }
}

private fun TdlibModule.copyContentUriToCache(context: ReactApplicationContext, uriString: String): String? {
    return try {
        val uri = Uri.parse(uriString)
        val contentResolver = context.contentResolver

        val fileName = getFileName(context, uri) ?: "tdlib_file_${System.currentTimeMillis()}"
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(tempFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        tempFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun TdlibModule.getFileName(context: ReactApplicationContext, uri: Uri): String? {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex != -1) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

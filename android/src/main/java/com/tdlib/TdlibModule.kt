package com.tdlib

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap

import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import com.google.gson.Gson
import java.util.Map
import java.util.HashMap
import org.json.JSONObject
import org.json.JSONException
import android.util.Log

import com.tdlib.functions.*

@ReactModule(name = TdlibModule.NAME)
class TdlibModule(reactContext: ReactApplicationContext) : NativeTdlibSpec(reactContext) {
    internal var TAG = "TdLibModule"
    internal var client: Client? = null
    internal var gson = Gson()

    companion object {
        const val NAME = "Tdlib"
    }

    override fun getName(): String {
        return NAME
    }

    override fun td_json_client_create(promise: Promise) {
        handle_td_json_client_create(promise)
    }

    /*override fun td_json_client_execute(request: ReadableMap, promise: Promise) {
        td_json_client_execute(request, promise)
    }

    override fun td_json_client_send(request: ReadableMap, promise: Promise) {
        td_json_client_send(request, promise)
    }*/

    override fun td_json_client_receive(promise: Promise) {
        handle_td_json_client_receive(promise)
    }

    // ==================== High-Level API ====================
    override fun startTdLib(parameters: ReadableMap, promise: Promise) {
        try {
            if (client != null) {
                promise.resolve("TDLib already started")
                return
            }

            client = Client.create(
                { obj -> Log.d(TAG, "Global Update: $obj") },
                null,
                null
            )

            Client.execute(TdApi.SetLogVerbosityLevel(0))
            setTdLibParameters(reactApplicationContext, parameters, promise)
        } catch (e: Exception) {
            promise.reject("TDLIB_START_ERROR", e.message)
        }
    }

    override fun getAuthorizationState(promise: Promise) {
        handleGetAuthorizationState(promise)
    }

    override fun login(userDetails: ReadableMap, promise: Promise) {
        handleLogin(userDetails, promise)
    }

    override fun verifyPhoneNumber(code: String, promise: Promise) {
        handleVerifyPhoneNumber(code, promise)
    }

    override fun verifyPassword(password: String, promise: Promise) {
        handleVerifyPassword(password, promise)
    }

    override fun getProfile(promise: Promise) {
        handleGetProfile(promise)
    }

    override fun sendMessage(chatId: String, message: String, file: String?, promise: Promise) {
        handleSendMessage(reactApplicationContext, chatId, message, file, promise)
    }

    override fun logout(promise: Promise) {
        handleLogout(promise)
    }

    override fun destroy(promise: Promise) {
        handleDestroy(promise)
    }

    // ==================== Helpers ====================
    private fun setTdLibParameters(reactApplicationContext: ReactApplicationContext, parameters: ReadableMap, promise: Promise) {
        handleSetTdLibParameters(reactApplicationContext, parameters, promise)
    }

    // ==================== Helpers ====================
    /*private fun convertMapToFunction(requestMap: Map<String, Any>): TdApi.Function {
        // TODO: Implement conversion logic based on TdApi request types
        throw UnsupportedOperationException("Conversion not implemented")
    }*/
}
package com.jhteck.icebox.api

import com.google.gson.TypeAdapter
import com.hele.mrd.app.lib.api.ApiConverterException
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter


class DecoderResponseConverter<T>(private val adapter: TypeAdapter<T>) :
    Converter<ResponseBody, T> {
    override fun convert(value: ResponseBody): T {
        return try {
            val resp = value.string()
            println(resp)
            if (resp.length <= 2) {
                throw RuntimeException(ApiConverterException(-2, "resp too short", null,resp))
            }
                val json = JSONObject(resp)
            val code = json.optInt("code")
            if (code == 200) {
                adapter.fromJson(resp)
            } else {
                val msg = json.optString("msg")
                throw ApiConverterException(code, msg, null,adapter.fromJson(resp))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is ApiConverterException) {
                throw e
            } else {
                throw RuntimeException(ApiConverterException(-1, "网络连接失败，请检查您的网络设置", e,null))
            }
        } finally {
            value.close()
        }

    }


}
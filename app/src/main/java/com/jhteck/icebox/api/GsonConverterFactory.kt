package com.jhteck.icebox.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class GsonConverterFactory(private val gson: Gson) : Converter.Factory() {
    companion object {
        fun create(gson: Gson): GsonConverterFactory {
            return GsonConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *> {
        val typeToken = TypeToken.get(type)
        val adapter = gson.getAdapter(typeToken)
        return DecoderResponseConverter(adapter)
    }

}
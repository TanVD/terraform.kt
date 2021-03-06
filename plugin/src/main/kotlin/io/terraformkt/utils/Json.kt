package io.terraformkt.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Json {
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    inline fun <reified T> string(value: T): String = moshi.adapter(T::class.java).toJson(value)
    inline fun <reified T> parse(value: String) = moshi.adapter(T::class.java).fromJson(value) as T
}

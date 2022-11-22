/*
 * Copyright (C) 2018 Drake, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.common.ducis.component.network.common

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.HashMap

class GsonConverter() : CustomJsonConverter() {
    companion object {
        val gson = GsonBuilder()
            .serializeNulls() //序列化为null对象
            .setDateFormat("yyyy-MM-dd HH:mm:ss") //设置日期的格式
            .disableHtmlEscaping() //防止对网址乱码 忽略对特殊字符的转换
            .registerTypeAdapter(String::class.java, GsonUtil.StringConverter()) //对为null的字段进行转换
            .registerTypeAdapter(object : TypeToken<HashMap<String, Any?>>(){}.type, GsonUtil.MapTypeAdapter())
            .create()
    }

    override fun <R> String.parseBody(succeed: Type): R? {
        return gson.fromJson(this, succeed)
    }

    /**
     * 解决将int转double问题
     */
    internal class MapTypeAdapter : TypeAdapter<Any?>() {
        @Throws(IOException::class)
        override fun read(`in`: JsonReader): Any? {
            val token: JsonToken = `in`.peek()
            return when (token) {
                JsonToken.BEGIN_ARRAY -> {
                    val list: MutableList<Any?> = ArrayList()
                    `in`.beginArray()
                    while (`in`.hasNext()) {
                        list.add(read(`in`))
                    }
                    `in`.endArray()
                    list
                }
                JsonToken.BEGIN_OBJECT -> {
                    val map: MutableMap<String, Any?> = LinkedTreeMap()
                    `in`.beginObject()
                    while (`in`.hasNext()) {
                        map[`in`.nextName()] = read(`in`)
                    }
                    `in`.endObject()
                    map
                }
                JsonToken.STRING -> `in`.nextString()
                JsonToken.NUMBER -> {
                    /**
                     * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                     */
                    val dbNum: Double = `in`.nextDouble()

                    // 数字超过long的最大值，返回浮点类型
                    if (dbNum > Long.MAX_VALUE) {
                        return dbNum
                    }

                    // 判断数字是否为整数值
                    val lngNum = dbNum.toLong()
                    if (dbNum == lngNum.toDouble()) {
                        lngNum
                    } else {
                        dbNum
                    }
                }
                JsonToken.BOOLEAN -> `in`.nextBoolean()
                JsonToken.NULL -> {
                    `in`.nextNull()
                    null
                }
                else -> throw IllegalStateException()
            }
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter?, value: Any?) {
            // 序列化无需实现
        }
    }
}
package com.common.ducis.network.common

import android.text.TextUtils
import com.common.ducis.extensions.removeEndZeros
import com.google.gson.*
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * @ClassName: GsonBinder
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2022/3/2
 */
object GsonUtil {
    //定义并配置gson
    private val gson: Gson = GsonBuilder() //建造者模式设置不同的配置
        .serializeNulls() //序列化为null对象
        .setDateFormat("yyyy-MM-dd HH:mm:ss") //设置日期的格式
        .disableHtmlEscaping() //防止对网址乱码 忽略对特殊字符的转换
        .registerTypeAdapter(String::class.java, StringConverter()) //对为null的字段进行转换
        .registerTypeAdapter(object : TypeToken<HashMap<String,Any?>>(){}.type,MapTypeAdapter())
        .create()

    /**
     * 对解析数据的形式进行转换
     *
     * @param obj 解析的对象
     * @return 转化结果为json字符串
     */
    fun toJsonStr(obj: Any?): String {
        return if (obj == null) {
            ""
        } else try {
            gson.toJson(obj)
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * 解析为一个具体的对象
     *
     * @param json 要解析的字符串
     * @param obj  要解析的对象
     * @param <T>  将json字符串解析成obj类型的对象
     * @return
    </T> */
    fun <T> toObj(json: String?, obj: Class<T>?): T? {
        //如果为null直接返回为null
        return if (obj == null || TextUtils.isEmpty(json)) {
            null
        } else try {
            gson.fromJson(json, obj)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return 不区分类型 传什么解析什么
     */
    fun <T> toObj(jsonStr: String?, type: Type?): T? {
        var t: T? = null
        try {
            t = gson.fromJson(jsonStr, type)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return t
    }

    /**
     * 将Json数组解析成相应的映射对象列表
     * 解决类型擦除的问题
     */
    fun <T> toList(jsonStr: String?, clz: Class<T>): List<T> {
        var list: List<T> = gson.fromJson(jsonStr, type(clz))
        if (list == null) list = ArrayList()
        return list
    }

    fun <T> toMap(jsonStr: String?, clz: Class<T>): Map<String?, T> {
        var map: Map<String?, T> = gson.fromJson(jsonStr, type(clz))
        if (map == null) map = HashMap()
        return map
    }

    fun toMap(jsonStr: String?): Map<String, Any> {
        val type: Type = object : TypeToken<Map<String?, Any?>?>() {}.type
        return gson.fromJson(jsonStr, type)
    }

    private class type(private val type: Type) : ParameterizedType {
        override fun getActualTypeArguments(): Array<Type> {
            return arrayOf(type)
        }

        override fun getRawType(): Type {
            return ArrayList::class.java
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }

    /**
     * 实现了 序列化 接口    对为null的字段进行转换
     */
    internal class StringConverter : JsonSerializer<String?>, JsonDeserializer<String?> {
        //字符串为null 转换成"",否则为字符串类型
        @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): String {
            return json.getAsJsonPrimitive().getAsString()
        }

        override fun serialize(
            src: String?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return if (src == null || src == "null") JsonPrimitive("") else JsonPrimitive(src.toString())
        }
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
                        dbNum.removeEndZeros()
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
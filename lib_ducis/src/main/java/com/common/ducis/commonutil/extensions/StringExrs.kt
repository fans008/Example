package com.common.ducis.commonutil.extensions

import java.util.regex.Pattern


/**
 * @ClassName: StringExrs
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/11/5
 */

//判断是否符合手机规则
fun String.isMobile(): Boolean {
    if (this.isEmpty())
        return false
    val regExp =
        "^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$"
    val pattern = Pattern.compile(regExp)
    val matcher = pattern.matcher(this)
    return matcher.matches()
}

//判断账号密码是否符合校验规则
fun String.isAccountRule(): Boolean{
    var isDigit = false//定义一个boolean值，用来表示是否包含数字
    var isLowerCase = false//定义一个boolean值，用来表示是否包含字母
    var isUpperCase = false
    for (i in 0 until this.length) {
        if (Character.isDigit(this[i])) {   //用char包装类中的判断数字的方法判断每一个字符
            isDigit = true
        } else if (Character.isLowerCase(this[i])) {  //用char包装类中的判断字母的方法判断每一个字符
            isLowerCase = true
        } else if (Character.isUpperCase(this[i])) {
            isUpperCase = true
        }
    }
    val regex = "^[a-zA-Z0-9]+$"
    return isDigit && isLowerCase && isUpperCase && this.matches(regex.toRegex())
}

//是否包含数字
fun String.isContainNumber(): Boolean {
    var isDigit = false//定义一个boolean值，用来表示是否包含数字
    for (element in this) {
        if (Character.isDigit(element)) {   //用char包装类中的判断数字的方法判断每一个字符
            isDigit = true
        }
    }
    return isDigit
}

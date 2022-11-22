package com.common.ducis.commonutil.extensions

import com.common.ducis.commonutil.CommonUtils


/**
 * @ClassName: StringExrs
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2021/11/5
 */

//判断是否符合手机规则
val String.isMobile: Boolean
    get() = CommonUtils.isMobile(this)

//判断账号密码是否符合校验规则
val String.isAccountRule: Boolean
    get() = CommonUtils.isContainAccount(this)

fun String.isContainNumber():Boolean
{
    var isDigit = false//定义一个boolean值，用来表示是否包含数字
    for (element in this) {
        if (Character.isDigit(element)) {   //用char包装类中的判断数字的方法判断每一个字符
            isDigit = true
        }
    }
    return isDigit
}

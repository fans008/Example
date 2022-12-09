package com.common.ducis.base.model

/**
 * @ClassName: BaseListParam
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/6/20
 */
class BaseListParam {
    var pageNo = 1
    var pageSize = 20
    var keyword: String? = null
    fun correct() {
        if (pageNo <= 0) {
            pageNo = 1
        }
        if (pageSize <= 0) {
            pageSize = 20
        }
    }
}
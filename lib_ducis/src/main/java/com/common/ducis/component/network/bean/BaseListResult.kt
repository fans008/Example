package com.common.ducis.component.network.bean

/**
 * @ClassName: BaseListResult
 * @Description:网络返回默认列表格式
 * @Author: Fan TaoTao
 * @Date: 2022/3/9
 */
open class BaseListResult<R> {

    open val list:MutableList<R>? = null

    open val page: PageParam? = PageParam()

    /**
     * 是否能加载更多
     * @return Boolean
     */
    fun canLoadMore():Boolean{
        if (page == null){
            return false
        }
        return page!!.pageNo < page!!.totalPages
    }

    /**
     * @property pageNo Int 当前页
     * @property pageSize Int 当页条数
     * @property totalPages Int 总页数
     * @property totalRows Int 总条数
     * @constructor
     */
    data class PageParam(val pageNo:Int = 1, val pageSize:Int = 20, val totalPages:Int = 0,val totalRows:Int = 0)
}
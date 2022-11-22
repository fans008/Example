package com.common.ducis.network.bean.common

/**
 * @ClassName: CommonStatusView
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/10/22
 */
data class CommonStatusView(val clientId:String) {

    /**
     * 用户姓名
     */
    val name:String? = null
    /**
     * 用户手机号
     */
    val phone:String? = null
    /**
     * url配置,名称和实际URL的映射关系
     */
    val urls:Map<String,String> = mapOf()
}
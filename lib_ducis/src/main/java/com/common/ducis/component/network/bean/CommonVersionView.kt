package com.common.ducis.component.network.bean

/**
 * @ClassName: CommonVersionView
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/10/22
 */
data class CommonVersionView(val versionId: Long, val appVersionId: Long, var downloadUrl: String?) {

    /**
     * 版本描述
     */
    val versionDescribe: String? = null

    /**
     * 版本名称
     */
    val versionName: String? = null

    /**
     * 強制更新
     */
    val force: Boolean = false

    /**
     * 创建日期
     */
    val createDate: String? = null

    /**
     * 文件hash值
     */
    val checksum: String? = null

    /**
     * 服务端使用参数
     */
    val usableVersionId: Long? = null
}
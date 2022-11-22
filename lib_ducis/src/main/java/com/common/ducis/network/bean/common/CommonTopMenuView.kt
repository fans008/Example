package com.common.ducis.network.bean.common

/**
 * @ClassName: CommonTopMenuView
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/10/22
 */
data class CommonTopMenuView(val htmlVersionId:Long) {

    var items:MutableList<MenuItemView> = mutableListOf()

    data class MenuItemView(val name:String,val text:String,val url:String){
        /**
         * 图标
         */
        private val icon: IconView? = null

        /**
         * 图标(选中状态)
         */
        private val activeIcon: IconView? = null

        /**
         * 图标(禁用状态)
         */
        private val disabledIcon: IconView? = null
    }

    data class IconView(val url:String){
        /**
         * 宽度
         */
        private val width = 0

        /**
         * 高度
         */
        private val height = 0

        /**
         * 在精灵图中的水平位置
         */
        private val xPosition = 0

        /**
         * 在精灵图中的垂直位置
         */
        private val yPosition = 0
    }
}
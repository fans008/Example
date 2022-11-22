package com.common.ducis.base.model;

/**
 * @ClassName: BaseListParam
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/6/20
 */
public class BaseListParam {
    private int pageNo = 1;
    private int pageSize = 20;
    private String keyword;


    public int getPageNo() {
        return pageNo;
    }
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void correct() {
        if (this.pageNo <= 0) {
            this.pageNo = 1;
        }

        if (this.pageSize <= 0) {
            this.pageSize = 10;
        }
    }
}

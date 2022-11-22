package com.common.ducis.base.model;


import java.util.List;

/**
 * @ClassName: BasePageListView
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/6/20
 */
public class BasePageListView<T> extends BaseListView<T> {

    private Page page = new Page();

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public static final class Page {
        private Integer pageNo;
        private Integer pageSize;
        private Integer totalPages;
        private Integer totalRows;

        public Integer getPageNo() {
            return pageNo;
        }

        public void setPageNo(Integer pageNo) {
            this.pageNo = pageNo;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public void setPageSize(Integer pageSize) {
            this.pageSize = pageSize;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
        }

        public Integer getTotalRows() {
            return totalRows;
        }

        public void setTotalRows(Integer totalRows) {
            this.totalRows = totalRows;
        }
    }

    @Override
    public void setPagedList(List<T> fullList, int pageNo, int pageSize) {
        this.page.setPageNo(pageNo);
        this.page.setPageSize(pageSize);
        if (fullList == null || fullList.size() == 0) {
            this.page.setTotalPages(0);
            this.page.setTotalRows(0);
            return;
        }
        super.setPagedList(fullList, pageNo, pageSize);
        this.page.setTotalPages((fullList.size() + pageSize - 1) / pageSize);
        this.page.setTotalRows(fullList.size());
    }
}

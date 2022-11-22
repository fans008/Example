package com.common.ducis.base.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: BaseListView
 * @Description:
 * @Author: Fan TaoTao
 * @Date: 2020/6/20
 */
public class BaseListView<T> {
    private List<T> list = new ArrayList<T>();

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void setPagedList(List<T> fullList, int pageNo, int pageSize) {
        if (pageNo <= 0) {
            pageNo = 0;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        int start = (pageNo - 1) * pageSize;
        int end = start + pageSize;

        if (start >= fullList.size()) {
            return;
        }

        if (end > fullList.size()) {
            end = fullList.size();
        }

        this.list.addAll(fullList.subList(start, end));
    }
}

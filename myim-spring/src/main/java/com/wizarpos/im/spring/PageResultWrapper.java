package com.wizarpos.im.spring;

import java.util.List;

/**
 * 分页包装器
 */
public abstract class PageResultWrapper<T> {

    public PageResultWrapper() {}

    public PageResultWrapper(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> paging(int pageNo) throws Exception {
        this.pageNo = pageNo;
        totalNo = queryTotalCount();
        if (totalNo != 0) {
            return (pageResult = query(calPageNo(), pageSize));
        }
        return null;
    }

    public abstract int queryTotalCount() throws Exception;

    public abstract List<T> query(int pageNo, int pageSize) throws Exception;

    /**
     * 查询的起始索引位置
     * @return
     */
    public int getStartIndex() {
    	return (pageNo - 1) * pageSize;
    }

    public int calPageNo() {
        if (pageNo < 1) {
        	pageNo = 1;
        }
        int totalPageNo = getTotalPageNo();
        if (pageNo > totalPageNo) {
        	pageNo = totalPageNo;
        }
        nextNo = pageNo + 1;
        if (nextNo > totalPageNo) {
        	nextNo = totalPageNo;
        }
        preNo = pageNo - 1;
        if (preNo < 1) {
        	preNo = 1;
        }
        return pageNo;
    }



    /**
     * 总页数。如果总记录数为 0，页数为1.
     * @return
     */
    public int getTotalPageNo() {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("每页的记录数量应当 > 0");
        }
        if (totalNo == 0) {
            return 1;
        }
        if (totalNo % pageSize == 0) {
            return totalNo / pageSize;
        }
        return totalNo / pageSize + 1;
    }

    public List<T> getPageResult() {
    	return this.pageResult;
    }

    public void setPageResult(List<T> list) {
    	this.pageResult = list;
    }

    /**
     * 总记录数
     * @return
     */
    public int getTotalNo() {
        return this.totalNo;
    }

    /**
     * 每页数量
     * @return
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * 当前页码
     * @return
     */
    public int getPageNo() {
        return this.pageNo;
    }

    public int getPreNo() {
    	return this.preNo;
    }

    public int getNextNo() {
    	return this.nextNo;
    }

    protected List<T> pageResult;

    /** 下一页 */
    protected int nextNo;

    /** 上一页 */
    protected int preNo;

    /** 总记录数 */
    private int totalNo = 0;

    /** 页码 */
    private int pageNo = 0;

    /** 每页数量 */
    private int pageSize = 4;
}

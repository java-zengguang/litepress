package io.github.java_zengguang.litepress.core.bean.entity;

import java.util.List;

/**
 * 分页结果，统一承载"列表数据 + 分页元信息"。
 *
 * <p>避免把 {@link PageEntity} 直接铺到响应信封上，让分页字段只在真正分页时出现。
 * 典型用法：{@code MessageBean.ok(PageResult.of(list, page))}。</p>
 *
 * @param <T> 列表元素类型
 */
public class PageResult<T> extends MainModel {
    private static final long serialVersionUID = 1L;

    /** 当前页数据列表 */
    private List<T> list;

    /** 分页元信息 */
    private PageEntity page;

    public PageResult() {
    }

    public PageResult(List<T> list, PageEntity page) {
        this.list = list;
        this.page = page;
    }

    /**
     * 快速构造一个分页结果。
     */
    public static <T> PageResult<T> of(List<T> list, PageEntity page) {
        return new PageResult<>(list, page);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public PageEntity getPage() {
        return page;
    }

    public void setPage(PageEntity page) {
        this.page = page;
    }
}
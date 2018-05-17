package com.xyz327.queryfilter.service;

import com.baomidou.mybatisplus.service.IService;
import com.xyz327.queryfilter.vo.Filter;
import com.xyz327.queryfilter.vo.Where;
import java.io.Serializable;
import java.util.List;

/**
 * @author xizhou
 */
public interface QueryFilterService<T> extends IService<T> {

    Serializable insertAndReturnId(T entity);

    List<T> find(Filter filter);

    T findOne(Filter filter);

    int count(Where where);

    int deleteAll(Where where);

    void updateById(String id, T entity);

    Boolean exists(String id);
}

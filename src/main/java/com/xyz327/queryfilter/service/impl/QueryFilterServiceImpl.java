package com.xyz327.queryfilter.service.impl;

import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.entity.TableInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xyz327.queryfilter.service.QueryFilterService;
import com.xyz327.queryfilter.utils.FilterConditionBuilder;
import com.xyz327.queryfilter.vo.Filter;
import com.xyz327.queryfilter.vo.Where;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author xizhou
 */
public abstract class QueryFilterServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements
    IService<T>,
    QueryFilterService<T> {

    private Class<T> modelClass;
    private AtomicBoolean initTableInfo = new AtomicBoolean(false);
    private Field keyFiled;
    /**
     * Entity的属性名和表明的对应map fieldName -> column
     */
    protected Map<String, TableFieldInfo> fieldColumnMap;
    private TableInfo tableInfo;

    public QueryFilterServiceImpl() {
        modelClass = currentModelClass();
    }

    public void initTableInfo() {
        if(initTableInfo.compareAndSet(false, true)) {
            tableInfo = SqlHelper.table(modelClass);
            Map<String, TableFieldInfo> map = tableInfo.getFieldList().stream()
                .collect(Collectors.toMap(TableFieldInfo::getProperty, o->o));
            fieldColumnMap = Collections.unmodifiableMap(map);
            keyFiled = ReflectionUtils.findField(modelClass, tableInfo.getKeyProperty());
        }
    }


    @Override
    public Serializable insertAndReturnId(T entity) {
        insert(entity);
        return (Serializable) ReflectionUtils.getField(keyFiled, entity);
    }

    @Override
    public List<T> find(Filter filter) {
        initTableInfo();
        Condition condition = FilterConditionBuilder.buildCondition(filter, modelClass, fieldColumnMap);
        Page<T> page = new Page<>(filter.getCurrentPage(), filter.getPageSize());
        // 不去查询总记录数
        page.setSearchCount(false);
        Page<T> ret = selectPage(page, condition);
        return ret.getRecords();
    }

    @Override
    public T findOne(Filter filter) {
        initTableInfo();
        filter.setCurrentPage(1);
        filter.setPageSize(1);
        return find(filter).get(0);
    }

    @Override
    public int count(Where where) {
        initTableInfo();
        Filter filter = new Filter();
        filter.setWhere(where);
        Condition condition = FilterConditionBuilder.buildCondition(filter, modelClass, fieldColumnMap);
        return selectCount(condition);
    }
    @Override
    public int deleteAll(Where where){
        initTableInfo();
        Filter filter = new Filter();
        filter.setWhere(where);
        Condition condition = FilterConditionBuilder.buildCondition(filter, modelClass, fieldColumnMap);
        boolean delete = delete(condition);
        return 1;
    }

    @Override
    public void updateById(String id, T entity) {
        ReflectionUtils.setField(keyFiled, entity, id);
        updateById(entity);
    }

    @Override
    public Boolean exists(String id) {
        return selectById(id) != null;
    }
}

package com.xyz327.queryfilter.utils;

import com.alibaba.fastjson.util.TypeUtils;
import com.baomidou.mybatisplus.entity.TableFieldInfo;
import com.baomidou.mybatisplus.mapper.Condition;
import com.xyz327.queryfilter.vo.Filter;
import com.xyz327.queryfilter.vo.Where;
import com.xyz327.queryfilter.vo.WhereEntry;
import com.xyz327.queryfilter.vo.WhereOperator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xizhou
 */
public class FilterConditionBuilder {

    public static Condition buildCondition(Filter filter, Class modelClass,
        Map<String, TableFieldInfo> fieldColumnMap) {
        Condition condition = Condition.create();
        Where where = new Where(filter.getWhere());
        buildWhere(condition, where, modelClass, fieldColumnMap);

        buildFields(condition, filter.getFields());
        List<String> order = filter.getOrder();
        buildOrder(condition, order);
        return condition;
    }

    private static void buildFields(Condition condition, Map<String, Boolean> fields) {
        if(fields == null || fields.size() == 0){
            return;
        }
        List<String> includes = new ArrayList<>();
        List<String> excludes = new ArrayList<>();
        fields.forEach((field, isInclude) -> {
            if(isInclude){
                includes.add(field);
            }else {
                excludes.add(field);
            }
        });
        if(excludes.size() > 0){
            // TODO: 18-5-17 实现排除某些字段
            //return;
        }
        if(includes.size() > 0){
            condition.setSqlSelect(includes.toArray(new String[includes.size()]));
        }

    }

    private static void buildOrder(Condition condition, List<String> orderList) {
        if(orderList == null){
            return;
        }
        orderList.forEach(s -> {
            String fieldName = "";
            boolean isAsc = false;
            String[] split = s.split("\\s+");
            if(split.length == 0){
                return;
            }
            fieldName = split[0];
            if(split.length == 2){
                String order = split[1];
                isAsc = "ASC".equalsIgnoreCase(order);
            }
            condition.orderBy(fieldName, isAsc);
        });
    }

    public static void buildWhere(Condition condition, Where where, Class modelClass,
        Map<String, TableFieldInfo> fieldColumnMap) {
        if(where == null){
            return;
        }
        where.forEach((key, whereEntry) -> {
            if (WhereOperator.and.name().equalsIgnoreCase(key) ||
                WhereOperator.or.name().equalsIgnoreCase(key)) {
                buildCompositionWhereEntry(condition, WhereOperator.valueOf(key), (List<Where>) whereEntry, modelClass,
                    fieldColumnMap);
            } else {
                if (whereEntry instanceof String) {
                    whereEntry = new WhereEntry(whereEntry);
                }
                TableFieldInfo tableFieldInfo = fieldColumnMap.get(key);
                if (tableFieldInfo == null) {
                    throw new IllegalArgumentException(modelClass + "中不存在属性" + key);
                }
                buildWhereEntry(condition, key, tableFieldInfo.getPropertyType(), (WhereEntry) whereEntry, modelClass, fieldColumnMap);
            }
        });
    }

    public static void buildCompositionWhereEntry(Condition condition, WhereOperator whereOperator,
        List<Where> composition, Class modelClass, Map<String, TableFieldInfo> fieldColumnMap) {
        switch (whereOperator) {
            case and:
                condition.andNew();
                break;
            case or:
                condition.orNew();
                break;
            default:
                throw new UnsupportedOperationException("不支持的操作:" + whereOperator);
        }
        composition.forEach(where -> {
            if (where.size() > 1) {
                throw new IllegalArgumentException("参数只能为一对key:value" + where);
            }
            buildWhere(condition, where, modelClass, fieldColumnMap);
        });
    }
    private static Object fixDateValue(Class<?> fieldType, Object value) {
        if (Timestamp.class.isAssignableFrom(fieldType)){
            return TypeUtils.castToTimestamp(value);
        }
        if(java.sql.Date.class.isAssignableFrom(fieldType)){
            return TypeUtils.castToSqlDate(value);
        }
        if (Date.class.isAssignableFrom(fieldType)) {
            return TypeUtils.castToDate(value);
        }
        return value;
    }

    public static Object fixValue(String key, Class<?> fieldType, Object value) {
        return value;
    }

    public static String fixColumnName(String fieldName, Class modelClass, Map<String, TableFieldInfo> fieldColumnMap) {
        TableFieldInfo tableFieldInfo = fieldColumnMap.get(fieldName);
        if (tableFieldInfo == null) {
            throw new IllegalArgumentException(modelClass + "中不存在属性" + fieldName);
        }
        return tableFieldInfo.getColumn();
    }

    public static void buildWhereEntry(Condition condition, String fieldName, Class<?> fieldType, WhereEntry whereEntry,
        Class modelClass, Map<String, TableFieldInfo> fieldColumnMap) {
        WhereOperator operator = whereEntry.getOperator();
        Object value = whereEntry.getValue();
        fieldName = fixColumnName(fieldName, modelClass, fieldColumnMap);
        value = fixDateValue(fieldType, value);
        value = fixValue(fieldName, fieldType, value);

        switch (operator) {
            case eq:
                condition.eq(fieldName, value);
                break;
            case neq:
                condition.ne(fieldName, value);
                break;
            case gt:
                condition.gt(fieldName, value);
                break;
            case gte:
                condition.ge(fieldName, value);
                break;
            case lt:
                condition.lt(fieldName, value);
                break;
            case lte:
                condition.le(fieldName, value);
                break;
            case like:
                condition.like(fieldName, value.toString());
                break;
            case nlike:
                condition.notLike(fieldName, value.toString());
                break;
            case isNull:
                condition.isNull(fieldName);
                break;
            case notNull:
                condition.isNotNull(fieldName);
                break;
            case between: {
                if (!(value instanceof Collection) || ((Collection) value).size() != 2) {
                    throw new IllegalArgumentException("between条件的值需要为长度为2的数组");
                }
                Collection collection = (Collection) value;
                Object[] array = collection.toArray(new Object[2]);
                condition.between(fieldName, array[0], array[1]);
            }
            break;
            case in:
            case nin: {
                if (!(value instanceof Collection)) {
                    throw new IllegalArgumentException(operator + "条件的值需要为数组");
                }
                if (operator == WhereOperator.in) {
                    condition.in(fieldName, (Collection) value);
                } else {
                    condition.notIn(fieldName, (Collection) value);
                }
            }
            break;
            default:
                throw new UnsupportedOperationException("不支持的操作:" + operator);
        }
    }


}

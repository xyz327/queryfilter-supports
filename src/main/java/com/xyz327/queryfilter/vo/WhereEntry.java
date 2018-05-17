package com.xyz327.queryfilter.vo;

import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;

/**
 * Created by kiway067 on 17-8-31.
 */
@Data
public class WhereEntry {

    private WhereOperator operator;
    private Object value;

    public WhereEntry() {
    }

    /**
     * 默认为{@link WhereOperator#eq}
     * @param value
     */
    public WhereEntry(Object value) {
        this.value = value;
        setOperator(WhereOperator.eq);
    }

    public WhereEntry(WhereOperator operator, Object value) {
        this.operator = operator;
        this.value = value;
    }

    public static WhereEntry valueOf(Map<String, Object> map) {
        WhereEntry whereEntry = new WhereEntry();
        if (map.size() > 1) {
            throw new IllegalArgumentException("where参数只能为一对key:value" + map);
        }
        for (Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            whereEntry.setOperator(WhereOperator.valueOf(key));
            whereEntry.setValue(value);
        }
        return whereEntry;
    }

    @Override
    public String toString() {
        return "{" + operator.name() + ":" + value + "}";
    }
}

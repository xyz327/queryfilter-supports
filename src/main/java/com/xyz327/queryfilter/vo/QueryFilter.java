package com.xyz327.queryfilter.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by xizhou on 17-8-29.
 */
@Slf4j
public class QueryFilter {

    private Filter obj;
    @Getter
    @Setter
    private String filter;

    public QueryFilter() {
    }

    public QueryFilter(String filter) {
        this.filter = filter;
    }

    public Filter toFilter() {
        if (obj == null) {
            if (filter == null || "".equals(filter) || (!filter.startsWith("{") && !filter.endsWith("}"))) {
                obj = new Filter();
                return obj;
            }
            JSONObject jsonObject = JSONObject.parseObject(this.filter);
            obj = jsonObject.toJavaObject(Filter.class);
            JSONObject whereJson = jsonObject.getJSONObject("where");
            Integer pageSize = jsonObject.getInteger("pageSize");
            if(pageSize == null) {
               pageSize = jsonObject.getInteger("limit");
            }
            if(pageSize == null){
                pageSize = obj.getPageSize();
            }
            if(pageSize <= 0){
                throw new IllegalArgumentException("limit或pageSize不能为0");
            }
            Integer skip = jsonObject.getInteger("skip");
            if(skip == null){
                skip = obj.getSkip();
            }
            Integer currentPage = jsonObject.getInteger("currentPage");
            if (currentPage == null) {
                currentPage = (obj.getSkip() / pageSize) + 1;
            } else {
                skip = (currentPage - 1) * pageSize;
            }
            obj.setLimit(pageSize);
            obj.setPageSize(pageSize);
            obj.setSkip(skip);
            obj.setCurrentPage(currentPage);
            Where where = new Where();
            if (whereJson == null) {
                whereJson = new JSONObject();
            }
            for (Entry<String, Object> next : whereJson.entrySet()) {
                String key = next.getKey();
                Object o = next.getValue();
                if (WhereOperator.and.name().equalsIgnoreCase(key) ||
                    WhereOperator.or.name().equalsIgnoreCase(key)) {
                    if (!(o instanceof Collection)) {
                        throw new IllegalArgumentException("and/or需要为数组");
                    }
                    List<Where> composition = new ArrayList<>();
                    for (Object subEntry : (Collection) o) {
                        if (subEntry instanceof Map) {
                            for (Entry<String, Object> entryNext : ((Map<String, Object>) subEntry).entrySet()) {
                                String subKey = entryNext.getKey();
                                Object subVal = entryNext.getValue();
                                Where subWhere = new Where();
                                subWhere.put(subKey, toWhereEntity(subVal));
                                composition.add(subWhere);
                            }
                        }
                    }
                    where.put(key, composition);
                    continue;
                }
                if (!(o instanceof Map)) {
                    o = new JSONObject().fluentPut(WhereOperator.eq.name(), o);
                }
                where.put(key, WhereEntry.valueOf((Map<String, Object>) o));
            }
            obj.setWhere(where);
        }
        return obj;
    }

    private WhereEntry toWhereEntity(Object o) {
        if (!(o instanceof Map)) {
            o = new JSONObject().fluentPut(WhereOperator.eq.name(), o);
        }
        return WhereEntry.valueOf((Map<String, Object>) o);
    }
}

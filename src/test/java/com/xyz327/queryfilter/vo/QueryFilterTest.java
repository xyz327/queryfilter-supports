package com.xyz327.queryfilter.vo;

import com.alibaba.fastjson.parser.ParserConfig;

/**
 * @author xizhou
 */
public class QueryFilterTest {

    @org.junit.Test
    public void toFilter() throws Exception {
        ParserConfig globalInstance = ParserConfig.getGlobalInstance();
       // globalInstance.putDeserializer(Where.class, WhereDeserializer.Instance);
        String filterStr = "{\"limit\":10, \"where\":{\"username\":\"zhangsan\"}, \"fields\":{\"username\":true}, \"order\": [\"created asc\"]}";
        QueryFilter queryFilter = new QueryFilter(filterStr);
        Filter filter = queryFilter.toFilter();
    }

}
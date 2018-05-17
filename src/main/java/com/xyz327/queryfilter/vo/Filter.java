package com.xyz327.queryfilter.vo;

import com.alibaba.fastjson.JSONObject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * Created by kiway067 on 17-8-31.
 */
@Data
public class Filter {
    public static final int DEFAULT_PAGE_SIZE = 10;
    // 等同于pageSize
    private int limit = DEFAULT_PAGE_SIZE;
    // 等同于 limit 优先级比 limit 高 如果设置了 pageSize 就会覆盖limit的值
    private int pageSize = DEFAULT_PAGE_SIZE;
    // 要跳过的数据条数
    private int skip = 0;
    //当前页数 设置了 currentPage 会计算出skip并覆盖
    private int currentPage = 1;

    private Map<String, Object> where = Collections.emptyMap();

    private Map<String, Boolean> fields = Collections.emptyMap();
    private List<String> order = Collections.emptyList();

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

package com.xyz327.queryfilter.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by kiway067 on 17-8-29.
 */
public class QueryWhere {
    @Getter
    @Setter
    private String where;

    public QueryWhere() {

    }

    public QueryWhere(String where) {
        this.where = where;
    }

    public Where toWhere(){
        String filter = "{\"where\": "+where+"}";
        return new Where(new QueryFilter(filter).toFilter().getWhere());
    }
}

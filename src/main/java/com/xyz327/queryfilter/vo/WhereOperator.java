package com.xyz327.queryfilter.vo;

/**
 * Created by kiway067 on 17-8-31.
 */
public enum WhereOperator {
    eq,neq,and,or,gt,gte,lt,lte,in,nin,between,like,nlike, isNull,notNull;

    @Override
    public String toString() {
        return this.name();
    }
}

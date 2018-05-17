package com.xyz327.queryfilter.spring.web;

import com.alibaba.fastjson.JSONObject;
import com.xyz327.queryfilter.service.QueryFilterService;
import com.xyz327.queryfilter.vo.QueryFilter;
import com.xyz327.queryfilter.vo.QueryWhere;
import io.swagger.annotations.ApiOperation;
import java.io.Serializable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xizhou
 */
public abstract class BaseCrudController<S extends QueryFilterService<T>, T> {

    @Autowired
    private S queryFilterService;

    @GetMapping("/")
    @ApiOperation("查询列表")
    public ResponseEntity<List<T>> list(QueryFilter queryFilter) {
        return ResponseEntity.ok(queryFilterService.find(queryFilter.toFilter()));
    }
    @PostMapping("/")
    @ApiOperation("新增用户")
    public ResponseEntity save(T entity) {
        Serializable id = queryFilterService.insertAndReturnId(entity);
        return ResponseEntity.ok(queryFilterService.selectById(id));
    }

    @DeleteMapping
    @ApiOperation("条件删除")
    public ResponseEntity deleteAll(QueryWhere queryWhere) {
        int count = queryFilterService.deleteAll(queryWhere.toWhere());
        return ResponseEntity.ok(new JSONObject().fluentPut("count", count));
    }
    @GetMapping("/count")
    @ApiOperation("统计数量")
    public ResponseEntity count(QueryWhere queryWhere) {
        int count = queryFilterService.count(queryWhere.toWhere());
        return ResponseEntity.ok(new JSONObject().fluentPut("count", count));
    }

    @GetMapping("/findOne")
    @ApiOperation("findOne")
    public ResponseEntity findOne(QueryFilter queryFilter) {
        T one = queryFilterService.findOne(queryFilter.toFilter());
        return ResponseEntity.ok(one);
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查找")
    public ResponseEntity findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(queryFilterService.selectById(id));
    }
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除")
    public ResponseEntity deleteById(@PathVariable("id") String id) {
        queryFilterService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    @ApiOperation("根据id更新")
    public ResponseEntity patchById(@PathVariable("id") String id, @RequestBody T user) {
        queryFilterService.updateById(id, user);
        return ResponseEntity.ok(queryFilterService.selectById(id));
    }
    @GetMapping("{id}/exists")
    @ApiOperation("判断是否存在")
    public ResponseEntity exists(@PathVariable("id") String id) {
        Boolean exists = queryFilterService.exists(id);
        return ResponseEntity.ok(new JSONObject().fluentPut("exists", exists));
    }

}

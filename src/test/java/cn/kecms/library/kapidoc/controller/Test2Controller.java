package cn.kecms.library.kapidoc.controller;

import cn.kecms.library.kapidoc.request.ListQueryRequest;
import cn.kecms.library.kapidoc.request.LongIdsRequest;
import cn.kecms.library.kapidoc.request.TestAddRequest;
import cn.kecms.library.kapidoc.dto.TestDto;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class Test2Controller {
    /**
     * 列表
     */
    @GetMapping("/list")
    public List<TestDto> getList() {
        return Collections.emptyList();
    }

    /**
     * qqq列表2
     * @param jjjids ids说明
     */
    @GetMapping("/list2")
    public List<TestDto> getList2(List<Long> jjjids) {
        return Collections.emptyList();
    }

    /**
     * 详情
     */
    @GetMapping("/get")
    public TestDto getDetail(@RequestParam Long id) {
        return null;
    }

    /**
     * 添加
     */
    @PostMapping("/add")
    public TestDto add(@RequestBody TestAddRequest request) {
        return null;
    }

    /**
     * 修改
     */
    @PutMapping("/{id}")
    public TestDto add(@PathVariable Long id, @RequestBody TestAddRequest request) {
        return null;
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    public void delete(@RequestBody LongIdsRequest request) {

    }
}

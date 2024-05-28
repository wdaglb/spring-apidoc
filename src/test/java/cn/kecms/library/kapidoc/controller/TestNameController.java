package cn.kecms.library.kapidoc.controller;

import cn.kecms.library.kapidoc.dto.TestDto;
import cn.kecms.library.kapidoc.request.ListQueryRequest;
import cn.kecms.library.kapidoc.request.LongIdsRequest;
import cn.kecms.library.kapidoc.request.TestAddRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 目录1/testName
 */
@RestController
@RequestMapping("/name")
public class TestNameController {
    /**
     * 列表
     */
    @GetMapping("/list")
    public List<TestDto> getList() {
        return Collections.emptyList();
    }

    /**
     * 列表2
     */
    @GetMapping("/list2")
    public List<TestDto> getList2(ListQueryRequest request) {
        System.out.println(request.getCurrent());
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

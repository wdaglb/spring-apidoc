package cn.kecms.library.kapidoc.controller;

import cn.kecms.library.kapidoc.request.ListQueryRequest;
import cn.kecms.library.kapidoc.request.TestAddRequest;
import cn.kecms.library.kapidoc.dto.TestDto;
import cn.kecms.library.kapidoc.request.LongIdsRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 测试
 * 描述文本1
 * 描述文本2
 */
@RestController
@RequestMapping("/test")
public class TestController {
    /**
     * test列表
     */
    @GetMapping(path = "/list")
    public List<TestDto> getList() {
        return Collections.emptyList();
    }

    /**
     * test列表2
     */
    @GetMapping("/list2")
    public List<TestDto> getList2(ListQueryRequest request) {
        return Collections.emptyList();
    }

    /**
     * query详情
     * @param id id说明
     */
    @GetMapping("/get")
    public TestDto getDetail(@RequestParam Long id) {
        return null;
    }

    /**
     * path详情
     * @param id id说明
     */
    @GetMapping("/{id}")
    public TestDto getDetailPath(@PathVariable Long id) {
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

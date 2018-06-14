package com.fulu.game.admin.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.CdkGroup;
import com.fulu.game.core.service.CdkGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/cdk")
@Slf4j
public class CdkController {

    @Autowired
    private CdkGroupService cdkGroupService;

    @PostMapping("/group/create")
    public Result create(@Valid CdkGroup cdkGroup) {
        Boolean success = cdkGroupService.generate(cdkGroup);
        if (success){
            return Result.success().msg("cdk生成成功");
        }else{
            return Result.error().msg("cdk生成异常");
        }
    }
}

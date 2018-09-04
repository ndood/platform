package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.DynamicFile;
import lombok.Data;

import java.util.List;


/**
 * 动态表
 *
 * @author shijiaoyun
 * @date 2018-08-30 10:31:41
 */
@Data
public class DynamicVO  extends Dynamic {
    private List<DynamicFile> dynamicFiles;
    private String[] urls;

    private String startTime;
    private String endTime;
    private String fileUrls;

    private String keyword;
}

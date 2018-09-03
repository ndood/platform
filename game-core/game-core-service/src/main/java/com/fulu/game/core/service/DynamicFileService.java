package com.fulu.game.core.service;

import com.fulu.game.core.entity.DynamicFile;
import com.fulu.game.core.entity.vo.DynamicFileVO;


/**
 * 动态文件表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 11:02:13
 */
public interface DynamicFileService extends ICommonService<DynamicFile,Long>{

    /**
     * 保存动态文件
     * @param dynamicFileVO
     */
    public void save(DynamicFileVO dynamicFileVO);
}

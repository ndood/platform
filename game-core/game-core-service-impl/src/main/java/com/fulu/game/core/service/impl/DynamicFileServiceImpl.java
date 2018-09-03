package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.DynamicFileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicFileDao;
import com.fulu.game.core.entity.DynamicFile;
import com.fulu.game.core.service.DynamicFileService;



@Service("dynamicFileService")
public class DynamicFileServiceImpl extends AbsCommonService<DynamicFile,Long> implements DynamicFileService {

    @Autowired
	private DynamicFileDao dynamicFileDao;



    @Override
    public ICommonDao<DynamicFile, Long> getDao() {
        return dynamicFileDao;
    }

    /**
     * 保存动态文件
     *
     * @param dynamicFileVO
     */
    @Override
    public void save(DynamicFileVO dynamicFileVO) {
        dynamicFileDao.create(dynamicFileVO);
    }
}

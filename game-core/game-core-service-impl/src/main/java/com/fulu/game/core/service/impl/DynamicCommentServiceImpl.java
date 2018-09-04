package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.vo.DynamicCommentVO;
import com.fulu.game.core.service.DynamicService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.DynamicCommentDao;
import com.fulu.game.core.entity.DynamicComment;
import com.fulu.game.core.service.DynamicCommentService;

import java.util.List;


@Service("dynamicCommentService")
public class DynamicCommentServiceImpl extends AbsCommonService<DynamicComment,Long> implements DynamicCommentService {

    @Autowired
	private DynamicCommentDao dynamicCommentDao;

    @Autowired
    private DynamicService dynamicService;



    @Override
    public ICommonDao<DynamicComment, Long> getDao() {
        return dynamicCommentDao;
    }

    /**
     * 评论接口
     *
     * @param dynamicCommentVO
     */
    @Override
    public void save(DynamicCommentVO dynamicCommentVO) {
        dynamicCommentDao.create(dynamicCommentVO);
        dynamicService.updateIndexFilesById(dynamicCommentVO.getId(), false, 0, 1,false);
        // TODO shijiaoyun 此处需要发送Jpush消息，通知被评论用户

    }

    /**
     * 获取评论列表
     *
     * @param pageNum
     * @param pageSize
     * @param dynamicId
     * @return
     */
    @Override
    public PageInfo<DynamicCommentVO> list(Integer pageNum, Integer pageSize, Long dynamicId) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        DynamicCommentVO dynamicCommentVO = new DynamicCommentVO();
        dynamicCommentVO.setDynamicId(dynamicId);
        List<DynamicCommentVO> list = dynamicCommentDao.list(dynamicCommentVO);
        return new PageInfo<>(list);
    }


}

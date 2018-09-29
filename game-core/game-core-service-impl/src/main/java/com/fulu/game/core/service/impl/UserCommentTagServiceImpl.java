package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.UserCommentTagDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.UserCommentTag;
import com.fulu.game.core.entity.vo.UserCommentTagVO;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.TagService;
import com.fulu.game.core.service.UserCommentTagService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class UserCommentTagServiceImpl extends AbsCommonService<UserCommentTag, Integer> implements UserCommentTagService {

    @Autowired
    private UserCommentTagDao userCommentTagDao;

    @Autowired
    private TagService tagService;


    public void create(UserComment comment, List<Integer> tagIds) {
        List<Tag> tagList = tagService.findByTagIds(tagIds);
        for (Tag tag : tagList) {
            UserCommentTag commentTag = new UserCommentTag();
            commentTag.setUserId(comment.getServerUserId());
            commentTag.setCommentId(comment.getId());
            commentTag.setTechAuthId(comment.getTechAuthId());
            commentTag.setTagId(tag.getId());
            commentTag.setTagName(tag.getName());
            commentTag.setTechAuthId(comment.getTechAuthId());
            commentTag.setCreateTime(new Date());
            create(commentTag);
        }
    }

    public List<UserCommentTag> findByCommentId(int commentId) {
        UserCommentTagVO param = new UserCommentTagVO();
        param.setCommentId(commentId);
        return userCommentTagDao.findByParameter(param);
    }

    @Override
    public PageInfo<UserCommentTag> findByServerId(Integer pageNum, Integer pageSize, Integer serverId) {
        UserCommentTagVO param = new UserCommentTagVO();
        param.setUserId(serverId);
        PageHelper.startPage(pageNum, pageSize, "create_time desc");
        List<UserCommentTag> userCommentTagList = userCommentTagDao.findByServerId(param);
        return new PageInfo<>(userCommentTagList);
    }


    @Override
    public ICommonDao<UserCommentTag, Integer> getDao() {
        return userCommentTagDao;
    }

}

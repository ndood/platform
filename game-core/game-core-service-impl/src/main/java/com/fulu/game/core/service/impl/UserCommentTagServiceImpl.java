package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.UserCommentTagDao;
import com.fulu.game.core.entity.UserCommentTag;
import com.fulu.game.core.service.UserCommentTagService;

import javax.xml.stream.events.Comment;
import java.util.List;


@Service
public class UserCommentTagServiceImpl extends AbsCommonService<UserCommentTag,Integer> implements UserCommentTagService {

    @Autowired
	private UserCommentTagDao userCommentTagDao;


    public void create(Comment comment, List<Integer> tagIds){

    }



    @Override
    public ICommonDao<UserCommentTag, Integer> getDao() {
        return userCommentTagDao;
    }
	
}

package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.TechTagDao;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.TechTag;
import com.fulu.game.core.entity.vo.TechTagVO;
import com.fulu.game.core.service.TagService;
import com.fulu.game.core.service.TechTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



@Service
public class TechTagServiceImpl extends AbsCommonService<TechTag,Integer> implements TechTagService {

    @Autowired
	private TechTagDao techTagDao;
    @Autowired
    private TagService tagService;

    @Override
    public ICommonDao<TechTag, Integer> getDao() {
        return techTagDao;
    }


    @Override
    public int deleteByTechAuthId(Integer techAuthId) {
        return techTagDao.deleteByTechAuthId(techAuthId);
    }

    @Override
    public List<TechTag> findByTechAuthId(Integer techAuthId) {
        if(techAuthId==null){
            return new ArrayList<>();
        }
        TechTagVO techTagVO = new TechTagVO();
        techTagVO.setTechAuthId(techAuthId);
        List<TechTag>  techTagList =   techTagDao.findByParameter(techTagVO);
        return techTagList;
    }

    @Override
    public int updateTechTagByTag(Tag tag) {
        return techTagDao.updateTechTagByTag(tag);
    }


    /**
     * 创建用户技能标签
     *
     * @param techAuthId
     * @param tags
     */
    public void saveTechTag(Integer techAuthId, Integer[] tags) {
        if (tags == null) {
            return;
        }
        List<TechTag> techTagList = findByTechAuthId(techAuthId);
        List<Integer> tagList = new ArrayList<>(Arrays.asList(tags));
        for(TechTag techTag : techTagList){
            if(!tagList.contains(techTag.getId())){
                deleteById(techTag.getId());
            }else {
                tagList.remove(techTag.getTagId());
            }
        }
        for (Integer tagId : tagList) {
            Tag tag = tagService.findById(tagId);
            TechTag techTag = new TechTag();
            techTag.setTagId(tagId);
            techTag.setName(tag.getName());
            techTag.setTechAuthId(techAuthId);
            techTag.setCreateTime(new Date());
            techTag.setUpdateTime(new Date());
            create(techTag);
        }
    }
}

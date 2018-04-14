package com.fulu.game.core.service.impl;


import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.service.ICommonService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangbin on 2016/6/28.
 */
@Transactional
public abstract class AbsCommonService<T,K> implements ICommonService<T,K> {

    public abstract ICommonDao<T,K> getDao();

    public List<T> findAll(){
       return getDao().findAll();
    }
    public PageInfo<T> find(int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = getDao().findAll();
        PageInfo page = new PageInfo(list);
        return page;
    }
    public T findById(K id){
        return getDao().findById(id);
    }
    @Transactional
    public int deleteById(K id){
        return getDao().deleteById(id);
    }
    @Transactional
    public int create(T t){
        return getDao().create(t);
    }
    @Transactional
    public int update(T t){
        return getDao().update(t);
    }
}

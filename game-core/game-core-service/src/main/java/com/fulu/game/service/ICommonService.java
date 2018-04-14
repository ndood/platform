package com.fulu.game.service;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Created by wangbin on 2016/6/28.
 */
public interface ICommonService<T,K> {
    public List<T> findAll();
    public PageInfo<T> find(int pageNum, int pageSize);
    public T findById(K id);
    public int deleteById(K id);
    public int create(T t);
    public int update(T t);
}

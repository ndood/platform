package com.fulu.game.core.dao;

import java.util.List;

/**
 * Created by bwang.abcft on 2018/3/5.
 */
public interface ICommonDao<T,K> {

    public List<T> findAll();
    public T findById(K id);
    public int deleteById(K id);
    public int create(T t);
    public int update(T t);

}

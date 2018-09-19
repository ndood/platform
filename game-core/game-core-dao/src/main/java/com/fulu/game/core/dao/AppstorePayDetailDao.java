package com.fulu.game.core.dao;

import com.fulu.game.core.entity.AppstorePayDetail;
import com.fulu.game.core.entity.vo.AppstorePayDetailVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-09-19 20:45:00
 */
@Mapper
public interface AppstorePayDetailDao extends ICommonDao<AppstorePayDetail,Integer>{

    List<AppstorePayDetail> findByParameter(AppstorePayDetailVO appstorePayDetailVO);

}

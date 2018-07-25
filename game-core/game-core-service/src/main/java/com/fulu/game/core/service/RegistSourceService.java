package com.fulu.game.core.service;

import com.fulu.game.core.entity.RegistSource;
import com.fulu.game.core.entity.vo.RegistSourceVO;
import com.github.pagehelper.PageInfo;
import me.chanjar.weixin.common.exception.WxErrorException;

import java.util.List;

/**
 * @author yanbiao
 * @date 2018-06-13 15:33:21
 */
public interface RegistSourceService extends ICommonService<RegistSource, Integer> {

    /**
     * 新增注册来源
     *
     * @param name
     * @param remark
     * @return
     * @throws WxErrorException
     */
    RegistSource save(String name, String remark) throws WxErrorException;

    /**
     * 修改注册来源
     *
     * @param id
     * @param name
     * @param remark
     * @return
     */
    RegistSource update(Integer id, String name, String remark);

    /**
     * 参数查询
     *
     * @param rsVO
     * @return
     */
    List<RegistSource> findByParam(RegistSourceVO rsVO);

    /**
     * 查询Cj（China Joy）活动的注册来源
     *
     * @return 注册来源Bean
     */
    RegistSource findCjRegistSource();

    /**
     * 通过主键id查询注册来源
     *
     * @param id 主键id
     * @return 注册来源Bean
     */
    RegistSource findById(Integer id);

    /**
     * 查询列表带统计结果
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<RegistSourceVO> listWithCount(Integer pageNum, Integer pageSize);
}

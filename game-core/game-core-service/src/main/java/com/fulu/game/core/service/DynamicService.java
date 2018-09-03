package com.fulu.game.core.service;

import com.fulu.game.core.entity.Dynamic;
import com.fulu.game.core.entity.vo.DynamicVO;
import com.fulu.game.core.search.domain.DynamicDoc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * 动态表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-08-30 10:31:41
 */
public interface DynamicService extends ICommonService<Dynamic,Long>{

    /**
     * 保存动态接口
     * @param dynamicVO
     * @return
     */
    public Dynamic save(DynamicVO dynamicVO);

    /**
     *获取动态列表接口
     * @param pageSize 每页数量
     * @param slide 0：下滑刷新；1：上划加载更多
     * @param id 上划：传客户端最大id；下滑：传客户端最小id
     * @param type 动态页tab类型（1：精选；2：关注）
     * @return
     */
    public Page<DynamicDoc> list(Integer pageSize, Integer slide, Integer id, Integer type);

    /**
     * 获取用户动态列表接口
     * @param pageSize 每页数量
     * @param slide 0：下滑刷新；1：上划加载更多
     * @param id 上划：传客户端最大id；下滑：传客户端最小id
     * @param userId 非必传，不传查用户自己动态，传了查其他用户动态
     * @return
     */
    Page<DynamicDoc> userDynamicList(Integer pageSize, Integer slide, Integer id, Integer userId);

    /**
     * 获取动态详情
     * @param id
     * @return
     */
    public DynamicDoc getDynamicDocById(Long id);

    /**
     * 获取用户最新动态，我的里面使用
     * @param userId
     * @return
     */
    public List<DynamicVO> getNewestDynamicList(Integer userId);
}

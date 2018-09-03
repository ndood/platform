package com.fulu.game.core.service.impl;


import com.fulu.game.common.domain.ClientInfo;
import com.fulu.game.common.utils.SubjectUtil;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.AccessLogDetail;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.vo.AccessLogVO;
import com.fulu.game.core.service.AccessLogDetailService;
import com.fulu.game.core.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.AccessLogDao;
import com.fulu.game.core.entity.AccessLog;
import com.fulu.game.core.service.AccessLogService;

import java.util.Date;
import java.util.List;


@Service("accessLogService")
public class AccessLogServiceImpl extends AbsCommonService<AccessLog,Long> implements AccessLogService {

    @Autowired
	private AccessLogDao accessLogDao;

    @Autowired
    private UserService userService;

    @Autowired
    private AccessLogDetailService accessLogDetailService;



    @Override
    public ICommonDao<AccessLog, Long> getDao() {
        return accessLogDao;
    }

    /**
     * 获取来访记录
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<AccessLogVO> accessList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        AccessLogVO accessLogVO = new AccessLogVO();
        accessLogVO.setToUserId(user.getId().longValue());
        List<AccessLogVO> list = accessLogDao.accessList(accessLogVO);
        return new PageInfo<>(list);
    }

    /**
     * 获取足迹记录
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<AccessLogVO> footprintList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        User user = userService.getCurrentUser();
        AccessLogVO accessLogVO = new AccessLogVO();
        accessLogVO.setFromUserId(user.getId().longValue());
        List<AccessLogVO> list = accessLogDao.footprintList(accessLogVO);
        return new PageInfo<>(list);
    }

    /**
     * 保存访问记录信息
     *
     * @param accessLog
     * @return
     */
    @Override
    public AccessLog save(AccessLog accessLog) {
        AccessLogVO accessLogVO = new AccessLogVO();
        accessLogVO.setFromUserId(accessLog.getFromUserId());
        accessLogVO.setToUserId(accessLog.getToUserId());
        List<AccessLogVO> list = accessLogDao.findByParameter(accessLogVO);
        setAccessLogInfo(accessLog);
        if(list != null && !list.isEmpty() && list.size() > 0){
            // TODO shijiaoyun 只有进入首页算次数 && "首页".equals(accessLog.getMenusName())
            int count = list.get(0) != null && list.get(0).getCount() != null && list.get(0).getCount()  > 0 ? list.get(0).getCount() + 1 : 1;
            if( "首页".equals(accessLog.getMenusName()) ) {
                accessLog.setCount(count);
            } else {
                accessLog.setCount(null);
            }
            accessLog.setId(list.get(0).getId());
            update(accessLog);
        } else {
            accessLog.setCount(1);
            accessLog.setCreateTime(new Date());
            create(accessLog);
        }
        // 保存访问日志详情接口
        AccessLogDetail accessLogDetail = new AccessLogDetail();
        accessLogDetail.setAccessLogId(accessLog.getId());
        accessLogDetail.setMenusName(accessLog.getMenusName());
        accessLogDetail.setCityCode(accessLog.getCityCode());
        accessLogDetail.setCityName(accessLog.getCityName());
        accessLogDetail.setCreateTime(new Date());
        accessLogDetail.setUpdateTime(new Date());
        accessLogDetail.setStatus(1);
        accessLogDetailService.create(accessLogDetail);
        return accessLog;
    }

    private void setAccessLogInfo(AccessLog accessLog){
        ClientInfo clientInfo = SubjectUtil.getUserClientInfo();
        if(clientInfo != null){
            accessLog.setCityCode(clientInfo.get_ipCity());
            accessLog.setCityName(clientInfo.get_cityName());
        }
        accessLog.setStatus(1);
        accessLog.setUpdateTime(new Date());
    }
}

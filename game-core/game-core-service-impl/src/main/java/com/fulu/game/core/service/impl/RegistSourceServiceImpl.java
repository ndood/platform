package com.fulu.game.core.service.impl;

import com.fulu.game.common.enums.PagePathEnum;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.dao.RegistSourceDao;
import com.fulu.game.core.entity.Admin;
import com.fulu.game.core.entity.RegistSource;
import com.fulu.game.core.entity.vo.RegistSourceVO;
import com.fulu.game.core.service.AdminService;
import com.fulu.game.core.service.RegistSourceService;
import com.fulu.game.core.service.WxCodeService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RegistSourceServiceImpl extends AbsCommonService<RegistSource, Integer> implements RegistSourceService {

    @Autowired
    private RegistSourceDao registSourceDao;
    @Autowired
    private AdminService adminService;
    @Autowired
    private WxCodeService wxCodeService;

    @Override
    public ICommonDao<RegistSource, Integer> getDao() {
        return registSourceDao;
    }

    @Override
    public RegistSource save(String name, String remark) throws WxErrorException {
        Admin admin = adminService.getCurrentUser();
        int adminId = admin.getId();
        log.info("调用新增注册来源接口,操作人id={},入参name={},remark={}", adminId, name, remark);
        RegistSource rs = new RegistSource();
        rs.setAdminId(adminId);
        rs.setName(name);
        rs.setRemark(remark);
        rs.setCreateTime(new Date());
        rs.setUpdateTime(new Date());
        registSourceDao.create(rs);

        String scene = "sourceId=" + rs.getId();
        log.info("调用生成小程序码接口,参数scene={}", scene);
        String url = wxCodeService.create(scene, PagePathEnum.PUSH_PAGE.getPagePath());
        rs.setWxcodeUrl(url);
        registSourceDao.update(rs);
        return rs;
    }

    @Override
    public List<RegistSource> findByParam(RegistSourceVO rsVO) {
        return registSourceDao.findByParameter(rsVO);
    }

}

package com.fulu.game.core.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.fulu.game.common.enums.SysRouterTypeEnum;
import com.fulu.game.common.exception.UserException;
import com.fulu.game.core.dao.ICommonDao;
import com.fulu.game.core.entity.*;
import com.fulu.game.core.entity.vo.SysRoleVO;
import com.fulu.game.core.service.*;
import com.fulu.game.core.service.util.SysRouterUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.fulu.game.core.dao.SysRoleDao;

import java.util.*;


@Service
public class SysRoleServiceImpl extends AbsCommonService<SysRole,Integer> implements SysRoleService {

    @Autowired
	private SysRoleDao sysRoleDao;

    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleRouterService roleRouterService;
    @Autowired
    private AdminRoleService adminRoleService;
    @Autowired
    private SysRouterService sysRouterService;



    @Override
    public ICommonDao<SysRole, Integer> getDao() {
        return sysRoleDao;
    }

    /**
     * 通过用户id获取角色列表
     *
     * @param id
     * @return
     */
    @Override
    public List<SysRole> findByAdminId(Integer id) {
        return sysRoleDao.findByAdminId(id);
    }

    /**
     * 分页获取角色列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<SysRole> list(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "id DESC");
        SysRoleVO sysRoleVO = new SysRoleVO();
        sysRoleVO.setIsDel(0);
        List<SysRole> list = sysRoleDao.findByParameter(sysRoleVO);
        return new PageInfo(list);
    }

    /**
     * 查询角色详情，包含Router信息，以及Router是否选中信息
     *
     * @param id
     * @return
     */
    @Override
    public SysRoleVO findDetailById(Integer id) {
        SysRole sysRole = findById(id);
        SysRoleVO sysRoleVO = new SysRoleVO();
        BeanUtil.copyProperties(sysRole,sysRoleVO);
        List<SysRouter> list = sysRouterService.findByType(SysRouterTypeEnum.MENUS.getType());
        Map<String, RoleRouter> adminHadRouterIdMap = new HashMap<>();
//        Admin admin = adminService.getCurrentUser();
//        List<AdminRole> adminRoleList = adminRoleService.findByAdminId(admin.getId());
//        if(CollectionUtils.isNotEmpty(adminRoleList)){
//            for(AdminRole adminRole: adminRoleList){
//
//            }
//        }
        List<RoleRouter>  roleRouterList = roleRouterService.findByRoleId(id);
        if(CollectionUtils.isNotEmpty(roleRouterList)){
            for(RoleRouter roleRouter: roleRouterList){
                adminHadRouterIdMap.put(roleRouter.getRouterId().toString(), roleRouter);
            }
        }
        // 设置router选中状态
        if(CollectionUtils.isNotEmpty(list) && adminHadRouterIdMap != null && !adminHadRouterIdMap.isEmpty()){
            for(SysRouter sysRouter: list){
                if(adminHadRouterIdMap.containsKey(sysRouter.getId().toString())){
                    sysRouter.setSelected(true);
                } else {
                    sysRouter.setSelected(false);
                }
            }
        }
        sysRoleVO.setRouterList(SysRouterUtil.formatRouter(list));
        return sysRoleVO;
    }

    /**
     * 保存角色信息
     *
     * @param sysRoleVO
     */
    @Override
    public void save(SysRoleVO sysRoleVO) {
        SysRole sysRole = new SysRole();
        BeanUtil.copyProperties(sysRoleVO, sysRole);
        boolean flag = false;
        SysRoleVO params = new SysRoleVO();
        params.setName(sysRoleVO.getName());
        params.setId(sysRoleVO.getId());
        List<SysRole> sysRoleList = sysRoleDao.findByParameter(params);
        if(!CollectionUtil.isEmpty(sysRoleList) && sysRoleVO.getName() != null ){
            throw new UserException(UserException.ExceptionCode.ROLE_NAME_EXIST);
        }
        Admin admin = adminService.getCurrentUser();
        sysRole.setOperatorId(admin.getId());
        sysRole.setOperatorName(admin.getName());
        sysRole.setStatus(sysRoleVO.getStatus());
        sysRole.setUpdateTime(new Date());
        if(sysRoleVO.getId() != null && sysRoleVO.getId().intValue() > 0){
            sysRoleDao.update(sysRole);
            flag = true;
        } else {
            sysRole.setCreateTime(new Date());
            sysRole.setStatus(1);
            sysRole.setIsDel(0);
            sysRoleDao.create(sysRole);
        }
        // 修改角色router信息
        if(sysRoleVO.getRouterIds() != null){
            //删除当前角色之前所有的角色路由中间表信息
            if(flag){
                roleRouterService.deleteByRoleId(sysRoleVO.getId());
            }
            Integer[] routerIds = sysRoleVO.getRouterIds();
            RoleRouter roleRouter = new RoleRouter();
            roleRouter.setRoleId(sysRoleVO.getId());
            for(int i = 0; i < routerIds.length; i++){
                roleRouter.setRouterId(routerIds[i]);
                roleRouterService.create(roleRouter);
            }
        }
    }
}

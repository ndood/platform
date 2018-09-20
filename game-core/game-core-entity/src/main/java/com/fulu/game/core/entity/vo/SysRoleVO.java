package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.SysRole;
import com.fulu.game.core.entity.SysRouter;
import lombok.Data;

import java.util.List;


/**
 * 角色表
 *
 * @author shijiaoyun
 * @date 2018-09-19 16:25:01
 */
@Data
public class SysRoleVO  extends SysRole {

    /** 角色选择的routerId集合 */
    private Integer[] routerIds;


    /** 所有供选择的router集合 */
    List<SysRouter> routerList;
}

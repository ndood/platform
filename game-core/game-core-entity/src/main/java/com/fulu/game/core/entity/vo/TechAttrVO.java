package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.TechAttr;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 技能属性表
 *
 * @author wangbin
 * @date 2018-04-18 16:29:27
 */
@Data
public class TechAttrVO  extends TechAttr {


    List<TechValueVO> techValueVOList = new ArrayList<>();

}

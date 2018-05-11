package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.ImUser;
import lombok.Data;

import java.util.List;

@Data
public class ImUserVo {

    private String cursor;
    private List<ImUser> imUserList;
}

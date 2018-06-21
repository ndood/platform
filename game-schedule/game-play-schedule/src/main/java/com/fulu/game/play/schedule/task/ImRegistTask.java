package com.fulu.game.play.schedule.task;

import com.fulu.game.core.entity.ImUser;
import com.fulu.game.core.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ImRegistTask {

    @Autowired
    private UserService userService;
    @Scheduled(cron = "0 0/10 * * * ? ")
    public void imRegistAndBind() {
        List<ImUser> list = userService.findImNullUser();
        //todo 单个注册IMID
        //todo 绑定到user中

    }
}

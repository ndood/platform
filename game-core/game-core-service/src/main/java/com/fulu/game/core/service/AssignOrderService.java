package com.fulu.game.core.service;

import com.fulu.game.core.entity.Order;
import com.fulu.game.core.entity.User;

public interface AssignOrderService {



    public User getMatchUser(Order order);
}

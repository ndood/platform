package com.fulu.game.common.domain;

import lombok.Data;

@Data
public class Password {

	private String password;
	private String salt;
	private String plainPassword;



}

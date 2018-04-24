package com.fulu.game.common.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Password {
	private String password;
	private String salt;
	private String plainPassword;
}

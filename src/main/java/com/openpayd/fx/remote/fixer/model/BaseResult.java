package com.openpayd.fx.remote.fixer.model;

import lombok.Data;

@Data
public class BaseResult {
	private boolean success;
	private Error error;
}

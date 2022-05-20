package com.openpayd.fx.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseOutput {
	private boolean success = true;
	private String errorCode;
	private String errorDescription;
}

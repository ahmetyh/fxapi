package com.openpayd.fx.common.exception;

import com.openpayd.fx.common.constant.FxError;

public class FxException extends RuntimeException {

	private FxError error;
	private String detailInfo;

	public FxException(FxError error) {
		this.error = error;
	}

	public FxException(FxError error, String detailInfo) {
		this.error = error;
		this.detailInfo=detailInfo;
	}

	public FxError getError() {
		return error;
	}

	public String getDetailInfo() {
		return detailInfo;
	}
}

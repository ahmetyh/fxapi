package com.openpayd.fx.common.constant;

public enum FxError {

	NO_SOURCE_CURRENCY("1000", "No source currency has been specified."),
	INVALID_BASE_CURRENCY("1001", "Invalid source currency!"),
	NO_TARGET_CURRENCY("1002", "No target currency has been specified."),
	INVALID_TARGET_CURRENCY("1003", "Invalid target currency!"),
	INVALID_CURRENCY_CODE("1004", "You have provided one or more invalid Currency Codes."),
	NO_SOURCE_AMOUNT("1005", "No source amount has been specified."),
	INVALID_SOURCE_AMOUNT("1006", "Invalid source amount!"),
	NO_TRANSACTION_ID_AND_OR_TRANSACTION_DATE("1007", "No transaction id and/or transaction date has been specified."),
	INVALID_PAGE_NO("1008", "You have provided an invalid page no."),
	INVALID_DATE_FORMAT("1009", "Date format is invalid! The required format is dd-mm-yyyy."),
	GENERAL_ERROR("9000", "Error occurred, please check your inputs and try again later!"),
	GENERAL_PARAMETER_FORMAT_ERROR("9005", "Invalid format for parameter "),
	GENERAL_SERVICE_PROVIDER_ERROR("9010", "Error occurred, please try again later!");

	private String errorCode;
	private String errorDescription;

	FxError(String code, String desc) {
		this.errorCode = code;
		this.errorDescription = desc;
	}

	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
}

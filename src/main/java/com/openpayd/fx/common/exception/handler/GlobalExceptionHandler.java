package com.openpayd.fx.common.exception.handler;

import com.openpayd.fx.common.constant.FxError;
import com.openpayd.fx.common.exception.FxException;
import com.openpayd.fx.common.model.BaseOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@SuppressWarnings({ "unchecked", "rawtypes" })
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {

		log.error("unhandled exception", ex);

		BaseOutput output = new BaseOutput();
		output.setSuccess(false);
		output.setErrorCode(FxError.GENERAL_ERROR.getErrorCode());
		output.setErrorDescription(FxError.GENERAL_ERROR.getErrorDescription());
		return new ResponseEntity(output, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FxException.class)
	public ResponseEntity<Object> handleFxException(FxException ex, WebRequest request) {

		log.error("fx exception errorcode: {} errordesc: {} detailInfo: {}", ex.getError().getErrorCode(),
				ex.getError().getErrorDescription(), ex.getDetailInfo(), ex);

		BaseOutput output = new BaseOutput();
		output.setSuccess(false);
		output.setErrorCode(ex.getError().getErrorCode());
		output.setErrorDescription(ex.getError().getErrorDescription());
		return new ResponseEntity(output, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ MethodArgumentTypeMismatchException.class })
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
			MethodArgumentTypeMismatchException ex, WebRequest request) {

		String parameterName = ex.getName();
		String errorDesc = FxError.GENERAL_PARAMETER_FORMAT_ERROR.getErrorDescription() + parameterName;
		String errorCode = FxError.GENERAL_PARAMETER_FORMAT_ERROR.getErrorCode();

		if ("transactionDate".equals(ex.getName())) {
			errorDesc = FxError.INVALID_DATE_FORMAT.getErrorDescription();
			errorCode = FxError.INVALID_DATE_FORMAT.getErrorCode();
		}

		log.error("parameter type exception", ex);

		BaseOutput output = new BaseOutput();
		output.setSuccess(false);
		output.setErrorCode(errorCode);
		output.setErrorDescription(errorDesc);
		return new ResponseEntity(output, HttpStatus.BAD_REQUEST);
	}

}
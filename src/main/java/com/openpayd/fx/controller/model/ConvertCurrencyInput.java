package com.openpayd.fx.controller.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConvertCurrencyInput{
	private BigDecimal sourceAmount;
	private String sourceCurrency;
	private String targetCurrency;
}

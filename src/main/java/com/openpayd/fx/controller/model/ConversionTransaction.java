package com.openpayd.fx.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversionTransaction{
	private String transactionId;
	private String sourceCurrency;
	private BigDecimal sourceAmount;
	private String targetCurrency;
	private BigDecimal targetAmount;
	private BigDecimal rate;
	private String transactionDate;
}

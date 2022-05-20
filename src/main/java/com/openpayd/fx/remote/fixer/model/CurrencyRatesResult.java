package com.openpayd.fx.remote.fixer.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CurrencyRatesResult extends BaseResult {
	private long timestamp;
	private String base;
	private String date;
	private Map<String, BigDecimal> rates;
}

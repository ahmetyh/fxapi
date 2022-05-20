package com.openpayd.fx.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateDTO {
	private String baseCurrency;
	private String targetCurrency;
	private BigDecimal rate;
	private long timestamp;
}

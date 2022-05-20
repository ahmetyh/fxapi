package com.openpayd.fx.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FxConversionDTO {
	private String transactionId;
	private String sourceCurrency;
	private BigDecimal sourceAmount;
	private String targetCurrency;
	private BigDecimal targetAmount;
	private BigDecimal rate;
	private Long timestamp;
	private LocalDateTime transactionDate;
}

package com.openpayd.fx.controller.model;

import com.openpayd.fx.common.model.BaseOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateOutput extends BaseOutput {
	private BigDecimal rate;
}

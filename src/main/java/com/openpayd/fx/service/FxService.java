package com.openpayd.fx.service;

import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FxService {

	ExchangeRateDTO getExchangeRate(String sourceCurrency, String targetCurrency);

	FxConversionDTO convertCurrency(BigDecimal sourceAmount, String sourceCurrency, String targetCurrency);

	List<FxConversionDTO> getConversionList(String transactionId, LocalDate transactionDate, int page);

}

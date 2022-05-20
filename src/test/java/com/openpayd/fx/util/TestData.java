package com.openpayd.fx.util;

import com.openpayd.fx.data.entity.FxConversionEntity;
import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;
import com.openpayd.fx.remote.fixer.model.CurrencyRatesResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class TestData {

	public static ExchangeRateDTO getExchangeRateDTO() {

		return ExchangeRateDTO.builder()
				.baseCurrency("EUR")
				.targetCurrency("CAD")
				.rate(new BigDecimal("0.123456"))
				.build();
	}


	public static CurrencyRatesResult getCurrencyRatesResult(String baseCurrency, String targetCurrency, BigDecimal rate) {
		
		CurrencyRatesResult result = new CurrencyRatesResult();
		
		result.setBase(baseCurrency);
		result.setSuccess(true);
		result.setTimestamp(new Date().getTime());
		result.setRates(new HashMap<>());
		result.getRates().put(targetCurrency, rate);
		
		return result;
	}
	
	public static FxConversionEntity getConversionEntity(String sourceCurrency, String targetCurrency) {
		
		FxConversionEntity entity = new FxConversionEntity();

		entity.setSourceCurrency(sourceCurrency);
		entity.setSourceAmount(new BigDecimal(10));
		entity.setTargetCurrency(targetCurrency);
		entity.setTargetAmount(new BigDecimal(12.34567));
		entity.setRate(new BigDecimal(1.234567));
		entity.setTimestamp(new java.util.Date().getTime());
		entity.setTransactionDate(LocalDateTime.now());
		entity.setTransactionId("trx_id_" + entity.getTimestamp());
		
		return entity;
	}

	public static FxConversionEntity createConversionEntity() {
		return getConversionEntity("EUR","USD");
	}

	public static Optional<FxConversionEntity> getConversionEntity(String trxId) {
		
		FxConversionEntity entity =getConversionEntity("EUR","USD");
		entity.setTransactionId(trxId);
		
		return Optional.of(entity);
	}
	
	
	public static Page<FxConversionEntity> getConversionEntityList(int count) {
		List<FxConversionEntity> list = new ArrayList<FxConversionEntity> ();
		
		for (int i=0;i<count;i++) {
			FxConversionEntity entity =getConversionEntity("EUR","USD");
			entity.setTransactionId("trx" + i);
			
			list.add(entity);
		}
		
		Page<FxConversionEntity> p = new PageImpl<FxConversionEntity>(list);
		
		return p;
	}

	public static List<FxConversionDTO> getConversionListByTransactionId(int count) {

		List<FxConversionDTO> conversionList = new ArrayList<>();

		for (int i = 1; i <= count; i++) {

			FxConversionDTO fxc1 = FxConversionDTO.builder()
					.sourceCurrency("EUR")
					.sourceAmount(new BigDecimal(10))
					.targetCurrency("CAD")
					.rate(new BigDecimal(1.23))
					.targetAmount(new BigDecimal(12.3))
					.timestamp(new Date().getTime())
					.transactionId("trx"+i)
					.transactionDate(LocalDateTime.now())
					.build();

			conversionList.add(fxc1);

		}

		return conversionList;
	}
	

}

package com.openpayd.fx.service.impl;

import com.openpayd.fx.common.constant.FxError;
import com.openpayd.fx.common.exception.FxException;
import com.openpayd.fx.data.entity.FxConversionEntity;
import com.openpayd.fx.data.repository.FxConversionRepository;
import com.openpayd.fx.data.util.EntityMapper;
import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;
import com.openpayd.fx.remote.fixer.FixerFxFeignClient;
import com.openpayd.fx.remote.fixer.model.CurrencyRatesResult;
import com.openpayd.fx.remote.fixer.model.Error;
import com.openpayd.fx.service.FxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FxServiceFixerImpl implements FxService {

	private final String API_KEY;

	private final int PAGE_SIZE;

	public FxServiceFixerImpl(FixerFxFeignClient fxClient,
							 FxConversionRepository fxConversionTransactionRepository,
							 @Value("${fixer.io.api.key}") String apikey,
							 @Value("${fx.api.conversion.list.page.size}") int pageSize) {
		
		this.fxClient = fxClient;
		this.fxConversionTransactionRepository = fxConversionTransactionRepository;
		this.API_KEY = apikey;
		this.PAGE_SIZE=pageSize;
	}

	private final FixerFxFeignClient fxClient;

	private final FxConversionRepository fxConversionTransactionRepository;


	/*
	 * Only one base currency (EUR) is supported by fixer.io
	 */

	@Override
	public ExchangeRateDTO getExchangeRate(String sourceCurrency, String targetCurrency) {

		if (!StringUtils.hasLength(sourceCurrency))
			throw new FxException(FxError.NO_SOURCE_CURRENCY);

		if (!StringUtils.hasLength(targetCurrency))
			throw new FxException(FxError.NO_TARGET_CURRENCY);

		CurrencyRatesResult result = fxClient.getCurrencyRates(API_KEY, sourceCurrency, targetCurrency);

		if (!result.isSuccess())
			handleRemoteError(result.getError());

		BigDecimal rate = result.getRates().get(targetCurrency);

		return ExchangeRateDTO.builder()
				.baseCurrency(sourceCurrency)
				.targetCurrency(targetCurrency)
				.timestamp(result.getTimestamp())
				.rate(rate)
				.build();
	}

	/*
	 * conversion logic runs locally since fixer.io free subscription does not support conversion api
	 */

	@Override
	public FxConversionDTO convertCurrency(BigDecimal sourceAmount, String sourceCurrency, String targetCurrency) {

		if (sourceAmount == null)
			throw new FxException(FxError.NO_SOURCE_AMOUNT);

		if (sourceAmount.compareTo(BigDecimal.ZERO) < 1)
			throw new FxException(FxError.INVALID_SOURCE_AMOUNT);

		if (!StringUtils.hasLength(sourceCurrency))
			throw new FxException(FxError.NO_SOURCE_CURRENCY);

		if (!StringUtils.hasLength(targetCurrency))
			throw new FxException(FxError.NO_TARGET_CURRENCY);
		
		ExchangeRateDTO exchangeRate = getExchangeRate(sourceCurrency, targetCurrency);

		//conversion logic
		BigDecimal targetAmount = exchangeRate.getRate().multiply(sourceAmount).setScale(6, RoundingMode.HALF_EVEN);
		
		FxConversionDTO dto = FxConversionDTO.builder()
								.sourceAmount(sourceAmount)
								.sourceCurrency(sourceCurrency)
								.targetCurrency(targetCurrency)
								.targetAmount(targetAmount)
								.rate(exchangeRate.getRate())
								.timestamp(exchangeRate.getTimestamp())
								.transactionDate(LocalDateTime.now())
								.build();
		
		 String transactionId = saveConversion(dto);
		 
		 dto.setTransactionId(transactionId);
		 
		return dto;

	}

	private String saveConversion(FxConversionDTO dto) {
		FxConversionEntity entity = EntityMapper.map(dto);
		entity = fxConversionTransactionRepository.save(entity);
		return entity.getTransactionId();
	}

	@Override
	public List<FxConversionDTO> getConversionList(String transactionId, LocalDate transactionDate, int page) {

		if (transactionId == null && transactionDate == null) 
			throw new FxException(FxError.NO_TRANSACTION_ID_AND_OR_TRANSACTION_DATE);
		
		if (page<0)
			throw new FxException(FxError.INVALID_PAGE_NO);

		if (transactionId != null) 
			return filterByTransactionId(transactionId, transactionDate); // no paging

		return filterByTransactionDate(transactionDate, page);
	}

	private List<FxConversionDTO> filterByTransactionId(String transactionId, LocalDate transactionDate) {

		List<FxConversionDTO> list = new ArrayList<>();

		Optional<FxConversionEntity> result = fxConversionTransactionRepository.findById(transactionId);

		if (result.isPresent()) {
			
			FxConversionDTO dto = EntityMapper.map(result.get());
			
			if (transactionDate == null)
				list.add(dto);
			else if (dto.getTransactionDate().isAfter(transactionDate.atStartOfDay()) && dto.getTransactionDate().isBefore(transactionDate.plusDays(1).atStartOfDay()) )
				list.add(dto);
		}
		
		return list;
	}

	private List<FxConversionDTO> filterByTransactionDate(LocalDate transactionDate, int pageNo) {

		Pageable paging = PageRequest.of(pageNo-1, PAGE_SIZE); //pageNo -= 1 since pageNo zero based

		LocalDateTime startDate = transactionDate.atStartOfDay();
		LocalDateTime endDate = startDate.plusDays(1);
		
		Page<FxConversionEntity> page = fxConversionTransactionRepository.listByTransactionDate(startDate, endDate, paging);

		if (!page.hasContent())
			return Collections.emptyList();

		List<FxConversionDTO> list = new ArrayList<>();

		page.getContent().forEach(e -> list.add(EntityMapper.map(e)));

		return list;

	}

	private void handleRemoteError(Error fixerError) {

		/*
			Map remote service provider's error codes to our error codes
			201	- An invalid base currency has been entered.
			202	- One or more invalid symbols have been specified.
			...
		*/

		if (fixerError.getCode().equals("201"))
			throw new FxException(FxError.INVALID_BASE_CURRENCY, fixerError.toString());
		else if (fixerError.getCode().equals("202"))
			throw new FxException(FxError.INVALID_CURRENCY_CODE,fixerError.toString());
		else
			throw new FxException(FxError.GENERAL_SERVICE_PROVIDER_ERROR, fixerError.toString());
	}

}

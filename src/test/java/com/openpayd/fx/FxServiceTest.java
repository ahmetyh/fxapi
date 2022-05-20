package com.openpayd.fx;

import com.openpayd.fx.common.constant.FxError;
import com.openpayd.fx.common.exception.FxException;
import com.openpayd.fx.data.repository.FxConversionRepository;
import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;
import com.openpayd.fx.remote.fixer.FixerFxFeignClient;
import com.openpayd.fx.remote.fixer.model.CurrencyRatesResult;
import com.openpayd.fx.service.FxService;
import com.openpayd.fx.service.impl.FxServiceFixerImpl;
import com.openpayd.fx.util.TestData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class FxServiceTest {

	private final static int PAGE_SIZE = 5;

	@Mock
	private FixerFxFeignClient fxClient;

	@Mock
	private FxConversionRepository fxConversionTransactionRepository;

	private FxService fxService;

	@BeforeEach
	void init() {
		fxService = new FxServiceFixerImpl(fxClient, fxConversionTransactionRepository, null, PAGE_SIZE);
	}

	@Test
	public void testGetExchangeRate() throws Exception {

		String baseCurrency = "EUR";
		String targetCurrency = "USD";
		BigDecimal expectedRate = new BigDecimal("1.234567");

		CurrencyRatesResult currencyRatesResult = TestData.getCurrencyRatesResult(baseCurrency, targetCurrency, expectedRate);

		Mockito.when(fxClient.getCurrencyRates(any(), any(), any())).thenReturn(currencyRatesResult);

		ExchangeRateDTO exchangeRate = fxService.getExchangeRate(baseCurrency, targetCurrency);

		Assertions.assertEquals(exchangeRate.getTargetCurrency(), targetCurrency);

		Assertions.assertTrue(exchangeRate.getRate().compareTo(expectedRate) == 0);

	}

	@Test
	public void testGetExchangeRateNoSourceCurrencyException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.getExchangeRate(null, "USD");
		});

		Assertions.assertEquals(FxError.NO_SOURCE_CURRENCY, expectedException.getError());

	}

	@Test
	public void testGetExchangeRateNoTargetCurrencyException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.getExchangeRate("EUR", "");
		});

		Assertions.assertEquals(FxError.NO_TARGET_CURRENCY, expectedException.getError());

	}

	@Test
	public void testConvertCurrency() throws Exception {

		String sourceCurrency = "EUR";
		String targetCurrency = "USD";
		BigDecimal sourceAmount = new BigDecimal("10");

		BigDecimal expectedRate = new BigDecimal("1.234567");
		BigDecimal expectedTargetAmount = new BigDecimal("12.345670");

		CurrencyRatesResult currencyRatesResult = TestData.getCurrencyRatesResult(sourceCurrency, targetCurrency, expectedRate);

		Mockito.when(fxClient.getCurrencyRates(any(), any(), any())).thenReturn(currencyRatesResult);

		Mockito.when(fxConversionTransactionRepository.save(any())).thenReturn(TestData.getConversionEntity(sourceCurrency, targetCurrency));

		FxConversionDTO conversion = fxService.convertCurrency(sourceAmount, sourceCurrency, targetCurrency);

		Assertions.assertTrue(conversion.getRate().compareTo(expectedRate) == 0);

		Assertions.assertNotNull(conversion.getTransactionId());

		Assertions.assertTrue(conversion.getTargetAmount().compareTo(expectedTargetAmount) == 0);

	}

	@Test
	public void testConvertCurrencyNoSourceCurrencyException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.convertCurrency(BigDecimal.ONE, "", "USD");
		});

		Assertions.assertEquals(FxError.NO_SOURCE_CURRENCY, expectedException.getError());
	}

	@Test
	public void testConvertCurrencyNoTargetCurrencyException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.convertCurrency(BigDecimal.ONE, "EUR", "");
		});

		Assertions.assertEquals(FxError.NO_TARGET_CURRENCY, expectedException.getError());
	}

	@Test
	public void testConvertCurrencyNoSourceAmountException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.convertCurrency(BigDecimal.ZERO, "EUR", "USD");
		});

		Assertions.assertEquals(FxError.INVALID_SOURCE_AMOUNT, expectedException.getError());
	}


	@Test
	public void testGetConversionListByTransactionIdOnly() throws Exception {

		String transactionId = "trx123";

		Mockito.when(fxConversionTransactionRepository.findById(any())).thenReturn(TestData.getConversionEntity(transactionId));

		List<FxConversionDTO> conversionList = fxService.getConversionList("trx123", null, 1);

		Assertions.assertTrue(conversionList.size() == 1);

		Assertions.assertTrue(conversionList.get(0).getTransactionId().equals(transactionId));

	}

	@Test
	public void testGetConversionListByTransactionIdAndDate() throws Exception {

		String transactionId = "trx123";

		Mockito.when(fxConversionTransactionRepository.findById(any())).thenReturn(TestData.getConversionEntity(transactionId));

		List<FxConversionDTO> conversionList = fxService.getConversionList("trx123", LocalDate.now(), 1);

		Assertions.assertTrue(conversionList.size() == 1);

		Assertions.assertTrue(conversionList.get(0).getTransactionId().equals(transactionId));

	}

	@Test
	public void testGetConversionListByTransactionDateOnly() throws Exception {

		int LIST_SIZE = 5;

		Mockito.when(fxConversionTransactionRepository.listByTransactionDate(any(), any(), any())).thenReturn(TestData.getConversionEntityList(LIST_SIZE));

		List<FxConversionDTO> conversionList = fxService.getConversionList(null, LocalDate.now(), 1);

		Assertions.assertTrue(conversionList.size() == LIST_SIZE);

	}

	@Test
	public void testGetConversionListTransactionParametersException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.getConversionList(null, null, 1);
		});

		Assertions.assertEquals(FxError.NO_TRANSACTION_ID_AND_OR_TRANSACTION_DATE, expectedException.getError());

	}

	@Test
	public void testGetConversionListPageParameterException() throws Exception {

		FxException expectedException = Assertions.assertThrows(FxException.class, () -> {
			fxService.getConversionList(null, LocalDate.now(), -1);
		});

		Assertions.assertEquals(FxError.INVALID_PAGE_NO, expectedException.getError());

	}

}

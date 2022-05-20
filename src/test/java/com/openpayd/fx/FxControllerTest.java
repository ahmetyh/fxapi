package com.openpayd.fx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpayd.fx.controller.model.ConvertCurrencyInput;
import com.openpayd.fx.controller.model.ExchangeRateOutput;
import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;
import com.openpayd.fx.service.FxService;
import com.openpayd.fx.util.TestData;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class FxControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FxService fxService;

	private static ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testLatest() throws Exception {

		ExchangeRateDTO exchangeRate = TestData.getExchangeRateDTO();

		Mockito.when(fxService.getExchangeRate(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(exchangeRate);

		MvcResult result = mockMvc.perform(get("/api/latest")
				.param("baseCurrency", "EUR")
				.param("targetCurrency", "CAD"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		String contentAsString = result.getResponse().getContentAsString();

		ExchangeRateOutput response = mapper.readValue(contentAsString, ExchangeRateOutput.class);

		assertAll(
				() -> assertNotNull(response),
				() -> assertTrue(response.isSuccess()),
				() -> assertTrue(response.getRate().compareTo(exchangeRate.getRate())==0)
		);

	}

	@Test
	public void testConvert() throws Exception {

		ConvertCurrencyInput input = new ConvertCurrencyInput();
		input.setSourceCurrency("EUR");
		input.setSourceAmount(new BigDecimal(10));
		input.setTargetCurrency("CAD");

		String json = mapper.writeValueAsString(input);

		FxConversionDTO conversion = FxConversionDTO.builder()
				.targetAmount(new BigDecimal(20.123456))
				.transactionId("trx12345")
				.build();

		Mockito.when(fxService.convertCurrency(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(conversion);

		mockMvc.perform(post("/api/convert")
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("utf-8")
				.content(json).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andExpect(jsonPath("$.success", Matchers.equalTo(true)))
				.andExpect(jsonPath("$.transactionId", Matchers.equalTo(conversion.getTransactionId())));
	}

	@Test
	public void testConversionListByTransactionId() throws Exception {

		Mockito.when(fxService.getConversionList(ArgumentMatchers.any(), ArgumentMatchers.any(), Mockito.anyInt())).thenReturn(TestData.getConversionListByTransactionId(1));

		mockMvc.perform(get("/api/conversions")
				.param("transactionId", "trx1") 
				.param("transactionDate", "17-12-2021")
				.param("page", "1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", Matchers.equalTo(true)))
				.andExpect(jsonPath("$.transactions", Matchers.hasSize(1)))
				.andExpect(jsonPath("$.transactions[0].transactionId", Matchers.equalTo("trx1")));

	}

	
	@Test
	public void testConversionListByTransactionDate() throws Exception {

		Mockito.when(fxService.getConversionList(ArgumentMatchers.any(), ArgumentMatchers.any(), Mockito.anyInt())).thenReturn(TestData.getConversionListByTransactionId(3));

		mockMvc.perform(get("/api/conversions")
				.param("baseCurrency", "EUR")
				.param("targetCurrency", "CAD"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", Matchers.equalTo(true)))
				.andExpect(jsonPath("$.transactions", Matchers.hasSize(3)))
				.andExpect(jsonPath("$.transactions[2].transactionId", Matchers.equalTo("trx3")));

	}
	


}

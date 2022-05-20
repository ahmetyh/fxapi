package com.openpayd.fx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openpayd.fx.common.constant.FxError;
import com.openpayd.fx.controller.model.ConversionListOutput;
import com.openpayd.fx.controller.model.ConvertCurrencyInput;
import com.openpayd.fx.controller.model.ConvertCurrencyOutput;
import com.openpayd.fx.controller.model.ExchangeRateOutput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FxControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    private static RestTemplate restTemplate = null;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        baseUrl = baseUrl.concat(":").concat(port+ "").concat("/fx/api/");
    }

    @Test
    public void testLatest() {

        String url = baseUrl.concat("latest?sourceCurrency=EUR&targetCurrency=CAD");

        ExchangeRateOutput output = restTemplate.getForObject(url, ExchangeRateOutput.class);

        assertAll(
                () -> assertNotNull(output),
                () -> assertTrue(output.isSuccess())
        );
    }

    @Test
    public void testLatestInvalidBaseCurrencyException() {

        String url = baseUrl.concat("latest?sourceCurrency=XXX&targetCurrency=CAD");

        ExchangeRateOutput output = restTemplate.getForObject(url, ExchangeRateOutput.class);

        assertAll(
                () -> assertNotNull(output),
                () -> assertFalse(output.isSuccess()),
                () -> assertFalse(output.getErrorCode().equals(FxError.INVALID_BASE_CURRENCY))
        );
    }

    @Test
    public void testConvert() throws Exception {

        String url = baseUrl.concat("convert");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ConvertCurrencyInput input = new ConvertCurrencyInput();
        input.setSourceAmount(BigDecimal.ONE);
        input.setSourceCurrency("EUR");
        input.setTargetCurrency("CAD");

        ObjectMapper mapper = new ObjectMapper();

        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(input), headers);

        ConvertCurrencyOutput output = restTemplate.postForObject(url, request, ConvertCurrencyOutput.class);

        assertAll(
                () -> assertNotNull(output),
                () -> assertTrue(output.isSuccess()),
                () -> assertNotNull(output.getTransactionId()),
                ()-> assertNotNull(output.getAmount())
        );
    }


    @Test
    @Sql(statements = "INSERT INTO FX_CONVERSIONS (TRANSACTION_ID, SOURCE_CURRENCY, SOURCE_AMOUNT, " +
            "                                      TARGET_CURRENCY, TARGET_AMOUNT, RATE, " +
            "                                      TIMESTAMP, TRANSACTION_DATE) " +
            "           VALUES ('trx123', 'EUR', 2, 'CAD', 2.2, 1.1, 123456790, CURRENT_TIMESTAMP)",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(statements = "DELETE FROM FX_CONVERSIONS",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)

    public void testConversionListByTransactionId() {

        String url = baseUrl.concat("conversions?page=1&transactionId=trx123");

        ConversionListOutput output = restTemplate.getForObject(url, ConversionListOutput.class);

        assertAll(
                () -> assertNotNull(output),
                () -> assertTrue(output.isSuccess()),
                () -> assertTrue(output.getTransactions().size()==1),
                () -> assertTrue(output.getTransactions().get(0).getTransactionId().equals("trx123"))
        );
    }

}

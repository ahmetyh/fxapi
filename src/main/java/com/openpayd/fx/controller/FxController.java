package com.openpayd.fx.controller;

import com.openpayd.fx.controller.model.ConversionListOutput;
import com.openpayd.fx.controller.model.ConvertCurrencyInput;
import com.openpayd.fx.controller.model.ConvertCurrencyOutput;
import com.openpayd.fx.controller.model.ExchangeRateOutput;
import com.openpayd.fx.controller.util.RestMapper;
import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;
import com.openpayd.fx.service.FxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class FxController {

	private FxService fxService;

	@Operation(summary = "Exchange rate API")
	@GetMapping("/latest")
	public ResponseEntity<ExchangeRateOutput> getExchangeRate(String sourceCurrency, String targetCurrency) {

		ExchangeRateDTO exchangeRate = fxService.getExchangeRate(sourceCurrency, targetCurrency);

		return ResponseEntity.ok(RestMapper.map(exchangeRate));
	}

	@Operation(summary = "Conversion API")
	@PostMapping("/convert")
	public ResponseEntity<ConvertCurrencyOutput> convert(@RequestBody ConvertCurrencyInput input) {

		FxConversionDTO conversion = fxService.convertCurrency(input.getSourceAmount(), input.getSourceCurrency(), input.getTargetCurrency());

		return ResponseEntity.ok(RestMapper.map(conversion));
	}

	@Operation(summary = "Conversion List API")
	@GetMapping("/conversions")
	public ResponseEntity<ConversionListOutput> getConversionList(@RequestParam(required = false) String transactionId, 
																  @Parameter(description = "Date format is dd-mm-yyyy") 
																  @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate transactionDate, 
																  @RequestParam(defaultValue = "1") int page) {

		List<FxConversionDTO> conversionList = fxService.getConversionList(transactionId, transactionDate, page);

		return ResponseEntity.ok(RestMapper.map(conversionList));
	}

}

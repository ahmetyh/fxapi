package com.openpayd.fx.remote.fixer;

import com.openpayd.fx.remote.fixer.model.CurrencyRatesResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fixerFxClient", url = "${fixer.io.url}")
public interface FixerFxFeignClient {

	@GetMapping("/latest")
	CurrencyRatesResult getCurrencyRates(@RequestParam("access_key") String access_key, @RequestParam("base") String base, @RequestParam("symbols") String symbols); 

}



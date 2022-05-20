package com.openpayd.fx.controller.util;

import com.openpayd.fx.controller.model.ConversionListOutput;
import com.openpayd.fx.controller.model.ConversionTransaction;
import com.openpayd.fx.controller.model.ConvertCurrencyOutput;
import com.openpayd.fx.controller.model.ExchangeRateOutput;
import com.openpayd.fx.model.ExchangeRateDTO;
import com.openpayd.fx.model.FxConversionDTO;
import org.springframework.beans.BeanUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RestMapper {

	public static ExchangeRateOutput map(ExchangeRateDTO dto) {
		return new ExchangeRateOutput(dto.getRate());
	}

	public static ConvertCurrencyOutput map(FxConversionDTO conversion) {
		return new ConvertCurrencyOutput(conversion.getTransactionId(), conversion.getTargetAmount());
	}

	public static ConversionListOutput map(List<FxConversionDTO> conversionList) {
		List<ConversionTransaction> list = conversionList.stream().map(RestMapper::mapTrx).collect(Collectors.toList());
		return new ConversionListOutput(list);
	}

	private static ConversionTransaction mapTrx(FxConversionDTO dto) {
		ConversionTransaction trx = new ConversionTransaction();
		BeanUtils.copyProperties(dto, trx);
		trx.setTransactionDate(dto.getTransactionDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
		return trx;
	}

}

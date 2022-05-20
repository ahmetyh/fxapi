package com.openpayd.fx.controller.model;

import com.openpayd.fx.common.model.BaseOutput;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversionListOutput extends BaseOutput {
	private List<ConversionTransaction> transactions;
}

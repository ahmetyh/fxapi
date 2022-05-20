package com.openpayd.fx.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "FX_CONVERSIONS")
public class FxConversionEntity {

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "uuid2")
	@Column(name = "TRANSACTION_ID")
	private String transactionId;

	@Column(name = "SOURCE_CURRENCY")
	private String sourceCurrency;

	@Column(name = "SOURCE_AMOUNT", precision = 20, scale = 6)
	private BigDecimal sourceAmount;

	@Column(name = "TARGET_CURRENCY")
	private String targetCurrency;

	@Column(name = "TARGET_AMOUNT", precision = 20, scale = 6)
	private BigDecimal targetAmount;
	
	@Column(name = "RATE", precision = 20, scale = 6)
	private BigDecimal rate;
	
	@Column(name = "TIMESTAMP")
	private Long timestamp;
	
	@Column(name = "TRANSACTION_DATE")
	private LocalDateTime transactionDate;
	
}

package com.openpayd.fx;

import com.openpayd.fx.data.entity.FxConversionEntity;
import com.openpayd.fx.data.repository.FxConversionRepository;
import com.openpayd.fx.util.TestData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FxConversionRepositoryTest {

	@Autowired
	private FxConversionRepository fxConversionRepository;

	@Test
	public void shouldGetByTransactionId() {

		FxConversionEntity entity = TestData.createConversionEntity();

		fxConversionRepository.save(entity);

		assertThat(entity.getTransactionId()).isNotEmpty();

		FxConversionEntity savedEntity = fxConversionRepository.getById(entity.getTransactionId());

		assertThat(savedEntity).isNotNull();

	}

	@Test
	public void shouldlistByTransactionDate() {

		int COUNT = 9;

		for (int i = 0; i < COUNT; i++) {
			FxConversionEntity entity = TestData.createConversionEntity();
			fxConversionRepository.save(entity);
		}

		LocalDateTime startDate = LocalDate.now().atStartOfDay();
		LocalDateTime endDate = startDate.plusDays(1);

		Page<FxConversionEntity> page1 = fxConversionRepository.listByTransactionDate(startDate, endDate, PageRequest.of(0, 5)); // page_size=5
		Page<FxConversionEntity> page2 = fxConversionRepository.listByTransactionDate(startDate, endDate, PageRequest.of(1, 5));

		assertThat(page1).isNotNull();
		assertThat(page1.getContent()).hasSize(5); // full page

		assertThat(page2).isNotNull();
		assertThat(page2.getContent()).hasSize(4); // remaining

	}

}

package com.openpayd.fx.data.repository;

import com.openpayd.fx.data.entity.FxConversionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;


public interface FxConversionRepository extends JpaRepository<FxConversionEntity, String> {
	
	@Query(value = "from FxConversionEntity t where t.transactionDate BETWEEN :startDate AND :endDate order by t.transactionDate")
	Page<FxConversionEntity> listByTransactionDate(@Param("startDate") LocalDateTime startDate, @Param("endDate")  LocalDateTime endDate, Pageable pageable);
	
}

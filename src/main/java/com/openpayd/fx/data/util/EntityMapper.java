package com.openpayd.fx.data.util;

import com.openpayd.fx.data.entity.FxConversionEntity;
import com.openpayd.fx.model.FxConversionDTO;
import org.springframework.beans.BeanUtils;

public class EntityMapper {

	public static FxConversionDTO map(FxConversionEntity entity) {

		FxConversionDTO target = new FxConversionDTO();

		BeanUtils.copyProperties(entity, target);

		return target;
	}

	public static FxConversionEntity map(FxConversionDTO dto) {

		FxConversionEntity entity = new FxConversionEntity();

		BeanUtils.copyProperties(dto, entity);

		return entity;
	}

}

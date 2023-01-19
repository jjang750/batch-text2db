package com.aegisep.batch.file2db;

import com.aegisep.batch.dto.ResidentVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ResidentItemProcessor implements ItemProcessor<ResidentVo, ResidentVo> {

	@Override
	public ResidentVo process(final ResidentVo residentVo) throws Exception {

		final ResidentVo transformedResidentVo = new ResidentVo();

		transformedResidentVo.setAptcd(residentVo.getAptcd());
		transformedResidentVo.setOrgaptcd(residentVo.getOrgaptcd());
		transformedResidentVo.setDongho(residentVo.getDongho());
		transformedResidentVo.setOccu_date(residentVo.getOccu_date());
		transformedResidentVo.setRel(residentVo.getRel());
		transformedResidentVo.setName(residentVo.getName());
		transformedResidentVo.setMobile_tel_no1(residentVo.getMobile_tel_no1());

		log.info("Converting (" + residentVo + ") into (" + transformedResidentVo + ")");

		return transformedResidentVo;
	}

}

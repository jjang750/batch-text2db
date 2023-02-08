package com.aegisep.batch.file2db;

import com.aegisep.batch.dto.TextToDBVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ResidentItemProcessor implements ItemProcessor<TextToDBVo, TextToDBVo> {

	@Override
	public TextToDBVo process(final TextToDBVo textToDBVo) throws Exception {

		TextToDBVo transformedTextToDBVo = textToDBVo.clone();
		log.info("Converting (" + textToDBVo + ") into (" + transformedTextToDBVo + ")");

		return transformedTextToDBVo;
	}

}

package com.aegisep.batch.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class TextToDBVo implements Cloneable{
	private String aptcd,dongho,billym,modifydt;
	private long monthfee,bfoverfee,bfoverduefee,bfduedtfee,afoverduefee,afduedtfee;

	@Override
	public TextToDBVo clone() {
		try {
			// TODO: copy mutable state here, so the clone can't change the internals of the original
			return (TextToDBVo) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}

package com.zg.sinosig.generate;

import com.zg.webdemo.entity.SinoSigSQLLogEntity;

import java.util.List;

public interface Generate {
    List<SinoSigSQLLogEntity> execute(List<SinoSigSQLLogEntity> list);
}

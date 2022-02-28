package com.zg.webdemo.entity;

import com.sun.org.apache.bcel.internal.generic.LDC;
import com.zg.bean.annotation.FieldTypeMode;
import com.zg.bean.annotation.Model;
import com.zg.bean.entity.MainModel;

@Model(tableName = "ldcode")
@FieldTypeMode(typeMode = "entity")
public class LDCode extends MainModel {

    public String  codetype;
    public String  codecode;
    public String  codecname;
    public String flag;
    public String newflag;

    public LDCode(){}
    public LDCode(String codetype, String codecode, String codecname, String flag) {
        this.codetype = codetype;
        this.codecode = codecode;
        this.codecname = codecname;
        this.flag = flag;

    }

    public LDCode(String codetype, String codecode, String codecname, String flag, String newflag) {
        this.codetype = codetype;
        this.codecode = codecode;
        this.codecname = codecname;
        this.flag = flag;
        this.newflag = newflag;
    }
}

package com.zg.mvc.controller;

import com.zg.mvc.entity.MessageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zkyd01 on 2018/9/1.
 */
public abstract class BaseController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());
    public MessageBean json;


}

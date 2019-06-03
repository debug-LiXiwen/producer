package com.softlab.logger.service;

import com.softlab.logger.common.ProducerException;
import com.softlab.logger.core.model.vo.LogVo;
import java.util.Map;

/**
 * Created by LiXiwen on 2019/6/2 19:41.
 **/
public interface ProducerService {

    Map<String,Object> sendLog(LogVo logVo) throws ProducerException;
}

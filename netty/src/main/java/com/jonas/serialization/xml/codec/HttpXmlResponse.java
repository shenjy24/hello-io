package com.jonas.serialization.xml.codec;

import io.netty.handler.codec.http.FullHttpResponse;
import lombok.Data;

/**
 * HttpXmlResponse
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-03
 */
@Data
public class HttpXmlResponse {
    private FullHttpResponse response;
    private Object result;

    public HttpXmlResponse(FullHttpResponse response, Object result) {
        this.response = response;
        this.result = result;
    }
}

package com.jonas.serialization.xml.codec;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HttpXmlRequest
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpXmlRequest {
    private FullHttpRequest request;
    private Object body;
}

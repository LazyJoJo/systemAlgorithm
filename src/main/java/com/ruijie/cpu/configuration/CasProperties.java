/**
 * projectName: gs-securing-web
 * fileName: CasProperties.java
 * packageName: auth
 * date: 2019-09-06 10:53
 * copyright(c) 2017-2020 锐捷网络股份有限公司
 */
package com.ruijie.cpu.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version: V1.0
 * @author: xieqingcheng
 * @className: CasProperties
 * @packageName: auth
 * @description:
 * @data: 2019-09-06 10:53
 **/
@Component
@Data
@ConfigurationProperties(prefix = "cas",ignoreUnknownFields = false)
public class CasProperties {
    private String casServerUrl;
    private String casServerLoginUrl;
    private String casServerLogoutUrl;
    private String appServerUrl;
    private String appLoginUrl;
    private String appLogoutUrl;
}

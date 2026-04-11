package com.github.iappapp.panda.business.resource;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.Serializable;

@ToString
@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfo implements Serializable {
   private static final long serialVersionUID = -6260895915479458485L;
   private String url;
   private RequestMethod method;
   private Long apiId;
   private String apiName;
   private Integer callLimit;

   public RequestInfo(String url, RequestMethod method) {
      this.url = StringUtils.trim(url);
      this.method = method;
   }
}

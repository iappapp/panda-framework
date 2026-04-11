package com.github.iappapp.panda.business.resource;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class InterfaceBlackList {
   @NotBlank(message = "contextPath can not be empty")
   private String contextPath;

   @NotNull(message = "interfaces can not be null")
   private List<RequestInfo> interfaces;
}

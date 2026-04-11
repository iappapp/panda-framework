/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.common.http.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponseDTO<T> {

    private Integer statusCode;

    private T result;

    private Boolean success;
}


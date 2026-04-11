/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.crypto.digest.DigestUtil
 *  com.alibaba.excel.context.AnalysisContext
 *  com.alibaba.excel.event.AnalysisEventListener
 *  com.alibaba.fastjson.JSON
 *  com.dahua.panda.common.util.ReflectionUtils
 *  javax.validation.Validation
 *  javax.validation.Validator
 *  javax.validation.ValidatorFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.common.office.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.github.iappapp.panda.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PandaExcelListener<T extends ExcelModel> extends AnalysisEventListener<T> {
    private static final Logger log = LoggerFactory.getLogger(PandaExcelListener.class);
    private static final Validator validator;
    private AtomicInteger totalNum = new AtomicInteger(0);
    private AtomicInteger failNum = new AtomicInteger(0);
    private AtomicInteger successNum = new AtomicInteger(0);
    private Set<String> set = new HashSet<String>();
    private List<T> errorList = new LinkedList<T>();

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        this.totalNum.incrementAndGet();
        String rowMsg = JSON.toJSONString(t);
        log.debug("read a row: {}", rowMsg);
        if (!this.validate(t)) {
            return;
        }
        if (this.isDuplicate(t)) {
            log.error("read a duplicate row at {} : {}", this.getCurrentRowIndex(analysisContext), rowMsg);
            this.handleErrorData(t, this.getDuplicateErrorMsg());
            return;
        }
        String result = null;
        try {
            result = this.handle(t, analysisContext);
        } catch (Exception e) {
            log.error("failed to execute handle()", e);
            this.handleErrorData(t, "数据处理异常");
            return;
        }
        if (StrUtil.isBlank(result)) {
            this.successNum.incrementAndGet();
        } else {
            this.handleErrorData(t, result);
        }
    }

    private void handleErrorData(T t, String errorMessage) {
        this.failNum.incrementAndGet();
        t.setErrorMessage(errorMessage);
        this.errorList.add(t);
    }

    private String getDuplicateErrorMsg() {
        if (this.isExactMatchExists()) {
            return "数据重复";
        }
        List<Field> list = this.getNonRepeatableField();
        String msg = list.stream()
                .map(field -> field.getName())
                .collect(Collectors.joining(","));
        return msg + " 不能重复";
    }

    private boolean validate(T t) {
        Set<ConstraintViolation<T>> validates = validator.validate(t, t.getClass());
        if (CollUtil.isNotEmpty(validates)) {
            String msg = validates.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
            this.handleErrorData(t, msg);
            return false;
        }
        return true;
    }

    private boolean isDuplicate(T t) {
        if (this.isExactMatchExists()) {
            String md5 = PandaExcelListener.md5(t);
            if (this.set.contains(md5)) {
                return true;
            }
            this.set.add(md5);
            return false;
        }
        List<Field> list = this.getNonRepeatableField();
        if (CollUtil.isEmpty(list)) {
            return false;
        }
        Object[] objects = list.stream().map(field -> ReflectionUtils.getFieldValue(t, field)).toArray();
        String md5 = PandaExcelListener.md5(objects);
        if (this.set.contains(md5)) {
            return true;
        }
        this.set.add(md5);
        return false;
    }

    private static String md5(Object ... objects) {
        String result = "";

        for (Object obj : objects) {
            result = DigestUtil.md5Hex((result + obj));
        }
        return result;
    }

    protected boolean isExactMatchExists() {
        return true;
    }

    protected List<Field> getNonRepeatableField() {
        return CollUtil.newArrayList();
    }

    protected Integer getCurrentRowIndex(AnalysisContext analysisContext) {
        return analysisContext.readRowHolder().getRowIndex();
    }

    public abstract String handle(T t, AnalysisContext analysisContext);

    @Override
    public abstract void doAfterAllAnalysed(AnalysisContext analysisContext);

    public Integer getTotalNum() {
        return this.totalNum.get();
    }

    public Integer getFailNum() {
        return this.failNum.get();
    }

    public Integer getSuccessNum() {
        return this.successNum.get();
    }

    public List<T> getErrorList() {
        return this.errorList;
    }
}


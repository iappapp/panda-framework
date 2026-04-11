/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.codec.Base64
 *  cn.hutool.core.collection.CollUtil
 *  cn.hutool.core.io.FileUtil
 *  cn.hutool.core.io.IoUtil
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.core.util.URLUtil
 *  com.alibaba.excel.EasyExcel
 *  com.alibaba.excel.ExcelWriter
 *  com.alibaba.excel.read.builder.ExcelReaderSheetBuilder
 *  com.alibaba.excel.read.listener.ReadListener
 *  com.alibaba.excel.read.metadata.ReadSheet
 *  com.alibaba.excel.write.builder.ExcelWriterSheetBuilder
 *  com.alibaba.excel.write.metadata.WriteSheet
 *  com.dahua.panda.base.core.code.CommonErrorCode
 *  com.dahua.panda.base.core.code.IErrorCode
 *  com.dahua.panda.base.core.exception.BusinessException
 *  com.dahua.panda.base.core.exception.SystemException
 *  com.dahua.panda.base.spring.request.RequestContextHelper
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletResponse
 *  lombok.NonNull
 */
package com.github.iappapp.panda.common.office.excel;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.iappapp.panda.code.CommonErrorCode;
import com.github.iappapp.panda.exception.BusinessException;
import com.github.iappapp.panda.exception.SystemException;
import com.github.iappapp.panda.request.RequestContextHelper;
import lombok.NonNull;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ExcelHelper {
    private static final String RESPONSE_CHARACTER_ENCODING = "utf-8";
    private static final String RESPONSE_CONTENT_TYPE = "application/octet-stream";

    public static void downloadFile(HttpServletResponse response, String filePath, String fileName) throws IOException {
        BufferedInputStream inputStream = FileUtil.getInputStream(filePath);
        ExcelHelper.configResponse(response, StrUtil.isBlank(fileName) ? filePath : fileName);
        byte[] bytes = IoUtil.readBytes(inputStream);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.close();
    }

    private static void configResponse(HttpServletResponse response, String fileName) {
        String encodedFileName = ExcelHelper.encodeFileName(FileUtil.getName(fileName));
        response.setHeader("Content-disposition", "attachment;filename=" + encodedFileName);
        response.setContentType(RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(RESPONSE_CHARACTER_ENCODING);
    }

    private static String encodeFileName(String fileName) {
        String firefox;
        String agent = RequestContextHelper.getRequest().getHeader("User-Agent");
        if (StrUtil.containsIgnoreCase(agent, "Firefox")) {
            return "=?utf-8?B?" + Base64.encode(fileName) + "?=";
        }
        return URLUtil.encode(fileName);
    }

    @Deprecated
    public static void write(HttpServletResponse response, String templateFilePath, String fileName, List<?> ... lists) throws IOException {
        if (StrUtil.isBlank(templateFilePath) || null == response) {
            throw new SystemException( CommonErrorCode.SYSTEM_ERROR);
        }
        if (!ExcelHelper.checkLists(lists)) {
            throw new BusinessException(PandaExcelErrorCode.EMPTY_DATA);
        }
        ExcelHelper.configResponse(response, fileName);
        LinkedHashMap<WriteSheet, List<?>> sheetDataMap = new LinkedHashMap();
        for (int i = 0; i < lists.length; ++i) {
            List<?> list = lists[i];
            if (!CollUtil.isNotEmpty(list)) {
                continue;
            }
            WriteSheet sheet = (EasyExcel.writerSheet(i).head(list.get(0).getClass())).build();
            sheetDataMap.put(sheet, list);
        }
        BufferedInputStream inputStream = FileUtil.getInputStream(templateFilePath);
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(inputStream).build();
        for (Map.Entry<WriteSheet, List<?>> entry : sheetDataMap.entrySet()) {
            excelWriter.write(entry.getValue(), entry.getKey());
        }
        excelWriter.finish();
    }

    public static void write(HttpServletResponse response, String fileName, List<?> ... lists) throws IOException {
        if (!ExcelHelper.checkLists(lists)) {
            throw new BusinessException(PandaExcelErrorCode.EMPTY_DATA);
        }
        ExcelHelper.configResponse(response, fileName);
        LinkedHashMap<WriteSheet, List<?>> sheetDataMap = new LinkedHashMap();
        for (int i = 0; i < lists.length; ++i) {
            List<?> list = lists[i];
            if (!CollUtil.isNotEmpty(list)) {
                continue;
            }
            WriteSheet sheet = (EasyExcel.writerSheet(("sheet" + (i + 1))).head(list.get(0).getClass())).build();
            sheetDataMap.put(sheet, list);
        }
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        for (Map.Entry<WriteSheet, List<?>> entry : sheetDataMap.entrySet()) {
            excelWriter.write(entry.getValue(), entry.getKey());
        }
        excelWriter.finish();
    }

    public static void write(HttpServletResponse response, String fileName, @NonNull Map<String, List<?>> sheetMap) throws IOException {
        if (sheetMap == null) {
            throw new NullPointerException("sheetMap is marked non-null but is null");
        }
        List[] lists = new List[sheetMap.size()];
        sheetMap.values().toArray(lists);
        if (!ExcelHelper.checkLists(lists)) {
            throw new BusinessException(PandaExcelErrorCode.EMPTY_DATA);
        }
        ExcelHelper.configResponse(response, fileName);
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        sheetMap.forEach((name, list) -> {
            if (CollUtil.isNotEmpty(list)) {
                WriteSheet sheet = (EasyExcel.writerSheet(name).head(list.get(0).getClass())).build();
                excelWriter.write(list, sheet);
            }
        });
        excelWriter.finish();
    }

    public static void fill(HttpServletResponse response, String templateFilePath, String fileName, List<?> list) throws IOException {
        if (StrUtil.isBlank(templateFilePath) || null == response) {
            throw new SystemException(CommonErrorCode.SYSTEM_ERROR);
        }
        if (CollUtil.isEmpty(list)) {
            throw new BusinessException(PandaExcelErrorCode.EMPTY_DATA);
        }
        ExcelHelper.configResponse(response, fileName);
        ServletOutputStream sos = response.getOutputStream();
        BufferedInputStream bis = FileUtil.getInputStream(templateFilePath);
        EasyExcel.write(sos).withTemplate(bis).sheet().doFill(list);
    }

    public static void fill(HttpServletResponse response, String templateFilePath, String fileName, Map<String, Object> map, List<?> list, boolean exportEmpty) throws IOException {
        if (StrUtil.isBlank(templateFilePath) || null == response) {
            throw new SystemException(CommonErrorCode.SYSTEM_ERROR);
        }
        if (CollUtil.isEmpty(list) && !exportEmpty) {
            throw new BusinessException(PandaExcelErrorCode.EMPTY_DATA);
        }
        ExcelHelper.configResponse(response, fileName);
        ServletOutputStream sos = response.getOutputStream();
        BufferedInputStream bis = FileUtil.getInputStream(templateFilePath);
        ExcelWriter excelWriter = EasyExcel.write(sos).withTemplate(bis).build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        excelWriter.fill(map, writeSheet);
        excelWriter.fill(list, writeSheet);
        excelWriter.finish();
    }

    private static boolean checkLists(List<?> ... list) {
        for (List<?> l : list) {
            if (!CollUtil.isNotEmpty(l)) continue;
            return true;
        }
        return false;
    }

    public static <T extends PandaExcelListener> List<T> read(InputStream inputStream, LinkedHashMap<Class<? extends ExcelModel>, T> map) {
        if (CollUtil.isEmpty(map)) {
            throw new SystemException(CommonErrorCode.SYSTEM_ERROR);
        }
        int i = 0;
        ArrayList<ReadSheet> sheets = new ArrayList<>(map.size());
        ArrayList<T> listeners = new ArrayList<T>(map.size());
        for (Map.Entry<Class<? extends ExcelModel>, T> entry : map.entrySet()) {
            ReadSheet sheet = ((EasyExcel.readSheet(i).head(entry.getKey())).registerReadListener(entry.getValue())).build();
            sheets.add(sheet);
            listeners.add(entry.getValue());
            ++i;
        }
        EasyExcel.read(inputStream).build().read(sheets.toArray(new ReadSheet[0])).finish();
        return listeners;
    }

    public static <T extends PandaExcelListener> CompletableFuture readAsync(InputStream inputStream, LinkedHashMap<Class<? extends ExcelModel>, T> map) {
        return CompletableFuture.runAsync(() -> ExcelHelper.read(inputStream, map));
    }
}


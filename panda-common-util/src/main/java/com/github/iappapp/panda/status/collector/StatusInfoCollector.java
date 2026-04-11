package com.github.iappapp.panda.status.collector;

import com.alibaba.fastjson.JSON;
import com.github.iappapp.panda.status.model.GcInfo;
import com.github.iappapp.panda.status.model.MemoryInfo;
import com.github.iappapp.panda.status.model.OsInfo;
import com.github.iappapp.panda.status.model.ThreadsInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.*;
import java.util.List;

/**
 * @author iappapp
 * @date 2020/11/12 14:30
 */
@Slf4j
public class StatusInfoCollector {
    private static final long ONE_MB = 1024 * 1024;

    public static MemoryInfo visitMemory() {
        MemoryInfo memory = new MemoryInfo();
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();
        Runtime runtime = Runtime.getRuntime();

        memory.setMax(runtime.maxMemory() / ONE_MB);
        memory.setTotal(runtime.totalMemory() / ONE_MB);
        memory.setFree(runtime.freeMemory() / ONE_MB);
        memory.setHeapUsage(bean.getHeapMemoryUsage().getUsed() / ONE_MB);
        memory.setNonHeapUsage(bean.getNonHeapMemoryUsage().getUsed() / ONE_MB);

        List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();

        for (GarbageCollectorMXBean mxBean : beans) {
            if (mxBean.isValid()) {
                GcInfo gc = new GcInfo();
                String name = mxBean.getName();
                long count = mxBean.getCollectionCount();

                gc.setName(name);
                gc.setCount(count);
                gc.setTime(mxBean.getCollectionTime());
                memory.addGc(gc);
            }
        }

        for (MemoryPoolMXBean mpBean : ManagementFactory.getMemoryPoolMXBeans()) {
            String name = mpBean.getName();
            MemoryUsage usage = mpBean.getUsage();
            log.info("MemoryPoolMXBean name={} MemoryUsage={}", name, JSON.toJSONString(usage));
        }
        log.info("visitMemory memory={}", memory);
        return memory;
    }

    public static ThreadsInfo visitThread() {
        ThreadsInfo thread = new ThreadsInfo();
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        bean.setThreadContentionMonitoringEnabled(true);

        ThreadInfo[] threads = bean.dumpAllThreads(false, false);
        int resinThreadCount = countThreadsBySubstring(threads, "resin-port");

        thread.setCount(bean.getThreadCount());
        thread.setDaemonCount(bean.getDaemonThreadCount());
        thread.setPeekCount(bean.getPeakThreadCount());
        thread.setTotalStartedCount((int) bean.getTotalStartedThreadCount());
        thread.setHttpThreadCount(resinThreadCount);

        log.info("visitThread thread={}", thread);
        return thread;
    }

    public static void visitOs() {
        OsInfo os = new OsInfo();
        OperatingSystemMXBean bean = ManagementFactory.getOperatingSystemMXBean();

        os.setArch(bean.getArch());
        os.setName(bean.getName());
        os.setVersion(bean.getVersion());
        os.setAvailableProcessors(bean.getAvailableProcessors());
        os.setSystemLoadAverage(bean.getSystemLoadAverage());

        // for Sun JDK
        if (isInstanceOfInterface(bean.getClass(), "com.sun.management.OperatingSystemMXBean")) {
            com.sun.management.OperatingSystemMXBean b = (com.sun.management.OperatingSystemMXBean) bean;

            os.setTotalPhysicalMemory(b.getTotalPhysicalMemorySize() / ONE_MB);
            os.setFreePhysicalMemory(b.getFreePhysicalMemorySize() / ONE_MB);
            os.setTotalSwapSpace(b.getTotalSwapSpaceSize() / ONE_MB);
            os.setFreeSwapSpace(b.getFreeSwapSpaceSize() / ONE_MB);
            os.setProcessTime(b.getProcessCpuTime());
            os.setCommittedVirtualMemory(b.getCommittedVirtualMemorySize() / ONE_MB);
        }
        log.info("visitOs os={}", os);
    }

    private static boolean isInstanceOfInterface(Class<?> clazz, String interfaceName) {
        if (clazz == Object.class) {
            return false;
        } else if (clazz.getName().equals(interfaceName)) {
            return true;
        }

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> interfaceClass : interfaces) {
            if (isInstanceOfInterface(interfaceClass, interfaceName)) {
                return true;
            }
        }

        return isInstanceOfInterface(clazz.getSuperclass(), interfaceName);
    }

    private static int countThreadsBySubstring(ThreadInfo[] threads, String... substrings) {
        int count = 0;

        for (ThreadInfo thread : threads) {
            for (String str : substrings) {
                if (thread.getThreadName().contains(str)) {
                    count++;
                }
            }
        }

        return count;
    }
}

package com.fosun.fc.projects.creepers.monitor;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SystemMonitor {

    public static Map<String, String> getHostBasicInfo() {
        Map<String, String> map = new HashMap<String, String>();
        InetAddress ia = null;
        try {
            ia = InetAddress.getLocalHost();
            map.put("hostIp", ia.getHostAddress());
            map.put("hostName", ia.getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        map.put("osName", os.getName());
        map.put("osArch", os.getArch());
        map.put("osVersion", os.getVersion());
        map.put("osProcessors", String.valueOf(os.getAvailableProcessors()));
        map.put("jvmVersion", System.getProperty("java.version"));
        map.put("jvmHome", System.getProperty("java.home"));
        return map;
    }
    
    public static Map<String, Object> getHostRealTimeInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = mem.getHeapMemoryUsage();
        map.put("MaxHeap", heap.getMax()/(1024*1024));
        map.put("UsedHeap", heap.getUsed()/(1024*1024));
        return map;
    }

    /**
     * Java 虚拟机在其上运行的操作系统
     */
    public static void showSystem() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("System name: " + os.getName());
        System.out.println("Processors: " + os.getAvailableProcessors());
        System.out.println("Architecture: " + os.getArch());
        System.out.println("System version: " + os.getVersion());
        System.out.println("Last minute load: " + os.getSystemLoadAverage());
    }

    /**
     * Java 虚拟机的运行时系统
     */
    public static void showJvmInfo() {
        RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
        System.out.println("jvm name:" + mxbean.getVmName());
        System.out.println("jvm version:" + System.getProperty("java.version") + " (" + mxbean.getVmVersion() + ")");
        System.out.println("jvm vendor:" + mxbean.getVmVendor());
        System.out.println("jvm start time:" + mxbean.getStartTime());
        System.out.println("jvm inputArguments:" + mxbean.getInputArguments());
        System.out.println("jvm systemProperties:" + mxbean.getSystemProperties());
    }

    /**
     * Java 虚拟机的内存系统
     */
    public static void showMemoryInfo() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = mem.getHeapMemoryUsage();
        System.out.println("Heap committed:" + heap.getCommitted() + " init:" + heap.getInit() + " max:" + heap.getMax()
                + " used:" + heap.getUsed());
        Runtime run = Runtime.getRuntime();

        long max = run.maxMemory();

        long total = run.totalMemory();

        long free = run.freeMemory();

        long usable = max - total + free;

        System.out.println("最大内存 = " + max);
        System.out.println("已分配内存 = " + total);
        System.out.println("已分配内存中的剩余空间 = " + free);
        System.out.println("最大可用内存 = " + usable);
    }

    /**
     * Java 虚拟机的类加载系统
     */
    public static void showClassLoading() {
        ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
        System.out.println("TotalLoadedClassCount: " + cl.getTotalLoadedClassCount());
        System.out.println("LoadedClassCount" + cl.getLoadedClassCount());
        System.out.println("UnloadedClassCount:" + cl.getUnloadedClassCount());
    }

    /**
     * Java 虚拟机的编译系统
     */
    public static void showCompilation() {
        CompilationMXBean com = ManagementFactory.getCompilationMXBean();
        System.out.println("TotalCompilationTime:" + com.getTotalCompilationTime());
        System.out.println("name:" + com.getName());
    }

    /**
     * Java 虚拟机的线程系统
     */
    public static void showThread() {
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        System.out.println("ThreadCount" + thread.getThreadCount());
        System.out.println("AllThreadIds:" + thread.getAllThreadIds());
        System.out.println("CurrentThreadUserTime" + thread.getCurrentThreadUserTime());
        // ......还有其他很多信息
    }

    /**
     * Java 虚拟机中的垃圾回收器。
     */
    public static void showGarbageCollector() {
        List<GarbageCollectorMXBean> gc = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean GarbageCollectorMXBean : gc) {
            System.out.println("name:" + GarbageCollectorMXBean.getName());
            System.out.println("CollectionCount:" + GarbageCollectorMXBean.getCollectionCount());
            System.out.println("CollectionTime" + GarbageCollectorMXBean.getCollectionTime());
        }
    }

    /**
     * Java 虚拟机中的内存管理器
     */
    public static void showMemoryManager() {
        List<MemoryManagerMXBean> mm = ManagementFactory.getMemoryManagerMXBeans();
        for (MemoryManagerMXBean eachmm : mm) {
            System.out.println("name:" + eachmm.getName());
            System.out.println("MemoryPoolNames:" + eachmm.getMemoryPoolNames().toString());
        }
    }

    /**
     * Java 虚拟机中的内存池
     */
    public static void showMemoryPool() {
        List<MemoryPoolMXBean> mps = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean mp : mps) {
            System.out.println("name:" + mp.getName());
            System.out.println("CollectionUsage:" + mp.getCollectionUsage());
            System.out.println("type:" + mp.getType());
        }
    }

    /**
     * 访问 MXBean 的方法的三种方法
     */
    public static void visitMBean() {

        // 第一种直接调用同一 Java 虚拟机内的 MXBean 中的方法。
        RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
        String vendor1 = mxbean.getVmVendor();
        System.out.println("vendor1:" + vendor1);

        // 第二种通过一个连接到正在运行的虚拟机的平台 MBeanServer 的 MBeanServerConnection。
        // MBeanServerConnection mbs = null;
        // Connect to a running JVM (or itself) and get MBeanServerConnection
        // that has the JVM MXBeans registered in it

        /*
         * try { // Assuming the RuntimeMXBean has been registered in mbs
         * ObjectName oname = new
         * ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME); String vendor2 =
         * (String) mbs.getAttribute(oname, "VmVendor");
         * System.out.println("vendor2:" + vendor2); } catch (Exception e) {
         * e.printStackTrace(); }
         */

        // 第三种使用 MXBean 代理
        // MBeanServerConnection mbs3 = null;
        // RuntimeMXBean proxy;
        // try {
        // proxy =
        // ManagementFactory.newPlatformMXBeanProxy(mbs3,ManagementFactory.RUNTIME_MXBEAN_NAME,
        // RuntimeMXBean.class);
        // String vendor = proxy.getVmVendor();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    public static void getSysemData() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get") && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                }       // try
                System.out.println(method.getName() + " = " + value);
            }       // if
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = getHostRealTimeInfo();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            System.out.println(key + ":" + map.get(key));
        }
//        showJvmInfo();
//         showMemoryInfo();
        // showSystem();
        // showClassLoading();
        // showCompilation();
        // showThread();
        // showGarbageCollector();
        // showMemoryManager();
        // showMemoryPool();
//        getSysemData();
//        Map<String, String> map = getHostBasicInfo();
//        Set<String> keySet = map.keySet();
//        for (String key : keySet) {
//            System.out.println(key + ":" + map.get(key));
//        }
    }
}

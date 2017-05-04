package com.fosun.fc.projects.creepers.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.Swap;

public class SystemInfoUtil {
    public static Map<String, Object> SystemProperty() {
        Map<String, Object> monitorMap = new HashMap<String, Object>();
        Runtime r = Runtime.getRuntime();
        Properties props = System.getProperties();
        InetAddress addr = null;
        String ip = "";
        String hostName = "";
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            ip = "无法获取主机IP";
            hostName = "无法获取主机名";
        }
        if (null != addr) {
            try {
                ip = addr.getHostAddress();
            } catch (Exception e) {
                ip = "无法获取主机IP";
            }
            try {
                hostName = addr.getHostName();
            } catch (Exception e) {
                hostName = "无法获取主机名";
            }
        }
        monitorMap.put("hostIp", ip);// 本地ip地址
        monitorMap.put("hostName", hostName);// 本地主机名
        monitorMap.put("osName", props.getProperty("os.name"));// 操作系统的名称
        monitorMap.put("arch", props.getProperty("os.arch"));// 操作系统的构架
        monitorMap.put("osVersion", props.getProperty("os.version"));// 操作系统的版本
        monitorMap.put("processors", r.availableProcessors());// JVM可以使用的处理器个数
        monitorMap.put("javaVersion", props.getProperty("java.version"));// Java的运行环境版本
        monitorMap.put("vendor", props.getProperty("java.vendor"));// Java的运行环境供应商
        monitorMap.put("javaUrl", props.getProperty("java.vendor.url"));// Java供应商的URL
        monitorMap.put("javaHome", props.getProperty("java.home"));// Java的安装路径
        monitorMap.put("tmpdir", props.getProperty("java.io.tmpdir"));// 默认的临时文件路径
        return monitorMap;
    }

    public static Map<String, Object> memory(Sigar sigar) {
        Map<String, Object> monitorMap = new HashMap<String, Object>();
        try {
            Runtime r = Runtime.getRuntime();
            monitorMap.put("jvmTotal", CommonMethodUtils.div(r.totalMemory(), (1024 * 1024), 2) + "M");// java总内存
            monitorMap.put("jvmUse", CommonMethodUtils.div(r.totalMemory() - r.freeMemory(), (1024 * 1024), 2) + "M");// JVM使用内存
            monitorMap.put("jvmFree", CommonMethodUtils.div(r.freeMemory(), (1024 * 1024), 2) + "M");// JVM剩余内存
            monitorMap.put("jvmUsage", CommonMethodUtils.div(r.totalMemory() - r.freeMemory(), r.totalMemory(), 2));// JVM使用率

            Mem mem = sigar.getMem();
            // 内存总量
            monitorMap.put("ramTotal", CommonMethodUtils.div(mem.getTotal(), (1024 * 1024 * 1024), 2) + "G");// 内存总量
            monitorMap.put("ramUse", CommonMethodUtils.div(mem.getUsed(), (1024 * 1024 * 1024), 2) + "G");// 当前内存使用量
            monitorMap.put("ramFree", CommonMethodUtils.div(mem.getFree(), (1024 * 1024 * 1024), 2) + "G");// 当前内存剩余量
            monitorMap.put("ramUsage", CommonMethodUtils.div(mem.getUsed(), mem.getTotal(), 2));// 内存使用率

            Swap swap = sigar.getSwap();
            // 交换区总量
            monitorMap.put("swapTotal", CommonMethodUtils.div(swap.getTotal(), (1024 * 1024 * 1024), 2) + "G");
            // 当前交换区使用量
            monitorMap.put("swapUse", CommonMethodUtils.div(swap.getUsed(), (1024 * 1024 * 1024), 2) + "G");
            // 当前交换区剩余量
            monitorMap.put("swapFree", CommonMethodUtils.div(swap.getFree(), (1024 * 1024 * 1024), 2) + "G");
            monitorMap.put("swapUsage", CommonMethodUtils.div(swap.getUsed(), swap.getTotal(), 2));//

        } catch (Exception e) {
        }
        return monitorMap;
    }
    public static Map<String, Object> usage(Sigar sigar) {
        Map<String, Object> monitorMap = new HashMap<String, Object>();
        try {
            Runtime r = Runtime.getRuntime();
            monitorMap.put("jvmUsage", Math.round(CommonMethodUtils.div(r.totalMemory()-r.freeMemory(), r.totalMemory(), 2)*100));// JVM使用率

            Mem mem = sigar.getMem();
            // 内存总量
            monitorMap.put("ramUsage", Math.round(CommonMethodUtils.div(mem.getUsed(), mem.getTotal(), 2)*100));// 内存使用率

            List<Map<String, Object>> cpu = cpuInfos(sigar);
            double b = 0.0;
            for (Map<String, Object> m : cpu) {
                b += Double.valueOf(m.get("cpuTotal")+"");
            }
            monitorMap.put("cpuUsage", Math.round(b/cpu.size()));// cpu使用率
        } catch (Exception e) {
        }
        return monitorMap;
    }

    public static List<Map<String, Object>> cpuInfos(Sigar sigar) {
        List<Map<String, Object>> monitorMaps = new ArrayList<Map<String, Object>>();
        try {
            CpuPerc cpuList[] = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuList) {
                Map<String, Object> monitorMap = new HashMap<String, Object>();
                monitorMap.put("cpuUserUse", Math.round(cpuPerc.getUser()*100));// 用户使用率
                monitorMap.put("cpuSysUse", Math.round(cpuPerc.getSys()*100));// 系统使用率
                monitorMap.put("cpuWait", Math.round(cpuPerc.getWait()*100));// 当前等待率
                monitorMap.put("cpuFree", Math.round(cpuPerc.getIdle()*100));// 当前空闲率
                monitorMap.put("cpuTotal",Math.round(cpuPerc.getCombined()*100));// 总的使用率
                monitorMaps.add(monitorMap);
            }
        } catch (Exception e) {
        }
        return monitorMaps;
    }

    public List<Map<String, Object>> diskInfos(Sigar sigar) throws Exception {
        List<Map<String, Object>> monitorMaps = new ArrayList<Map<String, Object>>();
        FileSystem fslist[] = sigar.getFileSystemList();
        for (int i = 0; i < fslist.length; i++) {
            Map<String, Object> monitorMap = new HashMap<String, Object>();
            FileSystem fs = fslist[i];
            // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
            FileSystemUsage usage = null;
            usage = sigar.getFileSystemUsage(fs.getDirName());
            switch (fs.getType()) {
            case 0: // TYPE_UNKNOWN ：未知
                break;
            case 1: // TYPE_NONE
                break;
            case 2: // TYPE_LOCAL_DISK : 本地硬盘

                monitorMap.put("diskName", fs.getDevName());// 系统盘名称
                monitorMap.put("diskType", fs.getSysTypeName());// 盘类型
                // 文件系统总大小
                monitorMap.put("diskTotal", fs.getSysTypeName());
                // 文件系统剩余大小
                monitorMap.put("diskFree", usage.getFree());
                // 文件系统已经使用量
                monitorMap.put("diskUse", usage.getUsed());
                double usePercent = usage.getUsePercent() * 100D;
                // 文件系统资源的利用率
                monitorMap.put("diskUsage", usePercent);// 内存使用率
                monitorMaps.add(monitorMap);
                break;
            case 3:// TYPE_NETWORK ：网络
                break;
            case 4:// TYPE_RAM_DISK ：闪存
                break;
            case 5:// TYPE_CDROM ：光驱
                break;
            case 6:// TYPE_SWAP ：页面交换
                break;
            }
        }
        return monitorMaps;
    }
    
    public static void main(String args[]){
//      System.out.println(System.getProperty("java.library.path"));
        Map<String, Object> map=usage(new Sigar());
        System.out.println(map);
    }
}
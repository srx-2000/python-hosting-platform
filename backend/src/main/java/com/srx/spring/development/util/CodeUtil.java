package com.srx.spring.development.util;

import com.srx.spring.development.Entities.Task;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

public class CodeUtil {

    private static PropertiesLoader loader = new PropertiesLoader("config.properties");
    private static String windows_root_path = loader.getValue("windows.root.path");
    private static String linux_root_path = loader.getValue("linux.root.path");

    public static final int WINDOWS_ROOT_PATH = 1;
    public static final int LINUX_ROOT_PATH = 2;
    public static final int TASK_PATH = 3;
    public static final int USER_PATH = 4;
    public static final int ENV_PATH = 5;
    public static final int PY_PATH = 6;
    public static final int INPUT_PATH = 7;
    public static final int OUTPUT_PATH = 8;

    // 获取uuid标识
    public static String get_uuid() {
        return UUID.randomUUID().toString();
    }


    //将传入的字符串编码为MD5加密格式
    public static String get_MD5_code(String uncodedString) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    uncodedString.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static String encodeEnvName(String envName, String userKey) {
        return envName + "@@" + userKey;
    }

    public static String decodeEnvName(String encodedEnName) {
        String[] split = encodedEnName.split("@@");
        return split[0];
    }

    public static String getPath(String userKey, String taskKey, String envName, Integer mode) {
        switch (mode) {
            case WINDOWS_ROOT_PATH:
                return windows_root_path;
            case LINUX_ROOT_PATH:
                return linux_root_path;
            case USER_PATH:
                if (!CommandUtil.isWindows())
                    return linux_root_path + userKey + "/";
                else
                    return windows_root_path + userKey + "/";
            case ENV_PATH:
                if (!CommandUtil.isWindows())
                    return linux_root_path + userKey + "/" + envName + "/";
                else
                    return windows_root_path + userKey + "/" + envName + "/";
            case TASK_PATH:
                if (!CommandUtil.isWindows())
                    return linux_root_path + userKey + "/" + envName + "/" + taskKey + "/";
                else
                    return windows_root_path + userKey + "/" + envName + "/" + taskKey + "/";
            case PY_PATH:
                if (!CommandUtil.isWindows())
                    return linux_root_path + userKey + "/" + envName + "/" + taskKey + "/py/";
                else
                    return windows_root_path + userKey + "/" + envName + "/" + taskKey + "/py/";
            case INPUT_PATH:
                if (!CommandUtil.isWindows())
                    return linux_root_path + userKey + "/" + envName + "/" + taskKey + "/input/";
                else
                    return windows_root_path + userKey + "/" + envName + "/" + taskKey + "/input/";
            case OUTPUT_PATH:
                if (!CommandUtil.isWindows())
                    return linux_root_path + userKey + "/" + envName + "/" + taskKey + "/output/";
                else
                    return windows_root_path + userKey + "/" + envName + "/" + taskKey + "/output/";
            default:
                return null;
        }
    }

    public static String decodePip(String pipString) {
        String warning = pipString.split("WARNING")[0];
        String[] split = warning.split("-------\n");
        return split[split.length - 1];
    }

    public static void taskStatusToString(List<Task> list) {
        for (Task task : list) {
            if (task.getStatus().equals("1")) {
                task.setStatus("未开始");
            }else if (task.getStatus().equals("2")){
                task.setStatus("已加入队列");
            }else if (task.getStatus().equals("0")){
                task.setStatus("正在运行中");
            }else if (task.getStatus().equals("-1")){
                task.setStatus("已完成");
            }
        }

    }
}

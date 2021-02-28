package com.srx.spring.development.util;

import com.srx.spring.development.Entities.Env;
import com.srx.spring.development.Entities.GPUInfo;
import com.srx.spring.development.Exception.CommandException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandUtil {

    private static PropertiesLoader loader = new PropertiesLoader("config.properties");
    public static final int MKDIR_USER = 1;
    public static final int MKDIR_ENV = 2;
    public static final int MKDIR_TASK = 3;
    public static final int MKDIR_PY = 4;
    public static final int MKDIR_INPUT = 5;
    public static final int MKDIR_OUTPUT = 6;
    public static final int COMMAND_STRING = 7;
    public static final int COMMAND_CODE = 8;
    public static final int LINUX = 9;
    public static final int WINDOWS = 10;
    private static String windowsRootPath = loader.getValue("windows.root.path");
    private static String linuxRootPath = loader.getValue("linux.root.path");


    public static String execProcess(String[] cmds, Integer mode) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmds);
            pb.redirectErrorStream(true);
            Process pr = pb.start();
            InputStream in = pr.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, "GBK"));
            String line = null;
            List<String> stringList = new ArrayList<>();
            String result = "";
            while ((line = read.readLine()) != null) {
                stringList.add(line);
//                System.out.println(line);
                result += line + "\n";
//                log.info("logging=======>" + line);
            }
            int exeCode = pr.waitFor();
            if (exeCode!=0){
                log.error(result);
            }else{
                log.info(line);
            }
            if (mode == COMMAND_CODE) {
                return String.valueOf(exeCode);
            } else if (mode == COMMAND_STRING) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGpu() {
        Process process = null;
        String os = System.getProperty("os.name");
        try {
            if (os.toLowerCase().startsWith("win")) {
                String[] shell = {"nvidia-smi.exe"};
                process = Runtime.getRuntime().exec(shell);
            } else if (os.toLowerCase().startsWith("lin")) {
                String[] shell = {"/bin/bash", "-c", "nvidia-smi"};
                process = Runtime.getRuntime().exec(shell);
            }

            process.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
//            throw new IndaiException("显卡不存在或获取显卡信息失败");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuffer stringBuffer = new StringBuffer();
        String line = "";
        while (true) {
            try {
                if (!(null != (line = reader.readLine()))) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuffer.append(line + "\n");
        }
        return stringBuffer.toString();
    }


    public static List<GPUInfo> getGpuInfos(Integer statusLine) {
        String gpus = null;
        gpus = CommandUtil.getGpu();
        //分割废物信息
        String[] split = gpus.split("\\|===============================\\+======================\\+======================\\|");
        for (int i = 0; i < split.length; i++) {
            System.out.println(split[i]);
        }
        String[] gpusInfo = split[1].split("                                                                               ");
        // 分割多个gpu
        String[] gpuInfo = gpusInfo[0].split("\\+-------------------------------\\+----------------------\\+----------------------\\+");
        //System.out.println("000000000000000000000000000000000");
        List<GPUInfo> gpuInfoList = new ArrayList<>();
        for (int i = 0; i < gpuInfo.length - 1; i++) {
            GPUInfo gpuInfo1 = new GPUInfo();
            String[] nameAndInfo = gpuInfo[i].split("\n");
            String[] split1 = nameAndInfo[1].split("\\|")[1] // 0  TITAN V             Off
                    .split("\\s+");//去空格

            gpuInfo1.setNumber(Integer.parseInt(split1[1]));
            StringBuffer name = new StringBuffer();
            for (int j = 0; j < split1.length - 1; j++) {
                if (j > 1 && j != split1.length) {
                    name.append(split1[j] + " ");
                }
            }
            gpuInfo1.setName(name.toString());

            String[] info = nameAndInfo[2].split("\\|")[2].split("\\s+");
            gpuInfo1.setUsedMemory(info[1]);
            gpuInfo1.setTotalMemory(info[3]);
            int useable = Integer.parseInt(gpuInfo1.getTotalMemory().split("MiB")[0]) - Integer.parseInt(gpuInfo1.getUsedMemory().split("MiB")[0]);
            gpuInfo1.setUsableMemory(useable + "MiB");
            Double usageRate = Integer.parseInt(gpuInfo1.getUsedMemory().split("MiB")[0]) * 100.00 / Integer.parseInt(gpuInfo1.getTotalMemory().split("MiB")[0]);
            gpuInfo1.setUsageRate(usageRate);
            if (gpuInfo1.getUsageRate() >= statusLine) {
                gpuInfo1.setStatus("1");
            } else if (gpuInfo1.getUsageRate() <= statusLine) {
                gpuInfo1.setStatus("0");
            } else {
                gpuInfo1.setStatus("-1");
            }
            gpuInfoList.add(gpuInfo1);
        }
        System.out.println(gpuInfoList);
        return gpuInfoList;
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return true;
        } else
            return false;

    }

    public static boolean mkdir(String userKey, String envName, String taskKey, Integer mode, Integer platform) {
        if (platform == WINDOWS) {
            return privateMkdir(userKey, envName, taskKey, mode, windowsRootPath);
        } else if (platform == LINUX) {
            return privateMkdir(userKey, envName, taskKey, mode, linuxRootPath);
        }
        return false;
    }

    private static boolean privateMkdir(String userKey, String envName, String taskKey, Integer mode, String platformRootPath) {
        File dir;
        switch (mode) {
            case MKDIR_USER:
                dir = new File(platformRootPath + userKey);
                if (!dir.exists()) {
                    boolean mkdir = dir.mkdir();
                    if (mkdir) log.info("文件夹" + userKey + "创建成功");
                    else log.warn("文件夹" + userKey + "创建失败，可能是因为上级目录并不存在");
                    return mkdir;
                } else {
                    log.warn("文件夹" + userKey + "已存在");
                    return false;
                }
            case MKDIR_ENV:
                String[] command;
                if (platformRootPath.contains(":")) {
                    if (platformRootPath.contains("\\")) {
                        command = new String[]{"cmd", "/c", platformRootPath.split("\\\\")[0] + "&& cd " + platformRootPath + "/" + userKey + "&&virtualenv " + envName};
                    } else {
                        command = new String[]{"cmd", "/c", platformRootPath.split("/")[0] + "&& cd " + platformRootPath + "/" + userKey + "&&virtualenv " + envName};
                    }
                } else {
                    command = new String[]{"/bash/bin", "-c", "cd " + platformRootPath + "/" + userKey + ";virtualenv " + envName};
                }
                String resultCode = execProcess(command, COMMAND_CODE);
                if (resultCode.equals("0")) log.info("文件夹" + envName + "创建成功");
                else log.warn("文件夹" + envName + "创建失败，可能是因为上级目录并不存在");
                return true;
            case MKDIR_TASK:
                dir = new File(platformRootPath + userKey + "/" + envName + "/" + taskKey);
                if (!dir.exists()) {
                    boolean mkdir = dir.mkdir();
                    if (mkdir) log.info("文件夹" + taskKey + "创建成功");
                    else log.warn("文件夹" + taskKey + "创建失败，可能是因为上级目录并不存在");
                    return mkdir;
                } else {
                    log.warn("文件夹" + taskKey + "已存在");
                    return false;
                }
            case MKDIR_PY:
                dir = new File(platformRootPath + userKey + "/" + envName + "/" + taskKey + "/py");
                if (!dir.exists()) {
                    boolean mkdir = dir.mkdir();
                    if (mkdir) log.info("文件夹" + taskKey + "的py文件夹创建成功");
                    else log.warn("文件夹" + taskKey + "的py文件夹创建失败，可能是因为上级目录并不存在");
                    return mkdir;
                } else {
                    log.warn("文件夹" + taskKey + "的py文件夹已存在");
                    return false;
                }
            case MKDIR_INPUT:
                dir = new File(platformRootPath + userKey + "/" + envName + "/" + taskKey + "/input");
                if (!dir.exists()) {
                    boolean mkdir = dir.mkdir();
                    if (mkdir) log.info("文件夹" + taskKey + "的input文件夹创建成功");
                    else log.warn("文件夹" + taskKey + "的input文件夹创建失败，可能是因为上级目录并不存在");
                    return mkdir;
                } else {
                    log.warn("文件夹" + taskKey + "的input文件夹已存在");
                    return false;
                }
            case MKDIR_OUTPUT:
                dir = new File(platformRootPath + userKey + "/" + envName + "/" + taskKey + "/output");
                if (!dir.exists()) {
                    boolean mkdir = dir.mkdir();
                    if (mkdir) log.info("文件夹" + taskKey + "的output文件夹创建成功");
                    else log.warn("文件夹" + taskKey + "的output文件夹创建失败，可能是因为上级目录并不存在");
                    return mkdir;
                } else {
                    log.warn("文件夹" + taskKey + "的output文件夹已存在");
                    return false;
                }
        }
        return false;
    }

    /**
     * 传入的env中必须包含userKey，envName
     *
     * @param env
     * @param platform
     * @return
     */
    public static String getPipList(Env env, Integer platform) {
        if (platform == WINDOWS) {
            String[] split = getPathArray();
            String path = split[0] + " && cd " + windowsRootPath + env.getUserKey() + "/" + CodeUtil.decodeEnvName(env.getEnvName()) + "/Scripts";
            String[] command = {"cmd", "/c", path + "&& activate.bat && pip list"};
            String list = CommandUtil.execProcess(command, CommandUtil.COMMAND_STRING);
            if (list.contains("pip"))
                return list;
            else {
                throw new CommandException("获取pip list失败");
            }
        } else if (platform == LINUX) {
            String path = "cd " + linuxRootPath + env.getUserKey() + "/" + env.getEnvName().split("@@")[0];
            String[] command = {"/bin/bash", "-c", path + ";source ./bin/activate;pip list"};
            String list = CommandUtil.execProcess(command, CommandUtil.COMMAND_STRING);
            if (list.contains("pip"))
                return list;
            else {
                throw new CommandException("获取pip list失败");
            }
        }
        return null;
    }

    public static Boolean isPathExists(String path, Integer platform) {
        if (platform == WINDOWS) {
            String[] split = getPathArray();
            String commandPath = split[0] + " && cd " + windowsRootPath + path + "&& dir";
            String[] command = {"cmd", "/c", commandPath};
            String s = execProcess(command, COMMAND_CODE);
            if (s.equals("0"))
                return true;
            else
                return false;
        } else if (platform == LINUX) {
            String commandPath = "cd " + linuxRootPath + path + ";ls";
            String[] command = {"/bin/bash", "-c", commandPath};
            String s = execProcess(command, COMMAND_CODE);
            if (s.equals("0"))
                return true;
            else
                return false;
        }
        return false;
    }

    public static Boolean pipInstallRequirements(String userKey, String envName, String requirementsPath, Integer platform) {
        if (platform == WINDOWS) {
            String envPath = windowsRootPath + "/" + userKey + "/" + envName + "/Scripts";
            String[] split = getPathArray();
            String commandPath = split[0] + " && cd " + envPath + "&& activate.bat &&";
            String install = "pip install -r " + requirementsPath+" -i https://pypi.tuna.tsinghua.edu.cn/simple/";
            String[] command = {"cmd", "/c", commandPath + install};
            String flag = execProcess(command, COMMAND_CODE);
            String result = execProcess(command, COMMAND_STRING);
            if (flag.equals("0")) {
                return true;
            } else {
                log.warn(result);
                return false;
            }
        } else if (platform == LINUX) {
            String envPath = linuxRootPath + "/" + userKey + "/" + envName;
            String commandPath = "cd " + envPath + "&& source ./bin/activate &&";
            String install = "pip install -r " + requirementsPath;
            String[] command = {"/bin/bash", "-c", commandPath + install};
            String flag = execProcess(command, COMMAND_CODE);
            String result = execProcess(command, COMMAND_STRING);
            if (flag.equals("0"))
                return true;
            else {
                log.warn(result);
                return false;
            }
        }
        return false;
    }

    private static String[] getPathArray() {
        String[] split;
        if (windowsRootPath.contains("\\")) {
            split = windowsRootPath.split("\\\\");
        } else {
            split = windowsRootPath.split("/");
        }
        return split;
    }

    public static String runMain(String userKey, String taskKey, String envName, String pythonName, Integer platform) {
        String envPath = CodeUtil.getPath(userKey, taskKey, envName, CodeUtil.ENV_PATH);
        String pythonPath = CodeUtil.getPath(userKey, taskKey, envName, CodeUtil.PY_PATH);
        if (platform == WINDOWS) {
            String[] command = {"cmd", "/c", "D:&&cd " + envPath + "Scripts && activate.bat && cd " + pythonPath + "&&python " + pythonName};
            String exitCode = CommandUtil.execProcess(command, CommandUtil.COMMAND_CODE);
            return exitCode;
        } else if (platform == LINUX) {
            String[] command = {"/bin/bash", "-c", "cd " + envPath + ";source ./bin/activate;cd " + pythonPath + "&&python " + pythonName};
            String exitCode = CommandUtil.execProcess(command, CommandUtil.COMMAND_CODE);
            return exitCode;
        }
        return "1";
    }
}

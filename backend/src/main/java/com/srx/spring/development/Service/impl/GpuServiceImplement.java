package com.srx.spring.development.Service.impl;

import com.srx.spring.development.Entities.*;
import com.srx.spring.development.Mapper.GpuMapper;
import com.srx.spring.development.Service.FileService;
import com.srx.spring.development.Service.GpuService;
import com.srx.spring.development.Service.TaskService;
import com.srx.spring.development.util.CodeUtil;
import com.srx.spring.development.util.CommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class GpuServiceImplement implements GpuService {
    @Autowired
    private GpuMapper gpuMapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FileService<PyFile> pyFileService;

    @Autowired
    private FileService<InputFile> inputFileService;

    @Autowired
    private FileService<OutputFile> outputFileService;

    private Integer statusLine;

    public void setStatusLine(Integer statusLine) {
        this.statusLine = statusLine;
    }

    @Override
    public Boolean initGpu() {
        Integer count = 0;
        List<GPUInfo> gpuInfos = CommandUtil.getGpuInfos(this.statusLine);
        for (GPUInfo gpuInfo : gpuInfos) {
            Boolean flag;
            try {
                flag = gpuMapper.insertGpu(gpuInfo);
            } catch (Exception e) {
                Integer integer = gpuMapper.updateStatus(gpuInfo);
                if (!integer.equals(0))
                    return false;
                else return true;
            }
            if (flag) {//插入失败有两种情况，一种是目前数据库中已经有该gpu，此时更新gpu。
                count++;
            }
        }
        if (count == gpuInfos.size())
            return true;
        return false;
    }


    @Override
    public List<GPUInfo> getGpuListFromDatabase() {
        List<GPUInfo> gpuInfos = gpuMapper.queryGpuList();
        return gpuInfos;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void run() {
        Integer statusLine = this.statusLine;
        List<GPUInfo> gpuInfos = CommandUtil.getGpuInfos(statusLine);
        for (int i = 0; i < gpuInfos.size(); i++) {
            gpuMapper.updateGpu(gpuInfos.get(i));
            Double rate = gpuInfos.get(i).getUsageRate();
            if (rate <= statusLine) {
                Task task = taskService.getFirstTask(String.valueOf(i));
                if (task == null) {
                    log.warn("队列为空");
                } else {
                    String envName = CodeUtil.decodeEnvName(task.getEnv().getEnvName());
                    String taskKey = task.getTaskKey();
                    List<PyFile> pyFiles = pyFileService.queryFileByTaskKey(taskKey, PyFile.class);
                    if (CommandUtil.isWindows()) {
                        int fileCount = 0;
                        for (PyFile file : pyFiles) {
                            String fileName = file.getFileName();
                            if (fileName.contains("Main") || fileName.contains("main") || fileName.contains("MAIN")) {
                                String exitCode = CommandUtil.runMain(task.getUserKey(), task.getTaskKey(), envName, file.getFileName(), CommandUtil.WINDOWS);
                                if (exitCode.equals("0")) {
                                    taskService.updateTaskStatusToComplete(taskKey);
                                    log.info("任务-" + task.getTaskTitle() + "完成");
                                } else {
                                    taskService.updateTaskStatusToComplete(taskKey);
                                    log.warn("任务-" + task.getTaskTitle() + "-task-key-" + task.getTaskKey() + "-执行报错，退出码-" + exitCode);
                                }
                            } else {
                                fileCount++;
                                if (fileCount == pyFiles.size()) {
                                    taskService.updateTaskStatusToComplete(taskKey);
                                    log.warn("任务-" + task.getTaskTitle() + "-task-key-" + task.getTaskKey() + "未找到程序主入口");
                                }
                                continue;
                            }
                        }
                        if (fileCount == pyFiles.size() && fileCount == 0) {
                            taskService.updateTaskStatusToComplete(taskKey);
                            log.warn("任务-" + task.getTaskTitle() + "-task-key-" + task.getTaskKey() + "未找到py文件");
                        }

                    } else {
                        int fileCount = 0;
                        for (PyFile file : pyFiles) {
                            String fileName = file.getFileName();
                            if (fileName.contains("Main") || fileName.contains("main")) {
                                String exitCode = CommandUtil.runMain(task.getUserKey(), task.getTaskKey(), envName, file.getFileName(), CommandUtil.LINUX);
                                if (exitCode.equals("0")) {
                                    taskService.updateTaskStatusToComplete(taskKey);
                                    log.info("任务-" + task.getTaskTitle() + "完成");
                                } else {
                                    taskService.updateTaskStatusToComplete(taskKey);
                                    log.warn("任务-" + task.getTaskTitle() + "-task-key-" + task.getTaskKey() + "-执行报错，退出码-" + exitCode);
                                }
                            } else {
                                fileCount++;
                                if (fileCount == pyFiles.size()) {
                                    taskService.updateTaskStatusToComplete(taskKey);
                                    log.warn("任务-" + task.getTaskTitle() + "-task-key-" + task.getTaskKey() + "未找到程序主入口");
                                }
                                continue;
                            }
                        }
                        if (fileCount == pyFiles.size() && fileCount == 0) {
                            taskService.updateTaskStatusToComplete(taskKey);
                            log.warn("任务-" + task.getTaskTitle() + "-task-key-" + task.getTaskKey() + "未找到py文件");
                        }
                    }
                }
            }
        }

    }

    @Override
    @Scheduled(fixedRate = 1000 * 60 * 60)
    public Boolean insertGpuPoint() {
        List<GPUInfo> gpuInfos = gpuMapper.queryGpuList();
        for (GPUInfo g : gpuInfos) {
            GPUDraw gpuDraw = new GPUDraw();
            gpuDraw.setName(g.getName());
            gpuDraw.setGpuId(String.valueOf(g.getNumber()));
            gpuDraw.setUsageRate(String.valueOf(g.getUsageRate()));
            gpuMapper.insertGpuDraw(gpuDraw);
            System.out.println("插入一个状态点");
        }
        return null;
    }

    @Override
    public List<String> getGpuStatusPointList(String gpuName, String gpuId) {
        List<String> list = gpuMapper.queryGpuStatusPointList(gpuName, gpuId);
        return list;
    }

//    public void test(){
//        Timer timer=new Timer();
//        TimerTask task=new TimerTask() {
//            @Override
//            public void run() {
//                List<GPUInfo> gpuInfos = CommandUtil.getGpuInfos(20);
//                for (GPUInfo gpuInfo :gpuInfos) {
//                    Integer integer = GpuServiceImplement.this.gpuMapper.updateGpu(gpuInfo);
//                    System.out.println(integer);
//                }
//            }
//        };
//        timer.schedule(task,0,1000);
//    }


}

package com.srx.spring.development.Controller;


import com.google.gson.Gson;
import com.srx.spring.development.Entities.DTO.ResultMessage;
import com.srx.spring.development.Entities.Env;
import com.srx.spring.development.Entities.InputFile;
import com.srx.spring.development.Entities.PyFile;
import com.srx.spring.development.Entities.Task;
import com.srx.spring.development.Service.EnvService;
import com.srx.spring.development.Service.FileService;
import com.srx.spring.development.Service.TaskService;
import com.srx.spring.development.util.CodeUtil;
import com.srx.spring.development.util.CommandUtil;
import com.srx.spring.development.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.srx.spring.development.Enum.ResultCode.*;
import static com.srx.spring.development.util.CommandUtil.LINUX;
import static com.srx.spring.development.util.CommandUtil.WINDOWS;

@RestController
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private EnvService envService;

    @Autowired
    private FileService<PyFile> pyFileFileService;

    @Autowired
    private FileService<InputFile> inputFileFileService;


    @PostMapping(value = "/insertTask")
    public ResultMessage insertTask(@RequestPart("task") String inputTask,
                                    @RequestPart("pyFiles") MultipartFile[] pyFiles,
                                    @RequestPart("inputFiles") MultipartFile[] inputFiles,
                                    @RequestPart("requirementsFile") MultipartFile file) throws IOException {
        try {
            Gson gson = new Gson();
            Task task = gson.fromJson(inputTask, Task.class);
            System.out.println("任务是：" + task);
            if (task.getTaskTitle() == null || task.getEnv().getEnvName() == null || task.getDeadTime() == null || task.getUserKey() == null || task.getGpuId() == null) {
                log.warn("传入的任务参数有误1");
                return new ResultMessage(ERROR_PARAM, null);
            }
            Task resultTask = taskService.insertTask(task);
            resultTask.getEnv().setEnvName(CodeUtil.decodeEnvName(resultTask.getEnv().getEnvName()));
            System.out.println(resultTask);
            if (resultTask != null) {
                FileUtil.uploadFile(file.getBytes(), task.getRequirementsPath(), null);
                for (MultipartFile m : pyFiles) {
                    if (m.isEmpty()) {
                        log.warn("上传的py文件有空文件");
                    } else {
                        String filePath = CodeUtil.getPath(resultTask.getUserKey(), resultTask.getTaskKey(), resultTask.getEnv().getEnvName(), CodeUtil.PY_PATH);
                        String fileName = m.getOriginalFilename();
                        byte[] bytes = m.getBytes();
                        if (filePath != null) {
                            PyFile pyFile = new PyFile();
                            pyFile.setFileName(fileName);
                            pyFile.setFilePath(filePath);
                            pyFile.setTaskKey(resultTask.getTaskKey());
                            Boolean pyFlag = pyFileFileService.insertFile(pyFile);
                            if (pyFlag) {
                                FileUtil.uploadFile(bytes, filePath, fileName);
                            } else
                                log.warn("文件：" + fileName + "上传失败");
                        } else
                            log.warn("py文件路径不正确");
                    }
                }
                for (MultipartFile m : inputFiles) {
                    if (m.isEmpty()) {
                        log.warn("上传的input文件有空文件");
                    } else {
                        String filePath = CodeUtil.getPath(resultTask.getUserKey(), resultTask.getTaskKey(), resultTask.getEnv().getEnvName(), CodeUtil.INPUT_PATH);
                        String fileName = m.getOriginalFilename();
                        byte[] bytes = m.getBytes();
                        if (filePath != null) {
                            InputFile inputFile = new InputFile();
                            inputFile.setFileName(fileName);
                            inputFile.setFilePath(filePath);
                            inputFile.setTaskKey(resultTask.getTaskKey());
                            Boolean inputFlag = inputFileFileService.insertFile(inputFile);
                            if (inputFlag) {
                                FileUtil.uploadFile(bytes, filePath, fileName);
                            } else
                                log.warn("文件：" + fileName + "上传失败");
                        } else
                            log.warn("input文件路径不正确");
                    }
                }
                Boolean flag = taskService.pipInstall(resultTask);
                Env env = new Env();
                env.setEnvName(CodeUtil.encodeEnvName(resultTask.getEnv().getEnvName(), resultTask.getUserKey()));
                env.setUserKey(resultTask.getUserKey());
                String pipList;
                if (CommandUtil.isWindows()) {
                    pipList = CommandUtil.getPipList(env, WINDOWS);
                } else {
                    pipList = CommandUtil.getPipList(env, LINUX);
                }
                env.setHasList(pipList);
                envService.updateEnvHasList(env);
                if (flag) {
                    return new ResultMessage(REQUIREMENTS_SUCCESS, flag);
                } else
                    return new ResultMessage(FAIL_INSTALL_REQUIREMENTS, "报错详情见log");
            } else {
                return new ResultMessage(ERROR_PARAM, task);
            }
        } catch (Exception e) {
            log.warn("传入的任务参数有误2");
            return new ResultMessage(ERROR_PARAM, null);
        }

    }

    @GetMapping("/getTaskListByUserKey")
    public ResultMessage showTaskListByUserKey(@RequestParam String userKey,
                                               @RequestParam String status,
                                               @RequestParam Integer currentPage,
                                               @RequestParam Integer pageSize) {
        List<Task> taskListByUserKey = taskService.getTaskListByUserKey(userKey, status, currentPage, pageSize);
        Integer taskCount = taskService.getTaskCountByUserKey(userKey, status);
        Map<String, Object> resultMap = new HashMap<>();
        if (taskListByUserKey.size() == 0) {
            return new ResultMessage(ERROR_NO_DATA, null);
        } else {
            CodeUtil.taskStatusToString(taskListByUserKey);
            resultMap.put("taskList", taskListByUserKey);
            resultMap.put("totalCount", taskCount);
            return new ResultMessage(DATA_RETURN_SUCCESS, resultMap);
        }
    }

    @GetMapping("/getTaskByTaskKey")
    public ResultMessage showTaskListByTaskKey(@RequestParam String taskKey) {
        Task taskByTaskKey = taskService.getTaskByTaskKey(taskKey);
        if (taskByTaskKey == null) {
            return new ResultMessage(ERROR_NO_DATA, null);
        } else {
            return new ResultMessage(DATA_RETURN_SUCCESS, taskByTaskKey);
        }
    }

    @GetMapping("/getTaskListByEnvName")
    public ResultMessage showTaskListByEnvName(@RequestParam String envName,
                                               @RequestParam String userKey,
                                               @RequestParam String status,
                                               @RequestParam Integer currentPage,
                                               @RequestParam Integer pageSize) {
        String name = CodeUtil.encodeEnvName(envName, userKey);
        List<Task> taskListByEnvName = taskService.getTaskListByEnvName(name, status, currentPage, pageSize);
        Integer taskCount = taskService.getTaskCountByEnvName(name, status);
        Map<String, Object> resultMap = new HashMap<>();
        if (taskListByEnvName.size() == 0) {
            return new ResultMessage(ERROR_NO_DATA, null);
        } else {
            CodeUtil.taskStatusToString(taskListByEnvName);
            resultMap.put("taskList", taskListByEnvName);
            resultMap.put("totalCount", taskCount);
            return new ResultMessage(DATA_RETURN_SUCCESS, resultMap);
        }
    }

    @PostMapping("/updateTask")
    public ResultMessage updateTask(@RequestPart("task") String inputTask,
                                    @RequestPart("pyFiles") MultipartFile[] pyFiles,
                                    @RequestPart("inputFiles") MultipartFile[] inputFiles,
                                    @RequestPart("requirementsFile") MultipartFile file) throws IOException {
        try {
            Gson gson = new Gson();
            Task task = gson.fromJson(inputTask, Task.class);
            if (task.getTaskTitle() == null || task.getEnv().getEnvName() == null || task.getDeadTime() == null || task.getTaskKey() == null || task.getUserKey() == null || task.getGpuId() == null) {
                log.warn("传入的任务参数有误");
                return new ResultMessage(ERROR_PARAM, null);
            }
            taskService.updateTask(task);
            Task resultTask = taskService.getTaskByTaskKey(task.getTaskKey());
            resultTask.getEnv().setEnvName(CodeUtil.decodeEnvName(resultTask.getEnv().getEnvName()));
            if (resultTask != null) {
                FileUtil.uploadFile(file.getBytes(), resultTask.getRequirementsPath(), null);
                for (MultipartFile m : pyFiles) {
                    if (m.isEmpty()) {
                        log.warn("上传的py文件有空文件");
                    } else {
                        String filePath = CodeUtil.getPath(resultTask.getUserKey(), resultTask.getTaskKey(), resultTask.getEnv().getEnvName(), CodeUtil.PY_PATH);
                        String fileName = m.getOriginalFilename();
                        byte[] bytes = m.getBytes();
                        if (filePath != null)
                            FileUtil.uploadFile(bytes, filePath, fileName);
                        else
                            log.warn("py文件路径不正确");
                    }
                }
                for (MultipartFile m : inputFiles) {
                    if (m.isEmpty()) {
                        log.warn("上传的input文件有空文件");
                    } else {
                        String filePath = CodeUtil.getPath(resultTask.getUserKey(), resultTask.getTaskKey(), resultTask.getEnv().getEnvName(), CodeUtil.INPUT_PATH);
                        String fileName = m.getOriginalFilename();
                        byte[] bytes = m.getBytes();
                        if (filePath != null)
                            FileUtil.uploadFile(bytes, filePath, fileName);
                        else
                            log.warn("input文件路径不正确");
                    }
                }
                Boolean flag = taskService.pipInstall(resultTask);
                Env env = new Env();
                env.setEnvName(CodeUtil.encodeEnvName(resultTask.getEnv().getEnvName(), resultTask.getUserKey()));
                env.setUserKey(resultTask.getUserKey());
                String pipList;
                if (CommandUtil.isWindows()) {
                    pipList = CommandUtil.getPipList(env, WINDOWS);
                } else {
                    pipList = CommandUtil.getPipList(env, LINUX);
                }
                env.setHasList(pipList);
                envService.updateEnvHasList(env);
                if (flag) {
                    return new ResultMessage(UPDATE_TASK_SUCCESS, flag);
                } else
                    return new ResultMessage(FAIL_UPDATE_TASK, "报错详情见log");

            } else {
                return new ResultMessage(ERROR_PARAM, task);
            }
        } catch (Exception e) {
            log.warn("传入的任务参数有误");
            return new ResultMessage(ERROR_PARAM, null);
        }

    }

    @GetMapping("/insertQueue")
    public ResultMessage insertQueue(@RequestParam String taskKey, @RequestParam String gpuId) {
        Boolean flag = taskService.insertToQueue(taskKey, Integer.parseInt(gpuId));
        if (flag) {
            return new ResultMessage(QUEUE_TASK_SUCCESS, flag);
        } else {
            return new ResultMessage(FAIL_QUEUE_TASK, flag);
        }
    }

    @GetMapping("/isTaskExist")
    public ResultMessage isTaskExist(@RequestParam String userKey, @RequestParam String taskKey) {
        List<Task> taskListByUserKey = taskService.getTaskListByUserKey(userKey, null, 1, 100000);
        for (Task t : taskListByUserKey) {
            if (t.getTaskKey().equals(taskKey)) {
                return new ResultMessage(TASK_EXIT, true);
            }
        }
        return new ResultMessage(TASK_NOT_EXIT, false);
    }


}

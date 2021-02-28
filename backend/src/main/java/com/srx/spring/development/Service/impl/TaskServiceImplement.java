package com.srx.spring.development.Service.impl;

import com.srx.spring.development.Entities.Task;
import com.srx.spring.development.Entities.TaskQueue;
import com.srx.spring.development.Exception.CommandException;
import com.srx.spring.development.Exception.DatabaseException;
import com.srx.spring.development.Mapper.EnvMapper;
import com.srx.spring.development.Mapper.TaskMapper;
import com.srx.spring.development.Service.TaskService;
import com.srx.spring.development.util.CodeUtil;
import com.srx.spring.development.util.CommandUtil;
import com.srx.spring.development.util.FileUtil;
import com.srx.spring.development.util.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class TaskServiceImplement implements TaskService {


    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EnvMapper envMapper;

    private static PropertiesLoader loader = new PropertiesLoader("config.properties");
    private String windows_root_path = loader.getValue("windows.root.path");
    private String linux_root_path = loader.getValue("linux.root.path");


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Task insertTask(Task task) {
        String taskKey = CodeUtil.get_uuid();
        task.setTaskKey(taskKey);
        String envName = task.getEnv().getEnvName();
        if (CommandUtil.isWindows()) {
            Integer envId = envMapper.queryEnvIdByEnvName(CodeUtil.encodeEnvName(envName, task.getUserKey()));
            boolean taskFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_TASK, CommandUtil.WINDOWS);
            boolean pyFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_PY, CommandUtil.WINDOWS);
            boolean inputFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_INPUT, CommandUtil.WINDOWS);
            boolean outputFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_OUTPUT, CommandUtil.WINDOWS);
            if (taskFlag && pyFlag && inputFlag && outputFlag && envId != null) {
                String path = task.getUserKey() + "/" + task.getEnv().getEnvName() + "/" + task.getTaskKey();
                if (CommandUtil.isPathExists(path, CommandUtil.WINDOWS)) {
                    path += "/requirements.txt";
                    task.setRequirementsPath(windows_root_path + path);
                    Boolean flag1 = taskMapper.insertTask(task);
                    Integer taskId = task.getTaskId();
                    Boolean flag2 = taskMapper.insertEnvTask(envId, taskId, taskKey, task.getUserKey());
                    if (flag1 && flag2) {
                        return task;
                    } else {
                        return null;
                    }
                } else throw new CommandException("任务路径不存在");
            } else throw new CommandException("创建任务文件夹失败");
        } else {
            Integer envId = envMapper.queryEnvIdByEnvName(CodeUtil.encodeEnvName(envName, task.getUserKey()));
            boolean taskFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_TASK, CommandUtil.LINUX);
            boolean pyFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_PY, CommandUtil.LINUX);
            boolean inputFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_INPUT, CommandUtil.LINUX);
            boolean outputFlag = CommandUtil.mkdir(task.getUserKey(), envName, taskKey, CommandUtil.MKDIR_OUTPUT, CommandUtil.LINUX);
            if (taskFlag && pyFlag && inputFlag && outputFlag) {
                String path = task.getUserKey() + "/" + task.getEnv().getEnvName() + "/" + task.getTaskKey();
                if (CommandUtil.isPathExists(path, CommandUtil.LINUX)) {
                    path += "/requirements.txt";
                    task.setRequirementsPath(linux_root_path + path);
                    Boolean flag1 = taskMapper.insertTask(task);
                    Integer taskId = task.getTaskId();
                    Boolean flag2 = taskMapper.insertEnvTask(envId, taskId, taskKey, task.getUserKey());
                    if (flag1 && flag2) {
                        return task;
                    } else
                        return null;
                } else throw new CommandException("任务路径不存在");
            } else throw new CommandException("创建任务文件夹失败");
        }
    }

    @Override
    public Boolean pipInstall(Task task) {
        if (CommandUtil.isWindows()) {
            String requirementsPath = task.getRequirementsPath();
            if (FileUtil.isRequirements(requirementsPath)) {
                Boolean flag = CommandUtil.pipInstallRequirements(task.getUserKey(), task.getEnv().getEnvName(), requirementsPath, CommandUtil.WINDOWS);
                return flag;
            } else {
                log.warn("任务:" + task.getTaskKey() + "的requirements文件错误");
                return false;
            }
        } else {
            String requirementsPath = task.getRequirementsPath();
            if (FileUtil.isRequirements(requirementsPath)) {
                Boolean flag = CommandUtil.pipInstallRequirements(task.getUserKey(), task.getEnv().getEnvName(), requirementsPath, CommandUtil.LINUX);
                return flag;
            } else {
                log.warn("任务:" + task.getTaskKey() + "的requirements文件错误");
                return false;
            }
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Task getFirstTask(String gpuId) {
        TaskQueue firstTask = taskMapper.queryFirstTask(gpuId);
        if (firstTask != null) {
            String taskKey = firstTask.getTaskKey();
            Task task = taskMapper.queryTaskByTaskKey(taskKey);
            Integer integer = taskMapper.updateTaskStatusToDoing(taskKey);
            Boolean flag = taskMapper.updateStatusToPop(taskKey);
            if (integer != 0 && flag)
                return task;
            else throw new DatabaseException("任务弹出队列失败！");
        } else {

            return null;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean insertToQueue(String taskKey, Integer gupId) {
        TaskQueue taskQueue = new TaskQueue();
        taskQueue.setGpuId(gupId.toString());
        taskQueue.setTaskKey(taskKey);
        Boolean flag = taskMapper.insertTaskQueue(taskQueue);
        Integer integer = taskMapper.updateTaskStatusToQueue(taskKey);
        if (integer != 0 && flag)
            return true;
        else throw new DatabaseException("任务插入队列失败");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer updateTask(Task task) {
        Integer status = taskMapper.queryTaskStatus(task.getTaskKey());
        if (status == null) {
            log.warn("已加入队列、正在运行、已经删除的任务无法更新任务信息,又或没有该环境");
            return 0;
        } else if (status == 1) {
            Integer oldEnvId = taskMapper.queryTaskByTaskKey(task.getTaskKey()).getEnv().getEnvId();
            String envName = task.getEnv().getEnvName();
            Integer newEnvId = envMapper.queryEnvIdByEnvName(CodeUtil.encodeEnvName(envName, task.getUserKey()));
            if (oldEnvId != newEnvId && newEnvId != 0) {
                taskMapper.updateEnvId(task.getTaskKey(), newEnvId);
            }
            Integer result = taskMapper.updateAll(task);
            return result;
        } else {
            return 0;
        }
    }

    @Override
    public Boolean updateTaskStatusToComplete(String taskKey) {
        Integer integer = taskMapper.updateTaskStatusToComplete(taskKey);
        if (integer != 0 && integer != null)
            return false;
        else
            return true;
    }

    @Override
    public Boolean updateFinalLiveTime(String taskKey) {
        String join_time = taskMapper.queryTaskJoinTime(taskKey);
        Task task = taskMapper.queryTaskByTaskKey(taskKey);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(join_time);
            long joinTime = date.getTime();
            long now = new Date().getTime();
            long liveTime = now - joinTime;
            task.setFinalLiveTime(String.valueOf(liveTime / 1000));
            Integer integer = taskMapper.updateFinalLiveTime(task);
            if (integer != 0)
                return true;
            else return false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Task> getTaskListByUserKey(String userKey, String status, Integer currentPage, Integer pageSize) {
        int begin = (currentPage - 1) * pageSize;
        if (status==null||status.equals(""))
            return taskMapper.queryTaskListByUserKey(userKey, null, begin, pageSize);
        else
            return taskMapper.queryTaskListByUserKey(userKey, status, begin, pageSize);

    }

    @Override
    public Task getTaskByTaskKey(String taskKey) {
        Task task = taskMapper.queryTaskByTaskKey(taskKey);
        if (task != null)
            task.getEnv().setTaskList(taskMapper.queryTaskListByEnvName(task.getEnv().getEnvName(), null, null, null));
        return task;
    }

    @Override
    public List<Task> getTaskListByEnvName(String envName, String status, Integer currentPage, Integer pageSize) {
        int begin = (currentPage - 1) * pageSize;
        if (status.equals(""))
            return taskMapper.queryTaskListByEnvName(envName, null, begin, pageSize);
        else return taskMapper.queryTaskListByEnvName(envName, status, begin, pageSize);
    }

    @Override
    public Integer getTaskCountByEnvName(String envName, String status) {
        if (status.equals(""))
            return taskMapper.queryTaskCountByEnvName(envName, null);
        else
            return taskMapper.queryTaskCountByEnvName(envName, status);
    }

    @Override
    public Integer getTaskCountByUserKey(String userKey, String status) {
        if (status.equals(""))
            return taskMapper.queryTaskCountByUserKey(userKey, null);
        else
            return taskMapper.queryTaskCountByUserKey(userKey, status);
    }


}

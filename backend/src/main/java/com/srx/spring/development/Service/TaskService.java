package com.srx.spring.development.Service;

import com.srx.spring.development.Entities.Task;

import java.util.List;


public interface TaskService {

    /**
     * 插入一个task，这里的插入仅仅是把数据插入到数据库中，并创建好相应的文件夹，不负责队列的相关事情
     *
     * @param task
     * @return
     */
    Task insertTask(Task task);

    /**
     * 根据服务器中的每个任务文件夹下的requirements.txt文件进行库的安装
     *
     * @param task
     * @return
     */
    Boolean pipInstall(Task task);


    /**
     * 获取队列中的第一个,如果返回值为null，证明该队列是空的
     *
     * @return
     */
    Task getFirstTask(String gpuId);

    /**
     * 向队列中插入一个task，即向task_queue表中插入一个记录
     *
     * @param taskKey
     * @param gupId
     * @return
     */
    Boolean insertToQueue(String taskKey, Integer gupId);

    /**
     * 更改一个任务的信息。这个任务一定是状态为1的，也就是说一旦任务处于
     * 插入队列，完成，正在进行这三个状态中的任意一种，都无法再更改任务的信息
     *
     * @param task
     * @return
     */
    Integer updateTask(Task task);

    Boolean updateTaskStatusToComplete(String taskKey);

    /**
     * 更改最后存活时间
     *
     * @param taskKey
     * @return
     */
    Boolean updateFinalLiveTime(String taskKey);

    /**
     * 根据用户key查询用户所有的任务
     *
     * @param userKey
     * @return
     */
    List<Task> getTaskListByUserKey(String userKey, String status, Integer currentPage, Integer pageSize);

    /**
     * 根据任务key查询单个任务
     *
     * @param taskKey
     * @return
     */
    Task getTaskByTaskKey(String taskKey);

    /**
     * 根据环境名称，查询一个环境下的所有任务
     *
     * @param envName
     * @return
     */
    List<Task> getTaskListByEnvName(String envName, String status, Integer currentPage, Integer pageSize);

    /**
     * 查询环境下所有任务
     *
     * @param envName
     * @param status
     * @return
     */
    Integer getTaskCountByEnvName(String envName, String status);


    /**
     * 查询环境下所有任务
     *
     * @param userKey
     * @param status
     * @return
     */
    Integer getTaskCountByUserKey(String userKey, String status);
}

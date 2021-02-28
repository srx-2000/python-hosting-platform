package com.srx.spring.development.Mapper;


import com.srx.spring.development.Entities.Task;
import com.srx.spring.development.Entities.TaskQueue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskMapper {

    /**
     * 插入一个任务
     *
     * @param task
     * @return
     */
    Boolean insertTask(Task task);

    /**
     * 插入一个env_task表的数据
     *
     * @param envId
     * @param taskId
     * @param taskKey
     * @param userKey
     * @return
     */
    Boolean insertEnvTask(@Param("envId") Integer envId, @Param("taskId") Integer taskId, @Param("taskKey") String taskKey, @Param("userKey") String userKey);

    /**
     * 插入一个任务到队列中，即在一个任务队列表中插入一个任务
     *
     * @param taskQueue
     * @return
     */
    Boolean insertTaskQueue(TaskQueue taskQueue);

    /**
     * 将一个任务从任务队列中弹出，即更改task_queue的状态为0
     *
     * @param taskKey
     * @return
     */
    Boolean updateStatusToPop(@Param("taskKey") String taskKey);

    /**
     * 获取数据库中状态为1的，且时间升序排序下的第一个task元素，即取出队列中的第一个元素
     * service层在调用该方法时同样需要配合上面的弹出使用，
     *
     * @return
     */
    TaskQueue queryFirstTask(@Param("gpuId") String gpuId);


    /**
     * 将任务的状态改为已完成
     *
     * @param task
     * @return
     */
    Integer updateTaskStatus(Task task);

    /**
     * 将任务的状态改为未完成
     *
     * @param taskKey
     * @return
     */
    Integer updateTaskStatusToNotComplete(@Param("taskKey") String taskKey);

    /**
     * 将任务的状态改为正在进行
     *
     * @param taskKey
     * @return
     */
    Integer updateTaskStatusToDoing(@Param("taskKey") String taskKey);

    /**
     * 将任务的状态改为加入队列
     *
     * @param taskKey
     * @return
     */
    Integer updateTaskStatusToQueue(@Param("taskKey") String taskKey);

    /**
     * 将任务的状态改为完成
     *
     * @param taskKey
     * @return
     */
    Integer updateTaskStatusToComplete(@Param("taskKey") String taskKey);

    /**
     * 更改任务的user_key，用来与user中的更改做事务
     *
     * @param task
     * @return
     */
    Integer updateTaskUserKey(Task task);

    /**
     * 更改requirements.txt文件的路径
     *
     * @param task
     * @return
     */
    Integer updateRequirements(Task task);

    /**
     * 更改用户规定的任务结束时间
     *
     * @param task
     * @return
     */
    Integer updateDeadTime(Task task);

    /**
     * 更改任务名称
     *
     * @param task
     * @return
     */
    Integer updateTaskTitle(Task task);

    /**
     * 更改任务描述
     *
     * @param task
     * @return
     */
    Integer updateTaskDescription(Task task);

    /**
     * 修改任务存活时间
     *
     * @param task
     * @return
     */
    Integer updateFinalLiveTime(Task task);

    /**
     * 更改传进来的一切，如果属性不为空的话
     *
     * @param task
     * @return
     */
    Integer updateAll(Task task);

    /**
     * 根据传入的id和key更新env_task表中的数据
     *
     * @param taskKey
     * @param envId
     * @return
     */
    Integer updateEnvId(@Param("taskKey") String taskKey, @Param("envId") Integer envId);


    /**
     * 根据envName查询任务列表
     *
     * @param envName
     * @return
     */
    List<Task> queryTaskListByEnvName(@Param("envName") String envName, @Param("status") String status, @Param("begin") Integer begin, @Param("pageSize") Integer pageSize);

    /**
     * 根据task_key 查询某一个任务的所有信息
     *
     * @param taskKey
     * @return
     */
    Task queryTaskByTaskKey(@Param("taskKey") String taskKey);

    /**
     * 根据User_key查询任务列表
     *
     * @param userKey
     * @return
     */
    List<Task> queryTaskListByUserKey(@Param("userKey") String userKey, @Param("status") String status, @Param("begin") Integer begin, @Param("pageSize") Integer pageSize);

    /**
     * 查询任务状态
     *
     * @param taskKey
     * @return
     */

    Integer queryTaskStatus(@Param("taskKey") String taskKey);

    /**
     * 查询任务加入队列的时间，用来计算最后任务的存活时间
     *
     * @param taskKey
     * @return
     */
    String queryTaskJoinTime(@Param("taskKey") String taskKey);

    /**
     * 查询一个用户的所有任务数量
     * @param userKey
     * @param status
     * @return
     */
    Integer queryTaskCountByUserKey(@Param("userKey") String userKey, @Param("status") String status);

    /**
     * 查询一个环境下下的所有任务数量
     * @param envName
     * @param status
     * @return
     */
    Integer queryTaskCountByEnvName(@Param("envName") String envName, @Param("status") String status);


}

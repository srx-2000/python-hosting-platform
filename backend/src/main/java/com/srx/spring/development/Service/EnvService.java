package com.srx.spring.development.Service;

import com.srx.spring.development.Entities.Env;
import com.srx.spring.development.Entities.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EnvService {

    /**
     * 通过id找到环境名
     *
     * @param envName
     * @return
     */
    Integer getEnvIdByEnvName(String envName);

    /**
     * 通过name找到id
     *
     * @param envId
     * @return
     */
    String getEnvNameByEnvId(Integer envId);

    /**
     * 插入一个数据，并在服务器上创建相应的环境，但是不运行
     *
     * @param env
     * @return
     */
    Boolean insertEnv(Env env);

    /**
     * 通过环境名更改环境状态到销毁，返回的是更新的id，同时更新finalLiveTime
     *
     * @param env
     * @return
     */
    Integer updateEnvStatusToDestroy(Env env);

    /**
     * 更新环境的安装列表，在每次用户保存一个任务的时候运行pip install
     * 更新安装完成后，更新数据库中env的haslist
     * 这个服务需要两个命令行支持
     *
     * @param env
     * @return
     */
    Integer updateEnvHasList(Env env);

    /**
     * 该方法主要是用来获取一个环境下的所有任务的，而在mapper中是获取到该环境的所有信息
     * ，需要重新取一下task
     *
     * @param envName
     * @return
     */
    List<Task> queryTaskListByEnvName(String envName);

    /**
     * 查找一个用户下的所有环境
     *
     * @param userKey
     * @return
     */
    List<Env> queryEnvListByUserKey(String userKey, Integer currentPage, Integer pageSize);

    /**
     * 查询一个用户的所有环境的数量
     * @param userKey
     * @return
     */
    Integer queryCountByUserKey(String userKey);

    /**
     * 模糊查询
     * @param userKey
     * @param queryString
     * @return
     */
    List<Env> queryEnvListByString(String userKey, String queryString, Integer currentPage, Integer pageSize);


    /**
     * 模糊查询的总数量
     * @param userKey
     * @param string
     * @return
     */
    Integer queryCountByString(@Param("userKey") String userKey,@Param("string") String string);

}

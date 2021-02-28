package com.srx.spring.development.Mapper;

import com.srx.spring.development.Entities.Env;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvMapper {

    /**
     * 插入一个环境
     * @param env
     * @return
     */
    Boolean insertEnv(Env env);
    /**
     * 根据环境名将环境的状态更新为正在运行
     * @param envName
     * @return
     */
    Integer updateEnvStatusToDestroy(String envName);

    /**
     * 更新has_list列表
     * @param env
     * @return
     */
    Integer updateEnvHasList(Env env);

    /**
     * 更新最后存活时间，主要是在service层的销毁方法中使用
     * @param env
     * @return
     */
    Integer updateFinalLiveTime(Env env);

    /**
     * 根据envName查询任务列表
     * @param envName
     * @return
     */
    Env queryTaskListByEnvName(String envName);

    /**
     * 根据用户key查询该用户的所有环境
     * @param userKey
     * @return
     */
    List<Env> queryEnvListByUserKey(@Param("userKey") String userKey, @Param(value = "begin") Integer begin, @Param(value = "pageSize") Integer pageSize);

    /**
     * 根据环境名查询环境id，但是这个环境名是env_name+user_key,
     * 如果返回值为null，证明该环境名还未被创建过。
     * @param envName
     * @return
     */
    Integer queryEnvIdByEnvName(String envName);

    /**
     * 根据环境id查询环境名
     * @param envId
     * @return
     */
    String queryEnvNameByEnvId(Integer envId);

    /**
     * 更改user_key,待定是否使用
     * 如果使用，那么逻辑应该如下：
     * 首先通过原有user_key找到所有的环境，然后通过这个环境列表中的
     * 每一个环境id更改env_task这个表中的user_key
     * @param env
     * @return
     */
    Boolean updateUserKey(Env env);

    /**
     * 获取用户环境数量
     * @param userKey
     * @return
     */
    Integer queryCountByUserKey(String userKey);

    /**
     * 模糊查询
     * @param string
     * @param userKey
     * @return
     */
    List<Env> queryEnvListByString(@Param("string") String string,@Param("userKey") String userKey, @Param(value = "begin") Integer begin, @Param(value = "pageSize") Integer pageSize);

    /**
     * 模糊查询的总数量
     * @param userKey
     * @param string
     * @return
     */
    Integer queryCountByString(@Param("userKey") String userKey,@Param("string") String string);

}

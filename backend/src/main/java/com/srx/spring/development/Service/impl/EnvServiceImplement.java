package com.srx.spring.development.Service.impl;


import com.srx.spring.development.Entities.Env;
import com.srx.spring.development.Entities.Task;
import com.srx.spring.development.Mapper.EnvMapper;
import com.srx.spring.development.Service.EnvService;
import com.srx.spring.development.util.CodeUtil;
import com.srx.spring.development.util.CommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EnvServiceImplement implements EnvService {

    @Autowired
    private EnvMapper envMapper;

    @Override
    public Integer getEnvIdByEnvName(String envName) {
        return envMapper.queryEnvIdByEnvName(envName);
    }

    @Override
    public String getEnvNameByEnvId(Integer envId) {
        return envMapper.queryEnvNameByEnvId(envId);
    }

    //这里传入的env的名字必须带有uuid
    @Override
    public Boolean insertEnv(Env env) {
        String envName = env.getEnvName();
        String[] nameList = envName.split("@@");
        env.setUserKey(nameList[1]);
        if (CommandUtil.isWindows()) {
            boolean flag = CommandUtil.mkdir(env.getUserKey(), nameList[0], null, CommandUtil.MKDIR_ENV, CommandUtil.WINDOWS);
            Boolean flag1 = false;
            if (flag) {
                String list = CommandUtil.getPipList(env, CommandUtil.WINDOWS);
                env.setHasList(list);
                flag1 = envMapper.insertEnv(env);
            }
            return flag && flag1;
        } else {
            boolean flag = CommandUtil.mkdir(env.getUserKey(), nameList[0], null, CommandUtil.MKDIR_ENV, CommandUtil.LINUX);
            Boolean flag1 = false;
            if (flag) {
                String list = CommandUtil.getPipList(env, CommandUtil.LINUX);
                env.setHasList(list);
                flag1 = envMapper.insertEnv(env);
            }
            return flag && flag1;
        }
    }

    @Override
    public Integer updateEnvStatusToDestroy(Env env) {
        if (env.getEnvName() != null && env.getFinalLiveTime() != null) {
            Integer integer1 = envMapper.updateEnvStatusToDestroy(env.getEnvName());
            Integer integer = envMapper.updateFinalLiveTime(env);
            if (integer == integer1)
                return integer;
            else
                return 0;
        }
        return 0;
    }

    @Override
    public Integer updateEnvHasList(Env env) {
        if (CommandUtil.isWindows()) {
            String pipList = CommandUtil.getPipList(env, CommandUtil.WINDOWS);
            System.out.println("环境信息："+pipList);
            env.setHasList(pipList);
            Integer integer = envMapper.updateEnvHasList(env);
            if (integer.equals(0))
                log.error("环境" + env.getEnvName() + "更新list失败");
            return integer;
        } else {
            String pipList = CommandUtil.getPipList(env, CommandUtil.LINUX);
            env.setHasList(pipList);
            Integer integer = envMapper.updateEnvHasList(env);
            if (integer.equals(0))
                log.error("环境" + env.getEnvName() + "更新list失败");
            return integer;
        }
    }

    @Override
    public List<Task> queryTaskListByEnvName(String envName) {
        if (envName != null) {
            Env env = envMapper.queryTaskListByEnvName(envName);
            return env.getTaskList();
        }
        return null;
    }

    @Override
    public List<Env> queryEnvListByUserKey(String userKey, Integer currentPage, Integer pageSize) {
        int begin = (currentPage - 1) * pageSize;
        if (userKey != null) {
            List<Env> envs = envMapper.queryEnvListByUserKey(userKey, begin, pageSize);
            for (Env env : envs) {
                env.setEnvName(CodeUtil.decodeEnvName(env.getEnvName()));
                env.setUserKey(userKey);
                env.setHasList(CodeUtil.decodePip(env.getHasList()));
            }
            return envs;
        }
        return null;
    }

    @Override
    public Integer queryCountByUserKey(String userKey) {
        if (userKey != null) {
            return envMapper.queryCountByUserKey(userKey);
        }
        return 0;
    }

    @Override
    public List<Env> queryEnvListByString(String userKey, String queryString, Integer currentPage, Integer pageSize) {
        int begin = (currentPage - 1) * pageSize;
        if (userKey != null && queryString != "" && queryString != null) {
            List<Env> envs = envMapper.queryEnvListByUserKey(userKey, begin, pageSize);
            List<Env> resultList=new ArrayList<>();
            for (Env env :envs) {
                String name = CodeUtil.decodeEnvName(env.getEnvName());
                if (name.contains(queryString)){
                    env.setEnvName(CodeUtil.decodeEnvName(env.getEnvName()));
                    env.setUserKey(userKey);
                    env.setHasList(CodeUtil.decodePip(env.getHasList()));
                    resultList.add(env);
                }else continue;
            }
            return resultList;
        }
        return null;
    }

    @Override
    public Integer queryCountByString(String userKey, String string) {
        if (userKey != null && string != null && string != "") {
            return envMapper.queryCountByString(userKey, string);
        }else
            return 0;
    }

}

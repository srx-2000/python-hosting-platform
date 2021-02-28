package com.srx.spring.development.Controller;

import com.srx.spring.development.Entities.DTO.ResultMessage;
import com.srx.spring.development.Entities.Env;
import com.srx.spring.development.Service.EnvService;
import com.srx.spring.development.util.CodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.srx.spring.development.Enum.ResultCode.*;

@RestController
@Slf4j
public class EnvController {

    @Autowired
    private EnvService envService;

    @PostMapping("/insertEnv")
    public ResultMessage insertEnv(@RequestBody Env env) {
        if (env != null) {
            String name = env.getEnvName();
            String userKey = env.getUserKey();
            env.setEnvName(CodeUtil.encodeEnvName(name, userKey));
            Boolean flag = envService.insertEnv(env);
            if (flag) {
                return new ResultMessage(INSERT_ENV_SUCCESS, flag);
            } else {
                return new ResultMessage(FAIL_INSERT_ENV, flag);
            }
        } else {
            return new ResultMessage(ERROR_PARAM, null);
        }
    }

//    @GetMapping("/getTaskListByEnvName")
//    public ResultMessage queryTaskListByEnvName(@RequestParam String envName){
//        List<Task> tasks = envService.queryTaskListByEnvName(envName);
//        if (tasks.size()!=0){
//            return new ResultMessage(DATA_RETURN_SUCCESS,tasks);
//        }else
//            return new ResultMessage(ERROR_NO_DATA,null);
//    }

    @GetMapping("/getEnvListByUserKey")
    public ResultMessage queryEnvListByUserKey(@RequestParam String userKey, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        List<Env> envs = envService.queryEnvListByUserKey(userKey, currentPage, pageSize);
        Map<String,Object> resultMap=new HashMap();
        if (envs.size() != 0) {
            resultMap.put("envList",envs);
            resultMap.put("totalCount",envService.queryCountByUserKey(userKey));
            return new ResultMessage(DATA_RETURN_SUCCESS, resultMap);

        } else
            return new ResultMessage(ERROR_NO_DATA, null);
    }

    @GetMapping("/isEnvExist")
    public ResultMessage queryEnvIsExist(@RequestParam String envName, @RequestParam String userKey) {
        String name = CodeUtil.encodeEnvName(envName, userKey);
        Integer envId = envService.getEnvIdByEnvName(name);
        if (envId == null) {
            return new ResultMessage(ENV_NOT_EXIT, false);
        } else {
            return new ResultMessage(ENV_EXIT, true);
        }
    }

    @GetMapping("/getEnvListByString")
    public ResultMessage getEnvListByString(@RequestParam String userKey, @RequestParam String queryString, @RequestParam Integer currentPage, @RequestParam Integer pageSize) {
        List<Env> envs = envService.queryEnvListByString(userKey, queryString, currentPage, pageSize);
        Map<String,Object> resultMap=new HashMap();
        if (envs.size() != 0) {
            resultMap.put("envList",envs);
            resultMap.put("totalCount",envService.queryCountByString(userKey,queryString));
            return new ResultMessage(DATA_RETURN_SUCCESS, resultMap);
        } else
            return new ResultMessage(ERROR_NO_DATA, null);
    }


}

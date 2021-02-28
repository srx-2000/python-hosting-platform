package com.srx.spring.development;

import com.srx.spring.development.Entities.TaskQueue;
import com.srx.spring.development.Entities.*;
import com.srx.spring.development.Mapper.EnvMapper;
import com.srx.spring.development.Mapper.GpuMapper;
import com.srx.spring.development.Mapper.TaskMapper;
import com.srx.spring.development.Service.EnvService;
import com.srx.spring.development.Service.GpuService;
import com.srx.spring.development.Service.TaskService;
import com.srx.spring.development.Service.UserService;
import com.srx.spring.development.util.CodeUtil;
import com.srx.spring.development.util.CommandUtil;
import com.srx.spring.development.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootTest
class DevelopmentApplicationTests {

    String userKey = "c24267a8-ecd5-4834-95be-6dcc683b4ac5";
    String taskKey = "9de47aca-3a91-465a-9181-cac586aa4f2a";
    @Autowired
    private GpuService gpuService;
    @Autowired
    private EnvService envService;
    @Autowired
    private UserService userService;
    @Autowired
    private EnvMapper envMapper;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private GpuMapper gpuMapper;

    @Test
    void contextLoads() {
    }

    @Test
    public void getGpuInfo() {
//        service.setStatusLine(10);
//        Boolean aBoolean = service.initGpu();
//        Timer timer = service.startMonitor(1000 * 60);
//        service.stopMonitor(timer);
//        System.out.println(timer);
        ArrayList<Task> taskList = new ArrayList<>();
//        taskList.add();
        String uuid = "7b12780b-f4b5-46d3-9268-c49586969afd";
        Env env = new Env(1, null, null, null, "0", "venv@@" + uuid, uuid, taskList);
//        userService.register(new User(null,"srx","1601684622@qq.com","srx62600",uuid,"bieer","13718322331"));
        envService.insertEnv(env);
//        System.out.println(aBoolean);
//        List<GPUInfo> gpuInfos = service.getGpuInfos(1);
//        System.out.println(gpuInfos);
    }

    @Test
    public void test2() {
        Boolean register = userService.register(new User(1, "srx", "1601684622@qq.com", "srx62600", "1111", "beier", "13718322331"));
        System.out.println(register);
    }

    @Test
    public void test3() {
        String userKey = "ba9bf477-3813-4578-a9cd-2d1af26c3902";
        String taskKey = "d7f71aaa-a496-47b4-8d43-7edeac6e4bdc";
        String envName = "venv";

        boolean mkdir = CommandUtil.mkdir(userKey, envName, taskKey, CommandUtil.MKDIR_USER, CommandUtil.WINDOWS);
        boolean mkdir1 = CommandUtil.mkdir(userKey, envName, taskKey, CommandUtil.MKDIR_ENV, CommandUtil.WINDOWS);
        boolean mkdir2 = CommandUtil.mkdir(userKey, envName, taskKey, CommandUtil.MKDIR_TASK, CommandUtil.WINDOWS);
        boolean mkdir3 = CommandUtil.mkdir(userKey, envName, taskKey, CommandUtil.MKDIR_PY, CommandUtil.WINDOWS);
        boolean mkdir4 = CommandUtil.mkdir(userKey, envName, taskKey, CommandUtil.MKDIR_INPUT, CommandUtil.WINDOWS);
        boolean mkdir5 = CommandUtil.mkdir(userKey, envName, taskKey, CommandUtil.MKDIR_OUTPUT, CommandUtil.WINDOWS);
    }

    @Test
    public void test4() {
//        Boolean venv = envMapper.insertEnv(new Env(null, null, null, "pandas\njieba", null, "venv", null, null));
//        System.out.println(venv);
//        Integer integer = envMapper.updateEnvStatusToDestroy("123");
//        System.out.println(integer);
    }

    @Test
    public void test5() {
        String uuid = "ba9bf477-3813-4578-a9cd-2d1af26c3902";
        String name = "venv";
        String envName = name + "@@" + uuid;
        String pipList = CommandUtil.getPipList(new Env(null, null, null, null, null, envName, uuid, null), CommandUtil.WINDOWS);
        System.out.println(pipList);
    }

    @Test
    public void test6() {
        String uuid = "7b12780b-f4b5-46d3-9268-c49586969afd";
//        Env env = envMapper.queryTaskListByEnvName("venv" + "@@" + uuid);
//        List<Task> venv = (List<Task>) env;
        List<Task> tasks = envService.queryTaskListByEnvName("venv" + "@@" + uuid);
        System.out.println(tasks);
    }

    @Test
    public void test7() {
        String uuid = "7b12780b-f4b5-46d3-9268-c49586969afd";
//        List<Env> envs = envMapper.queryEnvListByUserKey(uuid);
//        System.out.println(envs);
    }

    @Test
    public void test8() {
        String uuid = "7b12780b-f4b5-46d3-9268-c49586969afd";
        Integer integer = envMapper.updateFinalLiveTime(new Env(null, null, null, null, "1000", "venv" + "@@" + uuid, uuid, null));
        System.out.println(integer);
    }

    @Test
    public void test() {
        String task_key = "ba9bf477-3813-4578-a9cd-2d1af26c3902";
//        Task task = taskMapper.queryTaskByTaskKey(task_key);
//        System.out.println(task);
//        Integer integer = taskMapper.updateAll(new Task(null, "task1", null, task_key, null, null, null, null, null, null, null));
//        System.out.println(integer);
    }
//    @Test
//    public void test9(){
//        TaskServiceImplement taskServiceImplement=new TaskServiceImplement();
//        Boolean taskQueue = taskServiceImplement.createTaskQueue(3);
//        if (taskQueue){
//            System.out.println(taskServiceImplement.queueList);
//        }else{
//            System.out.println(taskQueue);
//        }
//    }

    @Test
    public void test9() {
        Env env = new Env();
        String envName = "venv";
        String name = CodeUtil.encodeEnvName(envName, userKey);
        env.setEnvName(name);
        env.setUserKey(userKey);
        Boolean aBoolean = envService.insertEnv(env);
        System.out.println(aBoolean);
    }

    @Test
    public void test10() {
        Task task = new Task();
        Env env = new Env();
        String userKey = "518d4e3f-9db0-4b0a-9a32-f9b49392dce5";
        String envName = "venv";
        String taskTitle = "gpu机器学习任务1";
        String deadTime = "36000";
        String taskDescription = "使用service层的方法插入的一个task";
        String gpuId = "0";
        env.setUserKey(userKey);
        env.setEnvName(envName);
        task.setTaskKey(taskKey);
        task.setUserKey(userKey);
        task.setTaskTitle(taskTitle);
        task.setDeadTime(deadTime);
        task.setTaskDescription(taskDescription);
        task.setGpuId(gpuId);
        task.setEnv(env);
//        Boolean aBoolean = taskService.insertTask(task);
//        System.out.println(aBoolean);
    }

    @Test
    public void test11() {
        String path = "/518d4e3f-9db0-4b0a-9a32-f9b49392dce5/venv";
        Boolean pathExists = CommandUtil.isPathExists(path, CommandUtil.WINDOWS);
        System.out.println(pathExists);
    }


    @Test
    public void test12() {
        Boolean requirements = FileUtil.isRequirements("D:\\upload\\requirements.txt");
//        System.out.println();
    }

    @Test
    public void test13() {
        gpuService.setStatusLine(20);
        Boolean aBoolean = gpuService.initGpu();
        System.out.println(aBoolean);
    }

    @Test
    public void test14() {
        List<GPUInfo> gpuInfos = CommandUtil.getGpuInfos(1);
        System.out.println(gpuInfos);
    }

    @Test
    public void test15() {
        String taskKey = "4de762a4-9976-420a-90f6-731d35f31e5b";
        Boolean aBoolean = taskService.insertToQueue(taskKey, 0);
        System.out.println(aBoolean);
    }

    @Test
    public void test16() throws ParseException {
        String taskKey = "4de762a4-9976-420a-90f6-731d35f31e5b";
//        Task task = taskMapper.queryTaskByTaskKey(taskKey);
        String str = taskMapper.queryTaskJoinTime(taskKey);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(str);
        long ts = date.getTime();
        long now = new Date().getTime();
        System.out.println(ts);
        System.out.println(now);
        System.out.println(now - ts);
        System.out.println(str);
    }

    @Test
    public void test17() {
        TaskQueue taskQueue = taskMapper.queryFirstTask("0");
        System.out.println(taskQueue);
    }

    @Test
    public void test18() {
        String taskKey = "4de762a4-9976-420a-90f6-731d35f31e5b";
        Task taskByTaskKey = taskService.getTaskByTaskKey(taskKey);
        System.out.println(taskByTaskKey);
    }

//    @Test
//    public void test19() {
//        String envName = "venv@@518d4e3f-9db0-4b0a-9a32-f9b49392dce5";
//        System.out.println(taskMapper.queryTaskListByEnvName(envName));
//    }

//    @Test
//    public void test20() {
//        List<Task> tasks = taskMapper.queryTaskListByUserKey("518d4e3f-9db0-4b0a-9a32-f9b49392dce5");
//        System.out.println(tasks);
//    }

    @Test
    public void test21() {
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.WINDOWS_ROOT_PATH));
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.USER_PATH));
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.ENV_PATH));
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.TASK_PATH));
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.PY_PATH));
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.INPUT_PATH));
        System.out.println(CodeUtil.getPath(userKey, taskKey, "venv", CodeUtil.OUTPUT_PATH));
    }

    @Test
    public void test22() {
        List<String> pyList = new ArrayList<>();
        pyList.add("test.py");
        pyList.add("testMain.py");
        for (String s : pyList) {
            if (s.contains("Main")) {
                String venv = CommandUtil.runMain(userKey, taskKey, "venv", s, CommandUtil.WINDOWS);
                System.out.println(venv);
            } else continue;
        }
    }

    @Test
    public void test23() {
        String s = "1Mian";
        System.out.println(s.contains("main"));
    }

    @Test
    public void test24() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<GPUInfo> gpuInfos = CommandUtil.getGpuInfos(20);
                for (GPUInfo gpuInfo : gpuInfos) {
                    Integer integer = gpuMapper.updateGpu(gpuInfo);
                    System.out.println(integer);
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @Test
    public void test25() {
        gpuService.setStatusLine(20);
        gpuService.run();
        System.out.println();
    }

    @Test
    public void test26() {
        Boolean venv = CommandUtil.pipInstallRequirements("9091bd8a-1657-4fa8-82fd-da1c64c35084", "test", "D:/test/9091bd8a-1657-4fa8-82fd-da1c64c35084/venv/218dc9f2-be38-41d3-81e7-5cf496b331ba/requirements.txt", CommandUtil.WINDOWS);
        System.out.println(venv);
    }

    @Test
    public void test27() {
        Env env = new Env();
        String userKey = "9091bd8a-1657-4fa8-82fd-da1c64c35084";
        env.setUserKey(userKey);
        String envName = "test";
        String s = CodeUtil.encodeEnvName(envName, userKey);
        env.setEnvName(s);
        Integer integer = envService.updateEnvHasList(env);
        System.out.println(integer);
    }

    @Test
    public void test28() {
        gpuService.run();
    }

    @Test
    public void test29() {
        //需要压缩文件地址
        String path = "D:\\test\\9091bd8a-1657-4fa8-82fd-da1c64c35084\\venv\\8b1bcbda-99ef-417e-8e62-7af7b441aa13\\output\\";
        String name1 = "markdown快捷键.png";
        String name2 = "out.txt";
        String name3 = "file.zip";
        List<File> versionZip = new ArrayList<>();
        versionZip.add(new File(path + name1));
        versionZip.add(new File(path + name2));
        String fileZip = path + name3;
        //压缩文件地址以及文件名
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(fileZip));
            //调用压缩方法
            FileUtil.toZip(versionZip, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

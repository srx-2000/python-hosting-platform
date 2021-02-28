package com.srx.spring.development.Service;

import com.srx.spring.development.Entities.GPUInfo;

import java.util.List;

public interface GpuService {
    /**
     * 初始化gpu表，用来系统自检并更新gpu表，该方法应设置一个刷新按钮
     *
     * @return
     */
    Boolean initGpu();

    void setStatusLine(Integer statusLine);

    /**
     * 获取所有gpu列表，该方法是从数据库中读取，对外暴露，因为是从数据库中获取，所以不会执行命令行操作
     * 该方法不用设置计时器，因为该方法只有用户在点击查看gpu页面之后才会调用一次。或者可以给用户添加一个刷新按钮
     *
     * @return
     */
    List<GPUInfo> getGpuListFromDatabase();

    /**
     * 跑
     */
    void run();

    /**
     * 定时方法，每隔1小时插入一个
     * 插入一个状态点
     *
     * @return
     */
    Boolean insertGpuPoint();

    /**
     * 查询与现在最接近的12的状态点
     * @return
     */
    List<String> getGpuStatusPointList(String gpuName,String gpuId);

//    /**
//     * 开启计时器，并每隔period更新一次数据库，并不断监测gpu利用率，一旦gpu的利用率低于
//     * statusLine就调用taskService中的方法获取每个任务队列中的第一个形成一个map，
//     * 并开始并开始与运行该任务
//     *
//     * @param period
//     */
//    Timer startMonitor(Integer period);
//
//    /**
//     * 关闭gpu
//     * @param timer
//     */
//
//    void stopMonitor(Timer timer);

}

package com.srx.spring.development.Mapper;

import com.srx.spring.development.Entities.GPUDraw;
import com.srx.spring.development.Entities.GPUInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GpuMapper {

    /**
     * 插入一个gpu，用于系统初始化，或是gpu自动添加，应该设置一个更新按钮，用户点击之后，系统自己检测显卡个数，然后添加或删除显卡信息
     *
     * @param gpu
     * @return
     */
    Boolean insertGpu(GPUInfo gpu);

    /**
     * 根据传入的gpuinfo实时更改数据库中表的状态，返回被更新的记录的id
     *
     * @param gpu
     * @return
     */
    Integer updateStatus(GPUInfo gpu);

    /**
     * 更新gpu的全部信息
     *
     * @param gpu
     * @return
     */
    Integer updateGpu(GPUInfo gpu);

    /**
     * 直接返回数据库中所有的Gpu信息
     *
     * @return
     */
    List<GPUInfo> queryGpuList();

    /**
     * 查询所有gpu的数量，用于创建任务队列
     *
     * @return
     */
    Integer queryAllGpuNumber();

    /**
     * 插入一个gpu的状态点
     *
     * @param gpuDraw
     * @return
     */
    Boolean insertGpuDraw(GPUDraw gpuDraw);

    /**
     * 通过id和名字查询一个gpu的12小时内的所有使用率
     * @param gpuName
     * @param gpuId
     * @return
     */
    List<String> queryGpuStatusPointList(@Param("name") String gpuName, @Param("gpuId") String gpuId);

}

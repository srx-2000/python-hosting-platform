package com.srx.spring.development.Mapper;


import com.srx.spring.development.Entities.InputFile;
import com.srx.spring.development.Entities.OutputFile;
import com.srx.spring.development.Entities.PyFile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMapper {

    /**
     * 插入一个input文件，这个只是在数据库中保存相应的路径，并非真的往数据库中插入一个文件
     * @param file
     * @return
     */
    Boolean insertInputFile(InputFile file);

    /**
     * 通过taskKey查询一个任务下的所有input文件
     * @param taskKey
     * @return
     */
    List<InputFile> queryInputFileByTaskKey(@Param("taskKey") String taskKey);


    /**
     * 更改文件路径
     * @param file
     * @return
     */
    Boolean updateInputFilePath(InputFile file);

    /**
     * 这里是删除一个任务下的所有input文件
     * @param taskKey
     * @return
     */
    Boolean updateInputFileStatusToDelete(@Param("taskKey") String taskKey);


    /**
     * 插入一个py文件，这个只是在数据库中保存相应的路径，并非真的往数据库中插入一个文件
     * @param file
     * @return
     */
    Boolean insertPyFile(PyFile file);

    /**
     * 通过taskKey查询一个任务下的所有py文件
     * @param taskKey
     * @return
     */
    List<PyFile> queryPyFileByTaskKey(@Param("taskKey") String taskKey);


    /**
     * 更改文件路径
     * @param file
     * @return
     */
    Boolean updatePyFilePath(PyFile file);

    /**
     * 这里是删除一个任务下的所有py文件
     * @param taskKey
     * @return
     */
    Boolean updatePyFileStatusToDelete(@Param("taskKey") String taskKey);

    /**
     * 插入一个input文件，这个只是在数据库中保存相应的路径，并非真的往数据库中插入一个文件
     * @param file
     * @return
     */
    Boolean insertOutputFile(OutputFile file);

    /**
     * 通过taskKey查询一个任务下的所有input文件
     * @param taskKey
     * @return
     */
    List<OutputFile> queryOutputFileByTaskKey(@Param("taskKey") String taskKey);


    /**
     * 更改文件路径
     * @param file
     * @return
     */
    Boolean updateOutputFilePath(OutputFile file);

    /**
     * 这里是删除一个任务下的所有Output文件
     * @param taskKey
     * @return
     */
    Boolean updateOutputFileStatusToDelete(@Param("taskKey") String taskKey);


}

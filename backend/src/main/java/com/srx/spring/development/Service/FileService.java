package com.srx.spring.development.Service;

import java.util.List;

public interface FileService<T> {

    /**
     * 插入一个泛型类型的文件，这个只是在数据库中保存相应的路径，并非真的往数据库中插入一个文件
     * @param file
     * @return
     */
    Boolean insertFile(T file);

    /**
     * 通过taskKey查询一个任务下的所有泛型类型文件
     * @param taskKey
     * @return
     */
    List<T> queryFileByTaskKey(String taskKey,Class T);


    /**
     * 更改泛型类型文件路径
     * @param file
     * @return
     */
    Boolean updateFilePath(T file);

    /**
     * 这里是删除一个任务下的所有泛型类型文件
     * @param taskKey
     * @return
     */
    Boolean updateFileStatusToDelete(String taskKey,Class T);



}

package com.srx.spring.development.Service.impl;

import com.srx.spring.development.Entities.InputFile;
import com.srx.spring.development.Entities.OutputFile;
import com.srx.spring.development.Entities.PyFile;
import com.srx.spring.development.Mapper.FileMapper;
import com.srx.spring.development.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FileServiceImplement<T> implements FileService<T> {

    @Autowired
    private FileMapper fileMapper;


    @Override
    public Boolean insertFile(T file) {
        Class<?> fileClass = file.getClass();
        if (fileClass.equals(InputFile.class))
            return fileMapper.insertInputFile((InputFile) file);
        else if (fileClass.equals(PyFile.class))
            return fileMapper.insertPyFile((PyFile) file);
        else if (fileClass.equals(OutputFile.class))
            return fileMapper.insertOutputFile((OutputFile) file);
        return false;
    }

    @Override
    public List<T> queryFileByTaskKey(String taskKey, Class T) {
        if (T.equals(InputFile.class))
            return (List<T>) fileMapper.queryInputFileByTaskKey(taskKey);
        else if (T.equals(PyFile.class))
            return (List<T>) fileMapper.queryPyFileByTaskKey(taskKey);
        else if (T.equals(OutputFile.class))
            return (List<T>) fileMapper.queryOutputFileByTaskKey(taskKey);
        return null;
    }


    @Override
    public Boolean updateFilePath(T file) {
        Class<?> fileClass = file.getClass();
        if (fileClass.equals(InputFile.class))
            return fileMapper.updateInputFilePath((InputFile) file);
        else if (fileClass.equals(PyFile.class))
            return fileMapper.updatePyFilePath((PyFile) file);
        else if (fileClass.equals(OutputFile.class))
            return fileMapper.updateOutputFilePath((OutputFile) file);
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean updateFileStatusToDelete(String taskKey,Class T) {
        if (T.equals(null)){
            Boolean flag1 = fileMapper.updateInputFileStatusToDelete(taskKey);
            Boolean flag2 = fileMapper.updatePyFileStatusToDelete(taskKey);
            Boolean flag3 = fileMapper.updateOutputFileStatusToDelete(taskKey);
            return flag1&&flag2&&flag3;
        }else if (T.equals(InputFile.class)){
            return fileMapper.updateInputFileStatusToDelete(taskKey);
        }else if (T.equals(OutputFile.class)){
            return fileMapper.updateOutputFileStatusToDelete(taskKey);
        }else if (T.equals(PyFile.class)){
            return fileMapper.updatePyFileStatusToDelete(taskKey);
        }
        return false;
    }


}

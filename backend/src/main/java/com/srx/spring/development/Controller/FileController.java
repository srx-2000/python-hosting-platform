package com.srx.spring.development.Controller;

import com.srx.spring.development.Entities.OutputFile;
import com.srx.spring.development.Service.FileService;
import com.srx.spring.development.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class FileController {

    @Autowired
    private FileService<OutputFile> outputFileFileService;

    @GetMapping("/download")
    public void downLoad(@RequestParam String taskKey, HttpServletResponse response) throws UnsupportedEncodingException {
        List<OutputFile> outputFiles = outputFileFileService.queryFileByTaskKey(taskKey, OutputFile.class);
        List<File> versionZip = new ArrayList<>();
        String filePath = outputFiles.get(0).getFilePath();
        for (OutputFile o : outputFiles) {
            String fileName = o.getFileName();
            versionZip.add(new File(filePath + fileName));
        }
        String fileZip = filePath + "output.zip";
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
        FileUtil.downloadFile(response, filePath, "output.zip");
    }


}

package com.srx.spring.development.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

public class FileUtil {

    /**
     * 判断需求文件是否符合python安装格式
     *
     * @param filePath
     * @return
     */
    public static Boolean isRequirements(String filePath) {
        try {
            File file = new File(filePath);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String strLine = "";
            String content = "";
            while (null != (strLine = bufferedReader.readLine())) {
                System.out.println(strLine);
                content += strLine;
            }
            System.out.println(content);
            String pattern = "\\w*==\\d+.\\d+.\\d+";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(content);
            System.out.println(m.find());
            return m.find();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 文件上传方法
     *
     * @param file     文件的二进制形式
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @throws IOException
     */
    public static void uploadFile(byte[] file, String filePath, String fileName) throws IOException {
        FileOutputStream out;
        if (fileName == null) {
            out = new FileOutputStream(filePath);
        } else {
            out = new FileOutputStream(filePath +"/"+ fileName);
        }
        out.write(file);
        out.flush();
        out.close();
    }

    public static void downloadFile(HttpServletResponse response,String filePath,String filename) throws UnsupportedEncodingException {
        File file = new File(filePath +  filename);
        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filename, "utf8"));
            byte[] buffer = new byte[1024];
            //输出流
            OutputStream os = null;
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis);) {
                os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 压缩成ZIP 方法2
     * @param srcFiles 需要压缩的文件列表
     * @param out           压缩文件输出流
     * @throws RuntimeException 压缩失败会抛出运行时异常
     */
    public static void toZip(List<File> srcFiles , OutputStream out)throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(out);
            for (File srcFile : srcFiles) {
                byte[] buf = new byte[BUFFER_SIZE];
                zos.putNextEntry(new ZipEntry(srcFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(srcFile);
                while ((len = in.read(buf)) != -1){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

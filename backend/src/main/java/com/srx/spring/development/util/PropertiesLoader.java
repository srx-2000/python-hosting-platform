package com.srx.spring.development.util;

import java.io.*;
import java.util.Properties;

/**
 * @author srx
 * @description
 * @create 2020-08-24 16:56:41
 */
public class PropertiesLoader {
    private String propertiesName;
    private Properties properties;

    public PropertiesLoader(String propertiesName) {
        this.propertiesName = propertiesName;
        this.loadProperties();
    }

    public void setPropertiesName(String propertiesName) {
        this.propertiesName = propertiesName;
    }


    private void loadProperties() {
        try {
            properties = new Properties();
            InputStream inputStream=this.getClass().getClassLoader().getResourceAsStream(this.propertiesName);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getValue(String key) {
        String value=null;
        try {
            value=new String(this.properties.getProperty(key).toString().getBytes(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return value;
    }
}

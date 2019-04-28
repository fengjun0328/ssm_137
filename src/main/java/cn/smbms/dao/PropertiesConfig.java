package cn.smbms.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig {
    private static PropertiesConfig propertiesConfig;
    static Properties params=new Properties();
    //初始化连接参数,从配置文件里获得
    public  void init(){
        String configFile = "database.properties";
        InputStream is= BaseDao.class.getClassLoader().getResourceAsStream(configFile);
        try {
            params.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 私有的
     */
    private PropertiesConfig() {
        init();
    }
    /**
     * 通过key获取值
     */
    public String getValueByKey(String key){
        return params.getProperty(key);
    }
    /**
     * 向外界提供公共的方法
     * @return
     */
    public synchronized static PropertiesConfig getInstance(){
        if(propertiesConfig==null){
            propertiesConfig=new PropertiesConfig();
        }
        return  propertiesConfig;
    }
}

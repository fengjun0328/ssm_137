package cn.smbms.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfig_1 {
    private static PropertiesConfig_1 propertiesConfig;
    static Properties params=new Properties();
    //初始化连接参数,从配置文件里获得
    public static void init(){

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
    private PropertiesConfig_1() {
        init();
    }

    /**
     * 通过key获取值
     */
    public String getValueByKey(String key){
        return params.getProperty(key);
    }

    /**
     * 内部静态类
     */
    public static class PropertiesConfig_1Helper{
        public static PropertiesConfig_1 propertiesConfig1=new PropertiesConfig_1();
        public static PropertiesConfig_1 getInstance_1(){
            return propertiesConfig1;
        }
    }
    /**
     * 向外界提供公共的方法
     * @return
     */
    public  static PropertiesConfig_1 getInstance(){
        return  propertiesConfig;
    }

    /**
     * 向外界提供公共的方法（采用内部类）
     * @return
     */
    public static PropertiesConfig_1 getInstance_2(){
        return PropertiesConfig_1Helper.getInstance_1();
    }
}

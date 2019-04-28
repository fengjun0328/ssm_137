package cn.smbms.tools;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
    private  String attr;

    public StringToDateConverter(String attr) {
        this.attr = attr;
    }

    @Override
    public Date convert(String source) {
        Date date=null;
        try {
            return new SimpleDateFormat(attr).parse(source);
        }catch (ParseException e){
           e.printStackTrace();
           return  null;
        }
    }
}

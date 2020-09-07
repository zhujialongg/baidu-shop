package com.baidu.shop.exception.errorenum;

import com.baidu.shop.exception.service.ErrorCode;
import org.apache.commons.lang.StringUtils;

public enum ErrorCodeEnum implements ErrorCode {

    UNSPECIFIED("500", "网络异常，请稍后再试"),
    ID_NOT_NULL("500", "ID不能为空")

    ;

    private final  String code;

    private final String description;

    //构造函数
    private ErrorCodeEnum(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    //根据编码查询枚举
    public  static ErrorCodeEnum getByCode(String code){
        System.out.println(ErrorCodeEnum.values());
        for (ErrorCodeEnum value : ErrorCodeEnum.values()) {
            if(StringUtils.equals(code,value.getCode())){
                return  value;
            }
        }
        return UNSPECIFIED; //否则网络异常
    }
    //枚举是否包含此编码code
    public static Boolean contains(String code){
        for (ErrorCodeEnum value : ErrorCodeEnum.values()) {
            if (StringUtils.equals(code, value.getCode())) {
                return true;
            }
        }
        return  false;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}

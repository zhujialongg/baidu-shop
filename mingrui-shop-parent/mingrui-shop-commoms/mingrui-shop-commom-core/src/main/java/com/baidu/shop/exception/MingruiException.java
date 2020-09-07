package com.baidu.shop.exception;

import com.baidu.shop.exception.errorenum.ErrorCodeEnum;
import com.baidu.shop.exception.service.ErrorCode;
import lombok.Data;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Data
public class MingruiException extends RuntimeException{


    protected final ErrorCode  errorCode; //错误码

    private String errorMsg;  // 错误信息

    //默认的无参构造  默认指向 网络异常UNSPECIFIED枚举
    public MingruiException() {
        super(ErrorCodeEnum.UNSPECIFIED.getDescription());
        this.errorCode = ErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 指定错误码构造通用异常
     * @param errorCode 错误码
     */
    public MingruiException(final ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    /**
     * 指定详细描述构造通用异常
     * @param detailedMessage 详细描述
     */
    public MingruiException(final String detailedMessage) {
        super(detailedMessage);
        this.errorCode = ErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 指定导火索构造通用异常
     * @param t 导火索
     */
    public MingruiException(final Throwable t) {
        super(t);
        this.errorCode = ErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param detailedMessage 详细描述
     */
    public MingruiException(final ErrorCode errorCode, final String detailedMessage) {
        super(detailedMessage);
        this.errorCode = errorCode;
    }

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param t 导火索
     */
    public MingruiException(final ErrorCode errorCode, final Throwable t) {
        super(errorCode.getDescription(), t);
        this.errorCode = errorCode;
    }

    /**
     * 构造通用异常
     * @param detailedMessage 详细描述
     * @param t 导火索
     */
    public MingruiException(final String detailedMessage, final Throwable t) {
        super(detailedMessage, t);
        this.errorCode = ErrorCodeEnum.UNSPECIFIED;
    }

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param detailedMessage 详细描述
     * @param t 导火索
     */
    public MingruiException(final ErrorCode errorCode, final String detailedMessage,
                        final Throwable t) {
        super(detailedMessage, t);
        this.errorCode = errorCode;
    }

    /**
     * Getter method for property <tt>errorCode</tt>.
     *
     * @return property value of errorCode
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }


}

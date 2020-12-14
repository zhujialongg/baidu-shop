package com.baidu.shop.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public class AlipayConfig {



    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102600766694";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdTkI1FrNqdlXFWUb4Tir0bjwVocTu9Z9mRT9sV8EVq8pbOMNcRgtdpxM5h5egghiTLYsYCwxNTuvFmFyyuAzkTh8XTzSESyFlWOLZuwBDPwkvnl+KXj2tqMVnt0rBwbWswc3Rd3hWnh+Eas0x7N2kuzPP6Ml4AGj1L+ND0VEVmkkF3Ql7DqMSNK+tueU5UWndQ+8i5KCGCnAckbg4bVzM67+sl0dxQEacT9BF2VTsR6eQMR5Rr4/3GTeHiRa6ymcFQsDGFlrz0mWUlPZUwLTiBZ1Mu4VJ6MfUQDNCfDdMgZooHCCF6P6ZSosrkbLCry8ZpinIdmDbGFghXqjJ2HtvAgMBAAECggEBAIEvLMYmf2+WAPhSSitPHo2gHe3x7SYFR0OY6O4T3zjp3QQvSJj12Q2vUOV1CjvBu2bbBxjWseKnRo7+glovNj8kPPpKjKl8fc+aQKponV2cXfHiUTn0DRkZugxkBZu7dnSKsHkucQBJWT7I6vzuNlO7fWBoH1RSmd+J1UzzkHB4+3c4wxUe3Trhw5p7Xjix5cjpdYgucFv0h1pk8ffBRgotz5uoqwXFxAQOOPPPRor7UHlIhcWWV6nCCSZzoMSFBojrI8SM+jhKreNpjW1H4ny5xH2+AXGlvlQYRH7feZ1ip6VIrGdjOQBCGKqtWhHLkzrf/Ff7QQelnt7Qg15QjlkCgYEAzjIGs9OUkRxJYeH2HRFHlhIdPzpHVSNTk7//TWSAsbM8gyTFfcLaxyoJyyCmRUJMwQqPiz6l8TkXhPjGPo+RlbltC/29w+/cUYkiiYXCEwtwr3wBCLSlD17TZm9pRpR97P4AecAadUmGxnnfyrWTau5hyvH+46fKm9ujXDdSz8sCgYEAw00qR97S5XIXsuYsTEq45w7uIVHcn/MeHkvzh3kS9Ff4wQj8Y3xOg/EvfkUGL8YhOLO8akvagDz+F62+/ZejVGyEr3FIvgglv0YXta3XHvgx3TjjjHd5QBnNZHdPw6Mlco4aQSi8AgeKnBf5WuuQAD3XF+njtYXOk8c2IpK9xm0CgYEAkrs808k0BxOXEOXIyySZyax4TZ8+VdK+zfyqRDvJ7Sq8XCLVu0nngCVlNLy2NcEZd6H5RkC9u+xB1WlnQevO0TpT5F2cJgvRv+ATyMN7uSvB888NuVE7yLVVQKb1xrky52xVkE5pRZ+eHi1qWQlfr4+V3eLZ3M1xu/nPxXbFIoMCgYBamfqqWsmVncc2vCcCwYtjzX5VQMfMABoRzgRM7bMIXaNN0eBcOrel6AazWkwTb6PLzT30Rfo4kZMC4xx+QpGHwcwQI8quNdNlF5w6MUph2JwxV4ngiTBbt1ycZelABfiCWz/8yGvnuR3pagnIzwz/v7CALIXxmQCLd9g6U77sRQKBgFywbf2tw9V80nNm63mhi2d5xwsPsdtPqY8AmCwSxAN8ws1KzGCXO9DgRxOmIUv2nQEwmqd1zJvHOAbSv7zI8RhzvKeaAL6WvTnY5EEVacvqXAYhBmk9VH/SHU2BgpZbLMBUzWCD4KhgwtX9enii2PZIcQtzVbxwXaShluOfzcS8";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj/RRQxXXSKCi5ln+wIUKGNJXTk2dQ6dT9TRM32E+X2taffpoKxAOs18lQzNxwO5beaiyzcWgq4/UBPpon0MPfWLjbFlhim879GuNCPoUuFO7/IJ+q5nDmEWP+U+pMtUEiRe6Hy6A71YxcvMJUD1eXjRur3Ws4Y1kHly6etMAP198IK8a1HnhUqtUOXjg2MS3D+SD8UWSrsJFQ5B2/0+ber0vL24JbaAMCe6zFXBrD6MgiUPIyhgSTAUVzly5tt1JZqH0xyWM3l/wgR9Hh43x9P0uaFhmbIi2JymssHbznBQWBBj8rkMl+UqTwCVQz5CrOsUykbB6CjeHWf1VmWGfkwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8900/pay/returnNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8900/pay/returnURL";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";



//    /**
//     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
//     * @param sWord 要写入日志里的文本内容
//     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

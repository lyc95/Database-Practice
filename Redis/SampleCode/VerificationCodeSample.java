package jedis;
import redis.clients.jedis.Jedis;
import java.util.Random;

//Requirement
//1. Generate 6 digit verfication code
//2. valid within 2 mins
//3. only 3 chances for each phone daily
public class VerificationCodeSample {

    public static void main(String[] args) {
        //String code = setVerifyCode("123465");
        //System.out.println(code);
        System.out.println(getRedisCode("123456","123456"));
        System.out.println(getRedisCode("123456","573312"));
    }

    // get random code
    public static String get6DigitCode(){
        Random rdn = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(rdn.nextInt(10));
        }
        return sb.toString();
    }
    // set verification code with 3 chances daily
    public static String setVerifyCode(String phone) {
        Jedis jedis = new Jedis("xx.xx.xx.xx", 6379);
        String cntKey = "VerificationCode" + phone + ":count";
        String codeKey = "VerificationCode" + phone + ":code";
        String cnt = jedis.get(cntKey);
        if (cnt == null) {
            //first time
            jedis.setex(cntKey, 24*60*60, "1");
        } else if(Integer.parseInt(cnt) <= 2){
            jedis.incr(cntKey);
        } else if (Integer.parseInt(cnt) > 2){
            System.out.println("No more chances for today!");
            return null;
        }
        // set verification code
        String vCode = get6DigitCode();
        jedis.setex(codeKey, 120, vCode);
        jedis.close();
        return vCode;
    }

    public static boolean getRedisCode(String phone, String code) {
        Jedis jedis = new Jedis("xx.xx.xx.xx", 6379);
        String codeKey = "VerificationCode" + phone + ":code";
        String redisCode = jedis.get(codeKey);
        jedis.close();
        if (redisCode.equals(code)) {
            System.out.println("success");
            return true;
        } else {
            System.out.println("fail");
            return false;
        }

    }

}

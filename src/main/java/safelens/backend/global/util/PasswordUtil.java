package safelens.backend.global.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * 비밀번호 암호화
     */
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    /**
     * 비밀번호 검증
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}

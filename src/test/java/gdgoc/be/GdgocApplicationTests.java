package gdgoc.be;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class GdgocApplicationTests {

    @Test
    void contextLoads() {
        // 기존에 있던 테스트 (냅둬도 됨)
    }

    @Test
    void getHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1부터 5까지 반복하면서 해시값 생성
        for (int i = 1; i <= 5; i++) {
            String rawPassword = "password" + i;
            String realHash = encoder.encode(rawPassword);

            // 결과 출력
            System.out.println(rawPassword + " 의 진짜 해시값: " + realHash);
        }
    }
}

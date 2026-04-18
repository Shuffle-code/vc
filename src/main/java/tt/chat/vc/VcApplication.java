package tt.chat.vc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VcApplication {

    public static void main(String[] args) {
        SpringApplication.run(VcApplication.class, args);
    }

}

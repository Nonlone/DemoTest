package per.nonlone.test.stress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = {"com.zuzuche"})
@SpringBootApplication
public class StressTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StressTestApplication.class, args);
    }

}

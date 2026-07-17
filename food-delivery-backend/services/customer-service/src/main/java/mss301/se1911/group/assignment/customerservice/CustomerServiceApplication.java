package mss301.se1911.group.assignment.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        // Ép timezone chuẩn IANA để tránh lỗi driver PostgreSQL gửi alias cũ "Asia/Saigon".
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

}

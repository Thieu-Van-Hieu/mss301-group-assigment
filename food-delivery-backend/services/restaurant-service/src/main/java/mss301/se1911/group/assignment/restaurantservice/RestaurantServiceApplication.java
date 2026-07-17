package mss301.se1911.group.assignment.restaurantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class RestaurantServiceApplication {

    public static void main(String[] args) {
        // Ép timezone chuẩn IANA để tránh lỗi driver PostgreSQL gửi alias cũ "Asia/Saigon"
        // (bản tzdata mới của image postgres không còn nhận alias này).
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(RestaurantServiceApplication.class, args);
    }

}

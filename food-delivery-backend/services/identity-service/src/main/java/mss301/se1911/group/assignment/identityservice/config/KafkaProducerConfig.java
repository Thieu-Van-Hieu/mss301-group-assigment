package mss301.se1911.group.assignment.identityservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // 1. Tạo ProducerFactory: Định nghĩa cách thức kết nối và mã hóa dữ liệu
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        // Địa chỉ cụm Kafka Broker (lấy từ Config Server / .env)
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Mã hóa Key của message dưới dạng String thuần (thường là UserId)
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Mã hóa Value (Object Event) tự động chuyển sang chuỗi JSON
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // acks=all đảm bảo dữ liệu được ghi nhận chắc chắn trên cụm KRaft trước khi báo thành công
        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        // Thử lại tối đa 3 lần nếu mạng mẽo chập chờn
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    // 2. Tạo bản thiết kế KafkaTemplate chuẩn chỉnh để tiêm (Inject) vào Adapter
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
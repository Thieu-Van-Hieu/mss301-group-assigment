package mss301.se1911.group.assignment.commonsecurity.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "security")
public class CommonSecurityProperties {
    private List<String> permitAllPaths = new ArrayList<>();
}
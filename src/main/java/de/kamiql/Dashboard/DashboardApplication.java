package de.kamiql.Dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@EnableScheduling
public class DashboardApplication {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(DashboardApplication.class, args);
	}
}

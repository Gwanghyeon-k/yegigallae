package com.umc.yeogi_gal_lae;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UmcYeogigallaeApplication {

	// 서버 시간 "Asia/Seoul"로 설정
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(UmcYeogigallaeApplication.class, args);
	}

}

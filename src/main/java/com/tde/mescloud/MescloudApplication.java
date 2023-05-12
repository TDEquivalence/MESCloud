package com.tde.mescloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://staging.mescloud.pt", "http://staging.mescloud.pt"})
@SpringBootApplication
public class MescloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(MescloudApplication.class, args);
	}

}

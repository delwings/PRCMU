package edu.unimagdalena.PRCMU;

import org.springframework.boot.SpringApplication;

public class TestRcmuApplication {

	public static void main(String[] args) {
		SpringApplication.from(RcmuApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

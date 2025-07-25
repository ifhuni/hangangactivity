package com.example.hangangactivity.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
public class MainApplication {

  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }

  @GetMapping("/")
  public String main() {
    return "main"; // 전체 템플릿
  }

  @GetMapping("/fragments/home")
  public String homeFragment() {
    return "fragments/home :: home";
  }

  @GetMapping("/fragments/reservation")
  public String reservationFragment() {
    return "fragments/reservation :: reservation";
  }
}

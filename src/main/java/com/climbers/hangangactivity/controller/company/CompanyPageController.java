package com.climbers.hangangactivity.controller.company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 회사 관련 페이지들을 처리하는 컨트롤러
 */
@Controller
public class CompanyPageController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyPageController.class);
    
    /**
     * 회사 로그인 페이지를 보여줍니다.
     */
    @GetMapping("/company/login")
    public String showCompanyLoginPage() {
        logger.info("Accessing company login page");
        return "company/login";  // 회사 로그인 페이지 템플릿 반환
    }

    /**
     * 회사 회원가입 페이지를 보여줍니다.
     */
    @GetMapping("company/register")
    public String showRegisterPage() {
        logger.info("Accessing company register page");
        return "company/register";  // 회원가입 페이지 템플릿 반환
    }

    /**
     * 업체 index.html을 보여줍니다.
     */
    @GetMapping("/company/index")
    public String companyIndex() {
        return "company/index"; // company/index.html 템플릿 반환
    }

    /**
     * 업체 index.html을 보여줍니다.
     */
    @GetMapping("/company/tables")
    public String tablesTables() {
        return "company/tables"; // company/index.html 템플릿 반환
    }

    /**
     * 프로그램 index.html을 보여줍니다.
     */
    @GetMapping("/company/program")
    public String programTables() {
        return "company/program"; // company/index.html 템플릿 반환
    }
    
    /**
     * 예약 index.html을 보여줍니다.
     */
    @GetMapping("/company/reservation")
    public String reservationTables() {
        return "company/reservation"; // company/index.html 템플릿 반환
    }
    
    /**
     * 재고 index.html을 보여줍니다.
     */
    @GetMapping("/company/inventory")
    public String inventoryTables() {
        return "company/inventory"; // company/index.html 템플릿 반환
    }
    
    /**
     * 휴무일 index.html을 보여줍니다.
     */
    @GetMapping("/company/holidays")
    public String holidaysTables() {
        return "company/holidays"; // company/index.html 템플릿 반환
    }
}

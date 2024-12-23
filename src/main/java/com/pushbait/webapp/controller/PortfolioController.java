package com.pushbait.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortfolioController {

    @GetMapping("/portfolio")
    public String showPortfolio() {
        return "portfolio";
    }  
}

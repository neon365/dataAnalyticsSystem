package com.dataanalytics.controller;

import java.lang.management.ManagementFactory;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiagnosticController {
	
    @RequestMapping("/uptime")
    Long uptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

}

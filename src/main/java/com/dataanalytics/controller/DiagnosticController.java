package com.dataanalytics.controller;

import org.springframework.web.bind.annotation.*;
import java.lang.management.ManagementFactory;

@RestController
public class DiagnosticController {

    @RequestMapping("/uptime")
    Long uptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

}

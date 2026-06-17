package com.sofit.externalmock.global.log;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class ApiLogController {

    private final ApiLogStore apiLogStore;

    @GetMapping
    public List<ApiLog> getLogs() {
        return apiLogStore.getAll();
    }

    @DeleteMapping
    public String clearLogs() {
        apiLogStore.clear();
        return "cleared";
    }
}

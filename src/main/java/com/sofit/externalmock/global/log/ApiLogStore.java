package com.sofit.externalmock.global.log;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 최근 100건의 API 호출 로그를 메모리에 저장합니다.
 */
@Component
public class ApiLogStore {

    private static final int MAX_SIZE = 100;
    private final ConcurrentLinkedDeque<ApiLog> logs = new ConcurrentLinkedDeque<>();

    public void add(ApiLog log) {
        logs.addFirst(log);
        while (logs.size() > MAX_SIZE) {
            logs.removeLast();
        }
    }

    public List<ApiLog> getAll() {
        return new ArrayList<>(logs);
    }

    public void clear() {
        logs.clear();
    }
}

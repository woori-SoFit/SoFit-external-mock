package com.sofit.externalmock.global.log;

import java.time.LocalDateTime;

public record ApiLog(
        String method,
        String uri,
        int status,
        long durationMs,
        LocalDateTime timestamp
) {
}

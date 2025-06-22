package com.gongdel.promptserver.adapter.in.rest.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 클라이언트 IP 주소 추출 유틸리티 클래스입니다.
 * 프록시 서버나 로드 밸런서를 고려하여 실제 클라이언트 IP를 추출합니다.
 */
@Slf4j
public class ClientIpExtractor {

    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";

    /**
     * HttpServletRequest에서 실제 클라이언트 IP 주소를 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            log.warn("HttpServletRequest is null, returning localhost IP");
            return LOCALHOST_IPV4;
        }

        // 헤더에서 IP 추출 시도
        for (String header : IP_HEADER_CANDIDATES) {
            String ipAddress = request.getHeader(header);
            if (isValidIpAddress(ipAddress)) {
                // X-Forwarded-For 헤더는 쉼표로 구분된 여러 IP를 포함할 수 있음
                if (header.equals("X-Forwarded-For") && ipAddress.contains(",")) {
                    ipAddress = ipAddress.split(",")[0].trim();
                }

                log.debug("Client IP extracted from header [{}]: {}", header, ipAddress);
                return ipAddress;
            }
        }

        // 헤더에서 찾지 못한 경우 기본 remote address 사용
        String remoteAddr = request.getRemoteAddr();
        if (isValidIpAddress(remoteAddr)) {
            log.debug("Client IP extracted from remote address: {}", remoteAddr);
            return remoteAddr;
        }

        // 모든 방법이 실패한 경우 로컬호스트 반환
        log.warn("Unable to determine client IP address, returning localhost IP");
        return LOCALHOST_IPV4;
    }

    /**
     * IP 주소가 유효한지 검증합니다.
     *
     * @param ipAddress 검증할 IP 주소
     * @return 유효한 IP 주소인지 여부
     */
    private static boolean isValidIpAddress(String ipAddress) {
        return StringUtils.hasText(ipAddress) &&
            !UNKNOWN.equalsIgnoreCase(ipAddress) &&
            !isLoopbackAddress(ipAddress);
    }

    /**
     * 루프백 주소인지 확인합니다.
     *
     * @param ipAddress IP 주소
     * @return 루프백 주소인지 여부
     */
    private static boolean isLoopbackAddress(String ipAddress) {
        return LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress);
    }

    /**
     * 개발 환경에서 실제 IP 주소를 시뮬레이션합니다.
     * 로컬 개발 시 테스트용으로 사용합니다.
     *
     * @param request HTTP 요청 객체
     * @return 개발용 시뮬레이션 IP 또는 실제 IP
     */
    public static String getClientIpAddressForDevelopment(HttpServletRequest request) {
        String realIp = getClientIpAddress(request);

        // 로컬호스트인 경우 개발용 IP로 대체
        if (LOCALHOST_IPV4.equals(realIp) || LOCALHOST_IPV6.equals(realIp)) {
            String simulatedIp = "192.168.1." + (System.currentTimeMillis() % 255);
            log.debug("Development mode: simulating IP {} instead of {}", simulatedIp, realIp);
            return simulatedIp;
        }

        return realIp;
    }
}

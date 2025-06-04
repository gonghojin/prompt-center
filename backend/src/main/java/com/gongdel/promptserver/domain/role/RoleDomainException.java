package com.gongdel.promptserver.domain.role;

/**
 * 역할 도메인 예외
 * <p>
 * 역할 도메인 계층에서 발생하는 예외를 처리합니다.
 * </p>
 */
public class RoleDomainException extends RuntimeException {
    /**
     * 메시지 기반 예외 생성자
     *
     * @param message 예외 메시지
     */
    public RoleDomainException(String message) {
        super(message);
    }

    /**
     * 메시지 및 원인 기반 예외 생성자
     *
     * @param message 예외 메시지
     * @param cause   원인 예외
     */
    public RoleDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 역할을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param id 역할 ID
     * @return RoleDomainException
     */
    public static RoleDomainException notFound(Long id) {
        return new RoleDomainException(String.format("Role not found with id: %d", id));
    }

    /**
     * 역할을 찾을 수 없을 때 발생하는 예외를 생성합니다.
     *
     * @param name 역할 이름
     * @return RoleDomainException
     */
    public static RoleDomainException notFoundByName(String name) {
        return new RoleDomainException(String.format("Role not found with name: %s", name));
    }
}

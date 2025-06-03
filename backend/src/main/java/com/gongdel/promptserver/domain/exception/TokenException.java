package com.gongdel.promptserver.domain.exception;

/**
 * 토큰 관련 비즈니스 로직 처리 중 발생하는 도메인 예외입니다.
 */
public class TokenException extends AuthDomainException {

    /**
     * 지정된 에러 타입과 메시지로 토큰 예외를 생성합니다.
     *
     * @param errorType 토큰 관련 에러 타입
     * @param message   예외 메시지
     */
    public TokenException(AuthErrorType errorType, String message) {
        super(errorType, message);
    }

    /**
     * 지정된 에러 타입, 메시지, 원인 예외와 함께 토큰 예외를 생성합니다.
     *
     * @param errorType 토큰 관련 에러 타입
     * @param message   예외 메시지
     * @param cause     원인 예외
     */
    public TokenException(AuthErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }

    /**
     * 토큰을 찾을 수 없는 경우의 예외를 생성합니다.
     *
     * @param tokenId 찾을 수 없는 토큰의 ID
     * @return 토큰을 찾을 수 없음을 나타내는 예외
     */
    public static TokenException notFound(String tokenId) {
        return new TokenException(
                AuthErrorType.TOKEN_NOT_FOUND,
                String.format("토큰을 찾을 수 없습니다: %s", tokenId));
    }

    /**
     * 토큰이 만료된 경우의 예외를 생성합니다.
     *
     * @param tokenId 만료된 토큰의 ID
     * @return 토큰 만료를 나타내는 예외
     */
    public static TokenException expired(String tokenId) {
        return new TokenException(
                AuthErrorType.TOKEN_EXPIRED,
                String.format("토큰이 만료되었습니다: %s", tokenId));
    }

    /**
     * 토큰이 이미 블랙리스트에 등록된 경우의 예외를 생성합니다.
     *
     * @param tokenId 블랙리스트에 이미 등록된 토큰의 ID
     * @return 토큰이 이미 블랙리스트에 있음을 나타내는 예외
     */
    public static TokenException alreadyBlacklisted(String tokenId) {
        return new TokenException(
                AuthErrorType.TOKEN_ALREADY_BLACKLISTED,
                String.format("이미 블랙리스트에 등록된 토큰입니다: %s", tokenId));
    }

    /**
     * 토큰 저장에 실패한 경우의 예외를 생성합니다.
     *
     * @param userId 토큰 저장을 시도한 사용자의 ID
     * @param cause  원인 예외
     * @return 토큰 저장 실패를 나타내는 예외
     */
    public static TokenException saveFailed(String userId, Throwable cause) {
        return new TokenException(
                AuthErrorType.TOKEN_SAVE_FAILED,
                String.format("토큰 저장에 실패했습니다: userId=%s", userId),
                cause);
    }

    /**
     * 토큰 삭제에 실패한 경우의 예외를 생성합니다.
     *
     * @param tokenId 삭제에 실패한 토큰의 ID
     * @param cause   원인 예외
     * @return 토큰 삭제 실패를 나타내는 예외
     */
    public static TokenException deleteFailed(String tokenId, Throwable cause) {
        return new TokenException(
                AuthErrorType.TOKEN_DELETE_FAILED,
                String.format("토큰 삭제에 실패했습니다: %s", tokenId),
                cause);
    }

    /**
     * 토큰 블랙리스트 등록에 실패한 경우의 예외를 생성합니다.
     *
     * @param tokenId 블랙리스트 등록에 실패한 토큰의 ID
     * @param cause   원인 예외
     * @return 토큰 블랙리스트 등록 실패를 나타내는 예외
     */
    public static TokenException blacklistFailed(String tokenId, Throwable cause) {
        return new TokenException(
                AuthErrorType.TOKEN_BLACKLIST_FAILED,
                String.format("토큰 블랙리스트 등록에 실패했습니다: %s", tokenId),
                cause);
    }
}

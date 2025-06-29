# 도메인 모델 설계

## 시간 관리 구조

프로젝트의 시간 관리는 아래와 같이 이원화된 구조로 설계되었습니다:

### 1. 도메인 레이어

- `BaseTimeEntity`: 도메인 모델에서 시간을 관리하는 추상 클래스
- 인프라스트럭처 기술에 독립적인 순수한 도메인 모델
- 필요한 경우 직접 시간을 업데이트하는 메서드 제공

### 2. 인프라스트럭처 레이어

- `BaseJpaEntity`: JPA 엔티티에서 시간을 관리하는 추상 클래스
- Spring Data JPA의 Auditing 기능을 사용하여 자동으로 시간 갱신
- 데이터베이스 영속성을 위한 설정 포함

## 패턴 적용 방식

도메인 모델과 JPA 엔티티를 분리함으로써:

1. 도메인 모델은 영속성 기술(JPA)에 의존하지 않음
2. 도메인 로직이 더 명확하게 표현됨
3. 테스트가 용이해짐 (인프라스트럭처 기술 없이도 도메인 로직 테스트 가능)

## 사용 방법

### 도메인 모델

```java
public class SomeDomainModel extends BaseTimeEntity {
  // 모델 필드들

  public void someBusinessMethod() {
    // 비즈니스 로직
    // 상태가 변경되었으므로 수정 시간 업데이트
    updateModifiedTime();
  }
}
```

### JPA 엔티티

```java
@Entity
@Table(name = "some_entity")
public class SomeJpaEntity extends BaseJpaEntity {
    // 엔티티 필드들

    // 도메인 모델 변환 메서드
    public SomeDomainModel toDomain() {
        return new SomeDomainModel(
            // 필드 매핑
            this.getCreatedAt(),
            this.getUpdatedAt()
        );
    }

    // 도메인 모델로부터 엔티티 생성 메서드
    public static SomeJpaEntity fromDomain(SomeDomainModel domain) {
        SomeJpaEntity entity = new SomeJpaEntity();
        // 필드 매핑
        return entity;
    }
}

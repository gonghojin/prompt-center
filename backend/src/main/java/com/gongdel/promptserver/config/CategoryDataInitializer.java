package com.gongdel.promptserver.config;

import com.gongdel.promptserver.adapter.out.persistence.entity.CategoryEntity;
import com.gongdel.promptserver.adapter.out.persistence.repository.JpaCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CategoryDataInitializer {

    private final JpaCategoryRepository categoryRepository;

    @Bean
    @Profile("local")
    public CommandLineRunner initCategoryData(@Value("${init.category.data:true}") boolean init) {
        return args -> {
            if (init) {
                log.info("카테고리 초기 데이터 로드 시작");
                initCategories();
                log.info("카테고리 초기 데이터 로드 완료");
            }
        };
    }

    private void initCategories() {
        // 이미 데이터가 있으면 초기화 생략
        if (categoryRepository.count() > 0) {
            log.info("이미 카테고리 데이터가 존재합니다. 초기화를 건너뜁니다.");
            return;
        }

        Map<String, CategoryEntity> categoryMap = new HashMap<>();

        // 상위 카테고리 생성 (모두 isSystem=true로 설정)
        CategoryEntity development = createCategory("development", "개발", "개발 관련 프롬프트 템플릿", true, null);
        CategoryEntity data = createCategory("data", "데이터", "데이터 관련 프롬프트 템플릿", true, null);
        CategoryEntity design = createCategory("design", "디자인", "디자인 관련 프롬프트 템플릿", true, null);
        CategoryEntity productManagement = createCategory("product_management", "제품 관리", "제품 관리 관련 프롬프트 템플릿", true,
                null);
        CategoryEntity marketing = createCategory("marketing", "마케팅", "마케팅 관련 프롬프트 템플릿", true, null);

        categoryMap.put("development", development);
        categoryMap.put("data", data);
        categoryMap.put("design", design);
        categoryMap.put("product_management", productManagement);
        categoryMap.put("marketing", marketing);

        // 개발 하위 카테고리
        createCategory("backend", "백엔드", "백엔드 개발 관련 프롬프트", true, categoryMap.get("development"));
        createCategory("frontend", "프론트엔드", "프론트엔드 개발 관련 프롬프트", true, categoryMap.get("development"));
        createCategory("mobile", "모바일", "모바일 개발 관련 프롬프트", true, categoryMap.get("development"));
        createCategory("devops", "DevOps", "DevOps 관련 프롬프트", true, categoryMap.get("development"));

        // 데이터 하위 카테고리
        createCategory("data_analysis", "데이터 분석", "데이터 분석 관련 프롬프트", true, categoryMap.get("data"));
        createCategory("machine_learning", "머신러닝", "머신러닝 관련 프롬프트", true, categoryMap.get("data"));
        createCategory("data_engineering", "데이터 엔지니어링", "데이터 엔지니어링 관련 프롬프트", true, categoryMap.get("data"));

        // 디자인 하위 카테고리
        createCategory("ux_design", "UX 디자인", "UX 디자인 관련 프롬프트", true, categoryMap.get("design"));
        createCategory("ui_design", "UI 디자인", "UI 디자인 관련 프롬프트", true, categoryMap.get("design"));
        createCategory("interaction_design", "인터랙션 디자인", "인터랙션 디자인 관련 프롬프트", true, categoryMap.get("design"));

        // 제품 관리 하위 카테고리
        createCategory("product_planning", "제품 기획", "제품 기획 관련 프롬프트", true, categoryMap.get("product_management"));
        createCategory("product_analysis", "제품 분석", "제품 분석 관련 프롬프트", true, categoryMap.get("product_management"));
        createCategory("agile_methodology", "애자일 방법론", "애자일 방법론 관련 프롬프트", true, categoryMap.get("product_management"));

        // 마케팅 하위 카테고리
        createCategory("content_marketing", "콘텐츠 마케팅", "콘텐츠 마케팅 관련 프롬프트", true, categoryMap.get("marketing"));
        createCategory("advertising", "광고", "광고 관련 프롬프트", true, categoryMap.get("marketing"));

        log.info("카테고리 초기 데이터 {} 개 생성 완료", categoryRepository.count());
    }

    private CategoryEntity createCategory(String name, String displayName, String description,
            boolean isSystem, CategoryEntity parentCategory) {
        // 중복 방지를 위한 체크
        Optional<CategoryEntity> existingCategory = categoryRepository.findByName(name);
        if (existingCategory.isPresent()) {
            log.info("이미 존재하는 카테고리입니다: {}", name);
            return existingCategory.get();
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setName(name);
        entity.setDisplayName(displayName);
        entity.setDescription(description);
        entity.setSystem(isSystem);
        entity.setParentCategory(parentCategory);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());

        return categoryRepository.save(entity);
    }
}

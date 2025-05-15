import React from 'react';
import PromptList from '@/components/features/PromptList';

const PromptPage = () => {
  return (
    <main className="max-w-4xl mx-auto py-8 px-4">
      <h1 className="text-3xl font-bold mb-4">프롬프트 목록</h1>
      <p className="mb-6 text-gray-600">등록된 프롬프트 템플릿을 검색·조회할 수 있습니다.</p>
      <PromptList />
    </main>
  );
};

export default PromptPage;

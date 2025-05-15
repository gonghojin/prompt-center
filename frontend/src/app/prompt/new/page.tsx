import React from 'react';
import PromptForm from '@/components/features/PromptForm';

const NewPromptPage = () => {
  return (
    <main className="max-w-3xl mx-auto py-8 px-4">
      <h1 className="text-3xl font-bold mb-4">새 프롬프트 등록</h1>
      <PromptForm mode="new" />
    </main>
  );
};

export default NewPromptPage; 
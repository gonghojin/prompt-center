import React from 'react';
import PromptCard from './PromptCard';
// import usePrompt from '@/hooks/usePrompt';
import { PromptTemplate } from '@/types';

// 임시 mock 데이터
const mockPrompts: PromptTemplate[] = [
  {
    id: '1',
    title: 'API 설계 프롬프트',
    content: 'RESTful API 설계 가이드라인을 작성하세요.',
    description: '백엔드 개발자를 위한 API 설계 템플릿',
    category: '백엔드',
    tags: ['API', 'REST'],
    author: { id: '1', email: 'admin@example.com', name: '관리자', role: 'ADMIN' },
    createdAt: '2024-05-15T12:00:00Z',
    updatedAt: '2024-05-15T12:00:00Z',
    version: 1,
    isPublic: true,
  },
  // 추가 mock 데이터 가능
];

const PromptList: React.FC = () => {
  // const { prompts, loading } = usePrompt();
  // if (loading) return <div>로딩 중...</div>;

  return (
    <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
      {mockPrompts.map((prompt) => (
        <PromptCard key={prompt.id} prompt={prompt} />
      ))}
    </div>
  );
};

export default PromptList; 
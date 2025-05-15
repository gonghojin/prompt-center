import React from 'react';
import { PromptTemplate } from '@/types';

interface PromptDetailProps {
  id: string;
}

// 임시 mock 데이터 (실제 구현 시 API 연동)
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
];

const PromptDetail: React.FC<PromptDetailProps> = ({ id }) => {
  const prompt = mockPrompts.find((p) => p.id === id);
  if (!prompt) return <div>프롬프트를 찾을 수 없습니다.</div>;

  return (
    <section className="bg-white dark:bg-gray-800 rounded-xl border p-6">
      <h1 className="text-2xl font-bold mb-2">{prompt.title}</h1>
      <div className="mb-2 text-gray-500">{prompt.description}</div>
      <div className="mb-4 text-sm text-gray-400">카테고리: {prompt.category}</div>
      <div className="mb-4 whitespace-pre-wrap border rounded p-4 bg-gray-50 dark:bg-gray-900">
        {prompt.content}
      </div>
      <div className="flex flex-wrap gap-2 mb-2">
        {prompt.tags.map((tag) => (
          <span key={tag} className="bg-gray-200 text-xs rounded px-2 py-1">#{tag}</span>
        ))}
      </div>
      <div className="text-xs text-gray-400">
        작성자: {prompt.author.name} | 생성일: {new Date(prompt.createdAt).toLocaleDateString()} | 버전: {prompt.version}
      </div>
    </section>
  );
};

export default PromptDetail; 
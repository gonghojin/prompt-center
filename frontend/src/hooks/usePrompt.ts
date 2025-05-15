import { useState } from 'react';
import { PromptTemplate } from '@/types';

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

export const usePrompt = () => {
  const [loading, setLoading] = useState(false);
  const [prompts, setPrompts] = useState<PromptTemplate[]>(mockPrompts);

  const getPrompts = async () => {
    setLoading(true);
    // TODO: API 연동
    setLoading(false);
    return prompts;
  };

  const getPromptById = async (id: string) => {
    setLoading(true);
    // TODO: API 연동
    const prompt = prompts.find((p) => p.id === id);
    setLoading(false);
    return prompt;
  };

  const createPrompt = async (data: Partial<PromptTemplate>) => {
    setLoading(true);
    // TODO: API 연동
    const newPrompt: PromptTemplate = {
      id: String(prompts.length + 1),
      ...data,
      author: { id: '1', email: 'admin@example.com', name: '관리자', role: 'ADMIN' },
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      version: 1,
    } as PromptTemplate;
    setPrompts((prev) => [...prev, newPrompt]);
    setLoading(false);
    return newPrompt;
  };

  const updatePrompt = async (id: string, data: Partial<PromptTemplate>) => {
    setLoading(true);
    // TODO: API 연동
    const updatedPrompts = prompts.map((p) =>
      p.id === id ? { ...p, ...data, updatedAt: new Date().toISOString(), version: p.version + 1 } : p
    );
    setPrompts(updatedPrompts);
    setLoading(false);
    return updatedPrompts.find((p) => p.id === id);
  };

  const deletePrompt = async (id: string) => {
    setLoading(true);
    // TODO: API 연동
    const filteredPrompts = prompts.filter((p) => p.id !== id);
    setPrompts(filteredPrompts);
    setLoading(false);
    return true;
  };

  return {
    prompts,
    loading,
    getPrompts,
    getPromptById,
    createPrompt,
    updatePrompt,
    deletePrompt,
  };
};

export default usePrompt; 
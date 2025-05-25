"use client";
import React, { useEffect, useState } from 'react';
import PromptCard from './PromptCard';
// import usePrompt from '@/hooks/usePrompt';
import { PromptTemplate } from '@/types';
import { fetchPrompts } from '@/api/prompts';

interface PromptListProps {
  onSelect: (id: string) => void;
}

const PromptList: React.FC<PromptListProps> = ({ onSelect }) => {
  const [prompts, setPrompts] = useState<PromptTemplate[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await fetchPrompts();
        setPrompts(data);
      } catch (err: any) {
        setError(err.message || '알 수 없는 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <div>로딩 중...</div>;
  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <div className="grid gap-4 grid-cols-1 md:grid-cols-2">
      {prompts.map((prompt) => (
        <PromptCard key={prompt.id} prompt={prompt} onSelect={onSelect} />
      ))}
    </div>
  );
};

export default PromptList;

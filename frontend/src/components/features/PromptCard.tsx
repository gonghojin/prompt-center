import React from 'react';
import { PromptTemplate } from '@/types';

interface PromptCardProps {
  prompt: PromptTemplate;
}

const PromptCard: React.FC<PromptCardProps> = ({ prompt }) => {
  return (
    <div className="rounded-xl border p-4 hover:shadow-lg bg-white dark:bg-gray-800">
      <h2 className="text-xl font-semibold mb-2">{prompt.title}</h2>
      <p className="text-gray-600 mb-2">{prompt.description}</p>
      <div className="flex flex-wrap gap-2 mb-2">
        {prompt.tags.map((tag) => (
          <span key={tag} className="bg-gray-200 text-xs rounded px-2 py-1">#{tag}</span>
        ))}
      </div>
      <div className="text-xs text-gray-400">
        작성자: {prompt.author.name} | 생성일: {new Date(prompt.createdAt).toLocaleDateString()}
      </div>
    </div>
  );
};

export default PromptCard; 
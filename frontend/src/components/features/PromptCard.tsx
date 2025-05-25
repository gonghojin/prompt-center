import React from 'react';
import { PromptTemplate } from '@/types';
import { UserIcon, CalendarIcon, HeartIcon, ChartBarIcon } from '@heroicons/react/24/outline';

interface PromptCardProps {
  prompt: PromptTemplate;
  onSelect: (id: string) => void;
}

const PromptCard: React.FC<PromptCardProps> = ({ prompt, onSelect }) => {
  return (
    <div
      className="rounded-2xl border border-gray-200 bg-white shadow-md hover:shadow-xl transition-shadow duration-200 p-6 flex flex-col gap-3 cursor-pointer"
      onClick={() => onSelect(prompt.id)}
    >
      <h2 className="text-lg font-bold text-gray-900 mb-1 truncate">{prompt.title}</h2>
      <p className="text-gray-600 text-sm mb-2 line-clamp-2">{prompt.description}</p>
      <div className="flex flex-wrap gap-2 mb-2">
        {(prompt.tags ?? []).map((tag: any) => {
          let key = '';
          if (typeof tag === 'string') key = tag;
          else if (tag && typeof tag === 'object') key = tag.id ?? tag.name ?? JSON.stringify(tag);
          return (
            <span
              key={key}
              className="bg-blue-100 text-blue-700 text-xs font-medium rounded-full px-3 py-1"
            >
              #{typeof tag === 'string' ? tag : tag.name ?? tag.id}
            </span>
          );
        })}
      </div>
      <div className="flex items-center text-xs text-gray-400 gap-3 mt-auto">
        <span className="flex items-center gap-1">
          <UserIcon className="w-4 h-4" />
          {prompt.author?.name ?? '알 수 없음'}
        </span>
        <span className="flex items-center gap-1">
          <CalendarIcon className="w-4 h-4" />
          {new Date(prompt.createdAt).toLocaleDateString('ko-KR', { year: 'numeric', month: 'short', day: 'numeric' })}
        </span>
        <span className="flex items-center gap-1 text-pink-500 ml-auto">
          <HeartIcon className="w-4 h-4" />
          {prompt.favoriteCount}
        </span>
        <span className="flex items-center gap-1 text-blue-500">
          <ChartBarIcon className="w-4 h-4" />
          {prompt.viewCount}
        </span>
      </div>
    </div>
  );
};

export default PromptCard;

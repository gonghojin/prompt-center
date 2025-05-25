'use client';

import React, { useEffect, useState } from 'react';
import { PromptTemplate } from '@/types';
import { fetchPromptDetail } from '@/api/prompts';
import { ArrowLeftIcon, UserIcon, CalendarIcon, TagIcon, ClipboardIcon, FolderOpenIcon, ExclamationTriangleIcon, CodeBracketIcon, HeartIcon, ChartBarIcon } from '@heroicons/react/24/outline';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import rehypeHighlight from 'rehype-highlight';
import { ArrowPathIcon } from '@heroicons/react/24/solid';

interface PromptDetailProps {
  id: string;
  onBack: () => void;
}

const PromptDetail: React.FC<PromptDetailProps> = ({ id, onBack }) => {
  const [prompt, setPrompt] = useState<PromptTemplate | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await fetchPromptDetail(id);
        setPrompt(data);
      } catch (err: any) {
        setError(err.message || '프롬프트를 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  // 복사 기능
  const handleCopy = () => {
    if (prompt?.content) {
      navigator.clipboard.writeText(prompt.content);
      setCopied(true);
      setTimeout(() => setCopied(false), 1200);
    }
  };

  // 스타일: 태그 색상
  const tagColors = [
    'bg-blue-200', 'bg-green-200', 'bg-yellow-200', 'bg-purple-200', 'bg-pink-200', 'bg-indigo-200', 'bg-red-200',
    'dark:bg-blue-800', 'dark:bg-green-800', 'dark:bg-yellow-800', 'dark:bg-purple-800', 'dark:bg-pink-800', 'dark:bg-indigo-800', 'dark:bg-red-800',
  ];

  // 로딩/에러/없음 처리
  if (loading) return (
    <div className="flex flex-col items-center justify-center h-64 text-gray-500 dark:text-gray-400">
      <ArrowPathIcon className="animate-spin w-8 h-8 mb-2" aria-label="로딩 중" />
      <span>로딩 중...</span>
    </div>
  );
  if (error) return (
    <div className="flex flex-col items-center justify-center h-64 text-red-500">
      <ExclamationTriangleIcon className="w-8 h-8 mb-2" aria-label="에러" />
      <span>{error}</span>
    </div>
  );
  if (!prompt) return (
    <div className="flex flex-col items-center justify-center h-64 text-gray-400">
      <ExclamationTriangleIcon className="w-8 h-8 mb-2" aria-label="프롬프트 없음" />
      <span>프롬프트를 찾을 수 없습니다.</span>
    </div>
  );

  return (
    <section className="w-full bg-white dark:bg-gray-800 rounded-2xl shadow-lg border border-gray-200 dark:border-gray-700 p-8 md:p-12 relative animate-fade-in">
      {/* 상단: 뒤로가기 */}
      <button
        className="flex items-center gap-2 text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700 px-3 py-2 rounded transition absolute left-4 top-4 md:static md:mb-6"
        onClick={onBack}
        aria-label="목록으로"
      >
        <ArrowLeftIcon className="w-5 h-5" />
        <span className="font-medium">목록으로</span>
      </button>

      {/* 제목 */}
      <h1 className="text-3xl font-bold mt-12 md:mt-0 mb-2 text-gray-900 dark:text-white break-words">{prompt.title}</h1>
      {/* 설명 */}
      <div className="mb-3 text-lg text-gray-500 dark:text-gray-300">{prompt.description}</div>
      {/* 카테고리 */}
      <div className="inline-flex items-center gap-1 mb-4 px-3 py-1 rounded-full bg-gray-100 dark:bg-gray-700 text-sm text-gray-700 dark:text-gray-200 font-medium">
        <FolderOpenIcon className="w-4 h-4 mr-1 text-gray-400" />
        {prompt.category}
      </div>
      {/* 태그 */}
      <div className="flex flex-wrap gap-2 mb-4">
        {(Array.isArray(prompt.tags) ? prompt.tags : []).map((tag: any, idx: number) => {
          let tagName = '';
          if (typeof tag === 'string') tagName = tag;
          else if (tag && typeof tag === 'object' && ('name' in tag || 'id' in tag)) tagName = tag.name ?? tag.id;
          if (!tagName) return null;
          const color = tagColors[idx % tagColors.length];
          return (
            <span
              key={tagName}
              className={`flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${color} text-gray-700 dark:text-gray-100`}
            >
              <TagIcon className="w-4 h-4 text-gray-400" />#{tagName}
            </span>
          );
        })}
      </div>
      {/* 본문 + 복사 버튼 */}
      <div className="relative mb-6">
        <div className="prose dark:prose-invert max-w-none whitespace-pre-wrap break-words border rounded-lg p-8 bg-gray-50 dark:bg-gray-900 text-gray-800 dark:text-gray-100 text-lg md:text-xl leading-relaxed overflow-x-auto">
          <ReactMarkdown
            remarkPlugins={[remarkGfm]}
            rehypePlugins={[rehypeHighlight]}
          >
            {prompt.content}
          </ReactMarkdown>
        </div>
        <button
          className="absolute top-2 right-2 flex items-center gap-1 px-2 py-1 bg-gray-200 dark:bg-gray-700 hover:bg-gray-300 dark:hover:bg-gray-600 rounded text-xs text-gray-700 dark:text-gray-100 transition"
          onClick={handleCopy}
          aria-label="프롬프트 복사"
        >
          <ClipboardIcon className="w-4 h-4" /> 복사
        </button>
        {copied && (
          <span className="absolute top-2 right-20 bg-green-500 text-white text-xs px-2 py-1 rounded shadow animate-fade-in">복사됨!</span>
        )}
      </div>
      {/* 하단 정보 */}
      <hr className="my-4 border-gray-200 dark:border-gray-700" />
      <div className="flex flex-wrap gap-4 text-xs text-gray-400 dark:text-gray-500 items-center">
        <span className="flex items-center gap-1"><UserIcon className="w-4 h-4" />작성자: {prompt.author?.name}</span>
        <span className="flex items-center gap-1"><CalendarIcon className="w-4 h-4" />생성일: {new Date(prompt.createdAt).toLocaleDateString()}</span>
        <span className="flex items-center gap-1"><CodeBracketIcon className="w-4 h-4" />버전: {prompt.version}</span>
        <span className="flex items-center gap-1 text-pink-500 ml-auto">
          <HeartIcon className="w-4 h-4" />{prompt.favoriteCount}
        </span>
        <span className="flex items-center gap-1 text-blue-500">
          <ChartBarIcon className="w-4 h-4" />{prompt.viewCount}
        </span>
      </div>
    </section>
  );
};

export default PromptDetail;

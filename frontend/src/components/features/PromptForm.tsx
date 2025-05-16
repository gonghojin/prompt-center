'use client';

import React, { useState } from 'react';
import { Prompt } from '@/types/prompt';

interface PromptFormProps {
  mode: 'new' | 'edit';
  id?: string;
}

export function PromptForm({ mode, id }: PromptFormProps) {
  const [formData, setFormData] = useState<Partial<Prompt>>({
    title: '',
    content: '',
    description: '',
    category: '',
    tags: [],
    isPublic: true,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // TODO: API 연동 (등록/수정)
    console.log('Form submitted:', formData);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="title" className="block text-sm font-medium text-gray-700">제목</label>
        <input
          type="text"
          id="title"
          name="title"
          value={formData.title}
          onChange={handleChange}
          className="mt-1 block w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div>
        <label htmlFor="description" className="block text-sm font-medium text-gray-700">설명</label>
        <input
          type="text"
          id="description"
          name="description"
          value={formData.description}
          onChange={handleChange}
          className="mt-1 block w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div>
        <label htmlFor="category" className="block text-sm font-medium text-gray-700">카테고리</label>
        <input
          type="text"
          id="category"
          name="category"
          value={formData.category}
          onChange={handleChange}
          className="mt-1 block w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div>
        <label htmlFor="content" className="block text-sm font-medium text-gray-700">내용</label>
        <textarea
          id="content"
          name="content"
          value={formData.content}
          onChange={handleChange}
          rows={5}
          className="mt-1 block w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          required
        />
      </div>
      <div>
        <label htmlFor="tags" className="block text-sm font-medium text-gray-700">태그 (쉼표로 구분)</label>
        <input
          type="text"
          id="tags"
          name="tags"
          value={formData.tags?.join(', ')}
          onChange={(e) => setFormData((prev) => ({ ...prev, tags: e.target.value.split(',').map((tag) => tag.trim()) }))}
          className="mt-1 block w-full border rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div>
        <label className="flex items-center">
          <input
            type="checkbox"
            name="isPublic"
            checked={formData.isPublic}
            onChange={(e) => setFormData((prev) => ({ ...prev, isPublic: e.target.checked }))}
            className="mr-2"
          />
          <span className="text-sm font-medium text-gray-700">공개</span>
        </label>
      </div>
      <button
        type="submit"
        className="w-full bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        {mode === 'new' ? '등록' : '수정'}
      </button>
    </form>
  );
}

// 호환성을 위한 default export
export default PromptForm;

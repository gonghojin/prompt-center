'use client';

import { Prompt } from '@/types/prompt';
import Link from 'next/link';

interface PromptCardProps {
  prompt: Prompt;
}

export function PromptCard({ prompt }: PromptCardProps) {
  return (
    <Link
      href={`/prompts/${prompt.id}`}
      className="card p-6 block hover:shadow-lg transition-shadow duration-200 bg-white dark:bg-gray-800 rounded-xl border"
    >
      <h3 className="text-xl font-semibold mb-2 text-gray-800 dark:text-gray-200">{prompt.title}</h3>
      <p className="text-gray-600 dark:text-gray-400 mb-4 line-clamp-2">{prompt.description}</p>
      <div className="flex flex-wrap gap-2 mb-4">
        {prompt.tags.map((tag) => (
          <span
            key={tag}
            className="bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 text-xs px-2 py-1 rounded-full"
          >
            #{tag}
          </span>
        ))}
      </div>
      <div className="flex justify-between items-center text-sm text-gray-500 dark:text-gray-400">
        <span>{prompt.author.username}</span>
        <span>{new Date(prompt.updatedAt).toLocaleDateString()}</span>
      </div>
    </Link>
  );
}

import { Prompt } from '@/types/prompt';

interface PromptDetailProps {
  prompt: Prompt;
}

export function PromptDetail({ prompt }: PromptDetailProps) {
  return (
    <div className="bg-white rounded-lg shadow-md p-8">
      <h1 className="text-3xl font-bold mb-4">{prompt.title}</h1>
      <p className="text-lg text-gray-700 mb-4">{prompt.description}</p>
      <div className="flex flex-wrap gap-2 mb-4">
        {prompt.tags.map(tag => (
          <span
            key={tag}
            className="bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200 text-xs px-2 py-1 rounded-full"
          >
            #{tag}
          </span>
        ))}
      </div>
      <div className="text-sm text-gray-500 mb-6 flex justify-between">
        <span>{prompt.author.username}</span>
        <span>{new Date(prompt.updatedAt).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
      </div>
      <div className="prose max-w-none text-gray-900">
        {prompt.content}
      </div>
    </div>
  );
}

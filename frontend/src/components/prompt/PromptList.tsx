import { Prompt } from '@/types/prompt';
import Link from 'next/link';

interface PromptListProps {
  prompts: Prompt[];
}

export default function PromptList({ prompts }: PromptListProps) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {prompts.map((prompt) => (
        <Link 
          href={`/prompts/${prompt.id}`} 
          key={prompt.id}
          className="card p-6"
        >
          <h3 className="text-xl font-semibold mb-2 text-gray-800">{prompt.title}</h3>
          <p className="text-gray-600 mb-4 line-clamp-2">{prompt.description}</p>
          <div className="flex flex-wrap gap-2 mb-4">
            {prompt.tags.map((tag) => (
              <span 
                key={tag} 
                className="tag"
              >
                {tag}
              </span>
            ))}
          </div>
          <div className="flex justify-between items-center text-sm text-gray-500">
            <span>{prompt.author.username}</span>
            <span>{new Date(prompt.updatedAt).toLocaleDateString()}</span>
          </div>
        </Link>
      ))}
    </div>
  );
} 
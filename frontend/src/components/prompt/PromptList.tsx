import { Prompt } from '@/types/prompt';
import { PromptCard } from '@/components/prompt/PromptCard';

interface PromptListProps {
  prompts: Prompt[];
}

export function PromptList({ prompts }: PromptListProps) {
  if (prompts.length === 0) {
    return (
      <div className="text-center py-10">
        <p className="text-gray-500 dark:text-gray-400">등록된 프롬프트가 없습니다.</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      {prompts.map((prompt) => (
        <PromptCard key={prompt.id} prompt={prompt} />
      ))}
    </div>
  );
}

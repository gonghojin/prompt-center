import React from 'react';
import { PromptDetail } from '@/components/prompt/PromptDetail';
import { mockPrompts } from '@/mocks/mockPrompts';
import { notFound } from 'next/navigation';

interface PromptDetailPageProps {
  params: { id: string };
}

const PromptDetailPage: React.FC<PromptDetailPageProps> = ({ params }) => {
  const prompt = mockPrompts.find(p => p.id === Number(params.id));

  if (!prompt) {
    notFound();
  }

  return (
    <main className="max-w-3xl mx-auto py-8 px-4">
      <PromptDetail prompt={prompt} />
    </main>
  );
};

export default PromptDetailPage;

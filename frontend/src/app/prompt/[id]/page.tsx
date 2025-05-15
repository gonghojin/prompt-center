import React from 'react';
import PromptDetail from '@/components/features/PromptDetail';

interface PromptDetailPageProps {
  params: { id: string };
}

const PromptDetailPage: React.FC<PromptDetailPageProps> = ({ params }) => {
  return (
    <main className="max-w-3xl mx-auto py-8 px-4">
      <PromptDetail id={params.id} />
    </main>
  );
};

export default PromptDetailPage;

import React from 'react';
import PromptForm from '@/components/features/PromptForm';

interface EditPromptPageProps {
  params: { id: string };
}

const EditPromptPage: React.FC<EditPromptPageProps> = ({ params }) => {
  return (
    <main className="max-w-3xl mx-auto py-8 px-4">
      <h1 className="text-3xl font-bold mb-4">프롬프트 수정</h1>
      <PromptForm mode="edit" id={params.id} />
    </main>
  );
};

export default EditPromptPage; 
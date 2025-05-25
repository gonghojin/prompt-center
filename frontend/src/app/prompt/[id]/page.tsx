import React from 'react';
import PromptDetail from '@/components/features/PromptDetail';
import { useRouter } from 'next/navigation';

interface PromptDetailPageProps {
  params: { id: string };
}

const PromptDetailPage: React.FC<PromptDetailPageProps> = ({ params }) => {
  const router = useRouter();
  return (
    <main className="max-w-3xl mx-auto py-8 px-4">
      <PromptDetail id={params.id} onBack={() => router.back()} />
    </main>
  );
};

export default PromptDetailPage;

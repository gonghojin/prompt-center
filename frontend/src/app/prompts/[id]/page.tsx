import { mockPrompts } from '@/mocks/mockPrompts';
import { notFound } from 'next/navigation';
import Link from 'next/link';
import { PromptDetail } from '@/components/prompt/PromptDetail';

interface PromptDetailPageProps {
  params: { id: string };
}

export default function PromptDetailPage({ params }: PromptDetailPageProps) {
  const prompt = mockPrompts.find(p => p.id === Number(params.id));

  if (!prompt) {
    notFound();
  }

  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-6">
          <Link href="/" className="text-sm text-blue-600 hover:text-blue-800 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
            <span>메인으로</span>
          </Link>
        </div>

        <PromptDetail prompt={prompt} />
      </div>
    </div>
  );
}

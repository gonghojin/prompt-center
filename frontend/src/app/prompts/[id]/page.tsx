import { mockPrompts } from '@/mocks/mockPrompts';
import { notFound } from 'next/navigation';
import Link from 'next/link';

interface PromptDetailPageProps {
  params: { id: string };
}

export default function PromptDetailPage({ params }: PromptDetailPageProps) {
  const prompt = mockPrompts.find(p => p.id === Number(params.id));

  if (!prompt) {
    return (
      <div className="max-w-xl mx-auto py-20 text-center">
        <h2 className="text-2xl font-bold mb-4">프롬프트를 찾을 수 없습니다.</h2>
        <Link href="/" className="text-blue-600 underline">메인으로 돌아가기</Link>
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50">
      <div className="max-w-2xl mx-auto px-4 py-12">
        <Link href="/" className="text-sm text-blue-600 underline mb-8 inline-block">← 메인으로</Link>
        <div className="bg-white rounded-lg shadow-md p-8">
          <h1 className="text-3xl font-bold mb-4">{prompt.title}</h1>
          <p className="text-lg text-gray-700 mb-4">{prompt.description}</p>
          <div className="flex flex-wrap gap-2 mb-4">
            {prompt.tags.map(tag => (
              <span key={tag} className="tag">{tag}</span>
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
      </div>
    </main>
  );
} 
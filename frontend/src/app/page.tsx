import PromptPage from '@/components/features/PromptPage';

export default function Home() {
  return (
    <main className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="relative flex flex-col items-center mb-8">
          <span className="absolute -top-8 left-1/2 -translate-x-1/2 text-4xl select-none" aria-hidden="true">💡</span>
          <h1 className="text-4xl font-bold text-center bg-gradient-to-r from-blue-500 to-purple-500 text-transparent bg-clip-text">
            Prompt Center
          </h1>
          <p className="text-center text-lg mt-4 bg-gray-100 rounded-lg px-4 py-2 text-blue-700 shadow-sm">
            프롬프트 템플릿 중앙화 서버
          </p>
        </div>

        <div className="mb-8 bg-white rounded-xl shadow p-6">
          <div className="flex items-center mb-4">
            <span className="text-2xl mr-2" aria-hidden="true">🕒</span>
            <h2 className="text-2xl font-semibold text-gray-800">최근 프롬프트</h2>
          </div>
          <div className="h-1 w-16 bg-gradient-to-r from-blue-400 to-purple-400 rounded mb-6" />
          <PromptPage />
        </div>
      </div>
    </main>
  );
}

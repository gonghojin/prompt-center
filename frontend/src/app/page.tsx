import PromptList from '@/components/prompt/PromptList';

// 임시 데이터 (나중에 API 연동으로 대체)
const mockPrompts = [
  {
    id: 1,
    title: "ChatGPT 프롬프트 템플릿",
    description: "ChatGPT를 위한 효과적인 프롬프트 작성 가이드",
    content: "당신은 전문가입니다...",
    category: "AI",
    tags: ["ChatGPT", "AI", "프롬프트"],
    createdAt: "2024-03-15T00:00:00Z",
    updatedAt: "2024-03-15T00:00:00Z",
    author: {
      id: 1,
      username: "admin"
    }
  },
  {
    id: 2,
    title: "Claude 프롬프트 템플릿",
    description: "Claude AI를 위한 최적화된 프롬프트 모음",
    content: "당신은 Claude AI입니다...",
    category: "AI",
    tags: ["Claude", "AI", "프롬프트"],
    createdAt: "2024-03-15T00:00:00Z",
    updatedAt: "2024-03-15T00:00:00Z",
    author: {
      id: 1,
      username: "admin"
    }
  }
];

export default function Home() {
  return (
    <main className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <h1 className="text-4xl font-bold text-center mb-8 text-gray-900">
          Prompt Center
        </h1>
        <p className="text-center text-lg mb-12 text-gray-600">
          프롬프트 템플릿 중앙화 서버
        </p>
        
        <div className="mb-8">
          <h2 className="text-2xl font-semibold mb-6 text-gray-800">최근 프롬프트</h2>
          <PromptList prompts={mockPrompts} />
        </div>
      </div>
    </main>
  );
} 
import { PromptList } from '@/components/prompt/PromptList';
import { mockPrompts } from '@/mocks/mockPrompts';
import Link from 'next/link';

export default function Home() {
  return (
    <div className="bg-gray-50">
      <section className="py-16 sm:py-24 lg:py-32 bg-gradient-to-b from-white to-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl font-extrabold tracking-tight text-gray-900 sm:text-5xl md:text-6xl">
            <span className="block">Prompt Center</span>
            <span className="block text-blue-600 mt-2">프롬프트 템플릿 관리 플랫폼</span>
          </h1>
          <p className="mt-6 max-w-lg mx-auto text-xl text-gray-500 sm:max-w-3xl">
            효과적인 프롬프트 템플릿을 찾고, 공유하고, 관리하세요.
            AI 모델과의 상호작용을 위한 최적의 프롬프트 컬렉션을 제공합니다.
          </p>
          <div className="mt-10 max-w-sm mx-auto sm:max-w-none sm:flex sm:justify-center">
            <div className="space-y-4 sm:space-y-0 sm:mx-auto sm:inline-grid sm:grid-cols-2 sm:gap-5">
              <Link
                href="/prompts"
                className="flex items-center justify-center px-4 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 sm:px-8"
              >
                프롬프트 둘러보기
              </Link>
              <Link
                href="/auth/login"
                className="flex items-center justify-center px-4 py-3 border border-gray-300 text-base font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 sm:px-8"
              >
                시작하기
              </Link>
            </div>
          </div>
        </div>
      </section>

      <section className="py-16 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="lg:text-center mb-12">
            <h2 className="text-base text-blue-600 font-semibold tracking-wide uppercase">최신 프롬프트</h2>
            <p className="mt-2 text-3xl leading-8 font-extrabold tracking-tight text-gray-900 sm:text-4xl">
              인기 있는 프롬프트 모음
            </p>
            <p className="mt-4 max-w-2xl text-xl text-gray-500 lg:mx-auto">
              사용자들이 가장 많이 활용하는 프롬프트 템플릿들을 확인하세요.
            </p>
          </div>

          <PromptList prompts={mockPrompts} />

          <div className="mt-12 text-center">
            <Link
              href="/prompts"
              className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
            >
              모든 프롬프트 보기
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}

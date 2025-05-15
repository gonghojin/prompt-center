import { Prompt } from '@/types/prompt';

export const mockPrompts: Prompt[] = [
  {
    id: 1,
    title: "ChatGPT 프롬프트 템플릿",
    description: "ChatGPT를 위한 효과적인 프롬프트 작성 가이드",
    content: "당신은 전문가입니다. 아래의 조건을 참고하여 답변을 작성하세요...",
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
    content: "당신은 Claude AI입니다. 아래의 조건을 참고하여 답변을 작성하세요...",
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
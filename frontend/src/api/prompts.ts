import { PromptTemplate } from '@/types';

export async function fetchPrompts(): Promise<PromptTemplate[]> {
  const res = await fetch('/api/v1/prompts');
  if (!res.ok) throw new Error('프롬프트 목록을 불러오지 못했습니다.');
  return res.json();
}

export async function fetchPromptDetail(id: string): Promise<PromptTemplate> {
  const res = await fetch(`/api/v1/prompts/${id}`);
  if (!res.ok) throw new Error('프롬프트 상세 정보를 불러오지 못했습니다.');
  return res.json();
}

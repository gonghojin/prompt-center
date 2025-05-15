export interface Prompt {
  id: number;
  title: string;
  description: string;
  content: string;
  category: string;
  tags: string[];
  createdAt: string;
  updatedAt: string;
  author: {
    id: number;
    username: string;
  };
}

export interface PromptListResponse {
  prompts: Prompt[];
  total: number;
  page: number;
  size: number;
} 
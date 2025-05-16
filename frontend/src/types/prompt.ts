import { User } from './index';

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
  version?: number;
  isPublic?: boolean;
}

export interface PromptListResponse {
  prompts: Prompt[];
  total: number;
  page: number;
  size: number;
}

// Export the old PromptTemplate type for backwards compatibility
// This will help maintain backward compatibility while we transition to the new Prompt type
export type PromptTemplate = Prompt;

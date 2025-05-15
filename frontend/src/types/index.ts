export interface User {
  id: string;
  email: string;
  name: string;
  role: 'ADMIN' | 'USER';
}

export interface PromptTemplate {
  id: string;
  title: string;
  content: string;
  description: string;
  category: string;
  tags: string[];
  author: User;
  createdAt: string;
  updatedAt: string;
  version: number;
  isPublic: boolean;
}

export interface Category {
  id: string;
  name: string;
  description: string;
}

export interface Tag {
  id: string;
  name: string;
} 
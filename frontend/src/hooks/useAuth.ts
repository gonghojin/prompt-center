import { useState, useEffect } from 'react';
import { User } from '@/types';

export function useAuth() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // TODO: Implement authentication check
    setLoading(false);
  }, []);

  const login = async (email: string, password: string) => {
    // TODO: Implement login
  };

  const logout = async () => {
    // TODO: Implement logout
  };

  return {
    user,
    loading,
    login,
    logout,
  };
} 
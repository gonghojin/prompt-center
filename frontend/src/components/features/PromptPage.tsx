'use client';

import React, { useState } from 'react';
import PromptList from './PromptList';
import PromptDetail from './PromptDetail';

const PromptPage: React.FC = () => {
  const [selectedPromptId, setSelectedPromptId] = useState<string | null>(null);

  return (
    <div>
      {selectedPromptId === null ? (
        <PromptList onSelect={setSelectedPromptId} />
      ) : (
        <PromptDetail id={selectedPromptId} onBack={() => setSelectedPromptId(null)} />
      )}
    </div>
  );
};

export default PromptPage;

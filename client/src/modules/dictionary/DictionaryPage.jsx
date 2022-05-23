import React from 'react';
import { withLayout } from '../common';
import KanjiContainer from './KanjiContainer';

const DictionaryPage = () => {
  return (
    <div>
      <KanjiContainer />
    </div>
  );
};

export default withLayout(DictionaryPage);

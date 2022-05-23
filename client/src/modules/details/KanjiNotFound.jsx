import React from 'react';
import NotFoundComponent from '../common/NotFoundComponent';

const props = {
  upperText: 'まよちゃった・・・',
  text: 'Kanji not found',
};

const KanjiNotFound = () => <NotFoundComponent {...props} />;

export default KanjiNotFound;

import React from 'react';
import { withLayout } from '../common';
import KanjiDetailsContainer from './KanjiDetailsContainer';

const DetailsPage = ({ computedMatch }) => {
  return <KanjiDetailsContainer literal={computedMatch.params.literal} />;
};

export default withLayout(DetailsPage);

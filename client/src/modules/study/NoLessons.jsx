import React from 'react';
import NotFoundComponent from '../common/NotFoundComponent';

const props = {
  upperText: '(－_－) zzZ',
  text: 'You have no new lessons!',
};

const NoLessons = () => <NotFoundComponent {...props} />;

export default NoLessons;

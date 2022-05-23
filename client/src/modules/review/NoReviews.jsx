import React from 'react';
import NotFoundComponent from '../common/NotFoundComponent';

const props = {
  upperText: '(－_－) zzZ',
  text: 'You have no reviews!',
};

const NoReviews = () => <NotFoundComponent {...props} />;

export default NoReviews;

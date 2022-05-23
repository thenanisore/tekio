import React from 'react';
import NotFoundComponent from '../common/NotFoundComponent';

const props = {
  upperText: '(҂⌣̀_⌣́)',
  text: 'User not found.',
};

const UserNotFound = () => <NotFoundComponent {...props} />;

export default UserNotFound;

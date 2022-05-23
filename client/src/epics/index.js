import { combineEpics } from 'redux-observable';
import auth from './auth';
import kanji from './kanji';
import review from './review';
import signup from './signup';
import user from './user';
import profile from './profile';
import study from './study';

export const rootEpic = combineEpics.apply(null, [
  ...auth,
  ...kanji,
  ...review,
  ...signup,
  ...user,
  ...profile,
  ...study,
]);

import auth from './auth';
import signup from './signup';
import user from './user';
import kanji from './kanji';
import review from './review';
import study from './study';
import profile from './profile';
import { connectRouter } from 'connected-react-router';
import { combineReducers } from 'redux';

export default (history) =>
  combineReducers({
    auth,
    signup,
    user,
    kanji,
    review,
    study,
    profile,
    router: connectRouter(history),
  });

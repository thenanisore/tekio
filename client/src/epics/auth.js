import { push } from 'connected-react-router';
import { ofType } from 'redux-observable';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap, tap } from 'rxjs/operators';
import * as auth from '../actions/auth';
import api from '../utils/api';
import { clearToken, getTokenData, setToken } from '../utils/auth';
import { clearPersistedReviews } from '../utils/review';

const loginEpic = (action$) =>
  action$.pipe(
    ofType(auth.LOGIN_REQUEST),
    mergeMap((action) =>
      from(api.user.login(action.payload)).pipe(
        map((response) => auth.tokenReceived(response.token)),
        catchError((error) => of(auth.loginFailure(error))),
      ),
    ),
  );

const tokenReceivedEpic = (action$) =>
  action$.pipe(
    ofType(auth.TOKEN_RECEIVED),
    map((action) => {
      try {
        const token = action.payload;
        const tokenData = getTokenData(token);
        setToken(token);
        return auth.loginSuccess(tokenData);
      } catch (error) {
        console.log(error);
        clearToken();
        return auth.loginFailure({ info: 'Invalid token.' });
      }
    }),
  );

const logoutEpic = (action$) =>
  action$.pipe(
    ofType(auth.LOGOUT),
    tap((action) => {
      clearToken();
      clearPersistedReviews();
    }),
    map((action) =>
      push(action.toLogin ? '/login' : '/welcome', { from: action.from }),
    ),
  );

const epics = [loginEpic, tokenReceivedEpic, logoutEpic];

export default epics;

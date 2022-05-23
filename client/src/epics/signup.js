import { ofType } from 'redux-observable';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import * as auth from '../actions/auth';
import * as signup from '../actions/signup';
import api from '../utils/api';

const signupEpic = (action$) =>
  action$.pipe(
    ofType(signup.SIGNUP_REQUEST),
    mergeMap((action) =>
      from(api.user.signup(action.payload)).pipe(
        map((response) => signup.signupSuccess(response.token)),
        catchError((error) => of(signup.signupFailure(error))),
      ),
    ),
  );

const loginOnSignupEpic = (action$) =>
  action$.pipe(
    ofType(signup.SIGNUP_SUCCESS),
    map((action) => auth.tokenReceived(action.payload)),
  );

const epics = [signupEpic, loginOnSignupEpic];

export default epics;

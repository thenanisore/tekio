import { ofType } from 'redux-observable';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import * as user from '../actions/user';
import api from '../utils/api';

const fetchUserEpic = (action$) =>
  action$.pipe(
    ofType(user.FETCH_USER_REQUEST),
    mergeMap((action) =>
      from(api.user.getUser(action.token)).pipe(
        map((response) => user.fetchUserSuccess(response)),
        catchError((error) => of(user.fetchUserFailure(error))),
      ),
    ),
  );

const putSettingsEpic = (action$) =>
  action$.pipe(
    ofType(user.PUT_SETTINGS_REQUEST),
    mergeMap((action) =>
      from(api.user.putSettings(action.settings, action.token)).pipe(
        map((response) => user.putSettingsSuccess(response)),
        catchError((error) => of(user.putSettingsFailure(error))),
      ),
    ),
  );

const epics = [fetchUserEpic, putSettingsEpic];

export default epics;

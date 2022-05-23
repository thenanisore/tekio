import { ofType } from 'redux-observable';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import * as profile from '../actions/profile';
import api from '../utils/api';

const fetchProfileEpic = (action$) =>
  action$.pipe(
    ofType(profile.FETCH_PROFILE_REQUEST),
    mergeMap((action) =>
      from(api.user.getProfile(action.id)).pipe(
        map((response) => profile.fetchProfileSuccess(response)),
        catchError((error) => of(profile.fetchProfileFailure(error))),
      ),
    ),
  );

const putProfileEpic = (action$) =>
  action$.pipe(
    ofType(profile.PUT_PROFILE_REQUEST),
    mergeMap((action) =>
      from(api.user.putProfile(action.profile, action.token)).pipe(
        map((response) => profile.putProfileSuccess(response)),
        catchError((error) => of(profile.putProfileFailure(error))),
      ),
    ),
  );

const epics = [fetchProfileEpic, putProfileEpic];

export default epics;

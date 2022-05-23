import { ofType } from 'redux-observable';
import { map, catchError, mergeMap } from 'rxjs/operators';
import { from, of } from 'rxjs';
import * as kanji from '../actions/kanji';
import * as study from '../actions/study';
import api from '../utils/api';

const prepareLessonsEpic = (action$) =>
  action$.pipe(
    ofType(study.PREPARE_LESSONS_REQUEST),
    map((action) => kanji.fetchKanjiFullByIdsRequest(action.ids, action.token)),
  );

const prepareLessonsSuccessEpic = (action$) =>
  action$.pipe(
    ofType(kanji.FETCH_KANJI_FULL_SUCCESS),
    map((action) => study.prepareLessonsSuccess(action.payload)),
  );

const uploadLessonsEpic = (action$) =>
  action$.pipe(
    ofType(study.UPLOAD_LESSONS_REQUEST),
    mergeMap((action) => {
      return from(api.user.postLessons(action.ids, action.spent, action.token)).pipe(
        map((response) => study.uploadLessonsSuccess(response)),
        catchError((error) => of(study.uploadLessonsFailure(error))),
      );
    }),
  );

const epics = [
  prepareLessonsEpic,
  prepareLessonsSuccessEpic,
  uploadLessonsEpic,
];

export default epics;

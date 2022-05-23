import { ofType } from 'redux-observable';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import * as kanji from '../actions/kanji';
import api from '../utils/api';

const fetchKanjiShortEpic = (action$) =>
  action$.pipe(
    ofType(kanji.FETCH_KANJI_LIST_REQUEST),
    mergeMap((action) => {
      return from(api.kanji.getShortList()).pipe(
        map((response) => kanji.fetchKanjiListSuccess(response)),
        catchError((error) => of(kanji.fetchKanjiListFailure(error))),
      );
    }),
  );

const fetchKanjiFullEpic = (action$) =>
  action$.pipe(
    ofType(kanji.FETCH_KANJI_FULL_REQUEST),
    mergeMap((action) => {
      let promise;
      if (action.ids) {
        // get by ids
        promise = api.user.getUserKanji(action.ids, action.token);
      } else if (action.literal) {
        // get by literal
        promise = api.user.getUserKanjiByLiteral(action.literal, action.token);
      }
      return from(promise).pipe(
        map((response) => kanji.fetchKanjiFullSuccess(response)),
        catchError((error) => of(kanji.fetchKanjiFullFailure(error))),
      );
    }),
  );

const unlockKanjiEpic = (action$) =>
  action$.pipe(
    ofType(kanji.UNLOCK_KANJI_REQUEST),
    mergeMap((action) => {
      return from(api.user.unlockKanji(action.id, action.token)).pipe(
        map((response) => kanji.unlockKanjiSuccess(response)),
        catchError((error) => of(kanji.unlockKanjiFailure(error))),
      );
    }),
  );

const epics = [fetchKanjiShortEpic, fetchKanjiFullEpic, unlockKanjiEpic];

export default epics;

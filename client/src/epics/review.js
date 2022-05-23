import { ofType } from 'redux-observable';
import { map, catchError, mergeMap } from 'rxjs/operators';
import { from, of } from 'rxjs';
import * as kanji from '../actions/kanji';
import * as review from '../actions/review';
import api from '../utils/api';
import { hasPersistedReviews, getPersistedReviews } from '../utils/review';

const prepareReviewEpic = (action$) =>
  action$.pipe(
    ofType(review.PREPARE_REVIEW_REQUEST),
    map((action) => kanji.fetchKanjiFullByIdsRequest(action.ids, action.token)),
  );

const prepareReviewSuccessEpic = (action$) =>
  action$.pipe(
    ofType(kanji.FETCH_KANJI_FULL_SUCCESS),
    map((action) => review.prepareReviewSuccess(action.payload)),
  );

const uploadAnswersEpic = (action$) =>
  action$.pipe(
    ofType(review.UPLOAD_ANSWERS_REQUEST),
    mergeMap((action) => {
      return from(api.user.postAnswers(action.answers, action.token)).pipe(
        map((response) => review.uploadAnswersSuccess(response)),
        catchError((error) => of(review.uploadAnswersFailure(error))),
      );
    }),
  );

const rehydrateReviewsEpic = (action$) =>
  action$.pipe(
    ofType(review.REHYDRATE_REVIEWS_REQUEST),
    map(() => {
      if (hasPersistedReviews()) {
        const state = getPersistedReviews();
        return review.rehydrateReviewsSuccess(state);
      } else {
        return review.rehydrateReviewsFailure();
      }
    }),
  );

const epics = [
  prepareReviewEpic,
  prepareReviewSuccessEpic,
  uploadAnswersEpic,
  rehydrateReviewsEpic,
];

export default epics;

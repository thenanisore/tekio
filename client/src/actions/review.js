export const START_REVIEW = '@@review/START_REVIEW';
export const PREPARE_REVIEW_REQUEST = '@@review/PREPARE_REVIEW_REQUEST';
export const PREPARE_REVIEW_SUCCESS = '@@review/PREPARE_REVIEW_SUCCESS';
export const SUBMIT_ANSWER = '@@review/SUBMIT_ANSWER';
export const UNDO_ANSWER = '@@review/UNDO_ANSWER';
export const FINISH_REVIEW = '@@review/FINISH_REVIEW';
export const UPLOAD_ANSWERS_REQUEST = '@@review/UPLOAD_ANSWERS_REQUEST';
export const UPLOAD_ANSWERS_SUCCESS = '@@review/UPLOAD_ANSWERS_SUCCESS';
export const UPLOAD_ANSWERS_FAILURE = '@@review/UPLOAD_ANSWERS_FAILURE';
export const STOP_LOADING = '@@review/STOP_LOADING';
export const REHYDRATE_REVIEWS_REQUEST = '@@review/REHYDRATE_REVIEWS_REQUEST';
export const REHYDRATE_REVIEWS_SUCCESS = '@@review/REHYDRATE_REVIEWS_SUCCESS';
export const REHYDRATE_REVIEWS_FAILURE = '@@review/REHYDRATE_REVIEWS_FAILURE';

export const startReview = (queue) => ({
  type: START_REVIEW,
  queue,
});

export const prepareReviewRequest = (ids, token) => ({
  type: PREPARE_REVIEW_REQUEST,
  ids,
  token,
});

export const prepareReviewSuccess = (payload) => ({
  type: PREPARE_REVIEW_SUCCESS,
  payload,
});

export const submitAnswer = (answer) => ({
  type: SUBMIT_ANSWER,
  answer,
});

export const undoAnswer = (answer) => ({
  type: UNDO_ANSWER,
  answer,
});

export const finishReview = () => ({
  type: FINISH_REVIEW,
});

export const uploadAnswersRequest = (answers, token) => ({
  type: UPLOAD_ANSWERS_REQUEST,
  answers,
  token,
});

export const uploadAnswersSuccess = (payload) => ({
  type: UPLOAD_ANSWERS_SUCCESS,
  payload,
});

export const uploadAnswersFailure = (payload) => ({
  type: UPLOAD_ANSWERS_FAILURE,
  payload,
});

export const stopLoading = () => ({
  type: STOP_LOADING,
});

export const rehydrateReviewsRequest = () => ({
  type: REHYDRATE_REVIEWS_REQUEST,
});

export const rehydrateReviewsSuccess = (state) => ({
  type: REHYDRATE_REVIEWS_SUCCESS,
  state,
});

export const rehydrateReviewsFailure = () => ({
  type: REHYDRATE_REVIEWS_FAILURE,
});

import * as auth from '../actions/auth';
import * as kanji from '../actions/kanji';
import * as profile from '../actions/profile';
import * as review from '../actions/review';
import * as study from '../actions/study';
import * as user from '../actions/user';

const initialState = {
  fetched: false,
  invalidate: false,
  loading: false,
  uploading: false,
  error: null,
};

export default (state = initialState, action) => {
  switch (action.type) {
    case user.FETCH_USER_REQUEST:
      return {
        fetched: false,
        invalidate: false,
        loading: true,
        uploading: false,
        error: null,
      };
    case user.FETCH_USER_SUCCESS:
      return {
        ...action.payload,
        fetched: true,
        invalidate: false,
        loading: false,
        uploading: false,
        error: null,
      };
    case user.FETCH_USER_FAILURE:
      return {
        fetched: false,
        invalidate: false,
        uploading: false,
        loading: true,
        error: {
          name: action.payload.name,
          code: action.payload.code,
          info: action.payload.message,
        },
      };
    case kanji.UNLOCK_KANJI_SUCCESS:
    case review.UPLOAD_ANSWERS_SUCCESS:
    case study.UPLOAD_LESSONS_SUCCESS:
      return {
        ...state,
        invalidate: true,
      };
    case user.PUT_SETTINGS_REQUEST:
      return {
        ...state,
        uploading: true,
      };
    case user.PUT_SETTINGS_SUCCESS:
      return {
        ...action.payload,
        uploading: false,
      };
    case user.PUT_SETTINGS_FAILURE:
      return {
        ...state,
        error: {
          name: action.payload.name,
          code: action.payload.code,
          info: action.payload.message,
        },
      };
    case profile.PUT_PROFILE_SUCCESS:
      return {
        ...state,
        username: action.payload.username,
        picture: action.payload.picture,
      };
    case auth.LOGOUT:
      return initialState;
    default:
      return state;
  }
};

export const getReviewCount = (state) => {
  return state.user.reviewQueue.length;
};

export const getLessonCount = (state) => {
  return state.user.lessonQueue.length;
};

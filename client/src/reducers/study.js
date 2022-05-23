import * as auth from '../actions/auth';
import * as study from '../actions/study';

const initialState = {
  kanjiInfo: {},
  learned: [],
  finished: false,
  uploading: false,
  uploaded: false,
  loading: false,
  error: null,
};

export default (state = initialState, action) => {
  switch (action.type) {
    case study.START_LESSONS:
      return {
        ...initialState,
        kanjiInfo: state.kanjiInfo,
      };
    case study.FINISH_LESSONS:
      return {
        ...state,
        finished: true,
      };
    case study.PREPARE_LESSONS_REQUEST:
      return {
        ...state,
        loading: true,
      };
    case study.PREPARE_LESSONS_SUCCESS:
      return {
        ...state,
        loading: false,
        kanjiInfo:
          state.learned.length > 0
            ? state.kanjiInfo
            : getKanjiInfoMap(action.payload.kanji),
      };
    case study.UPLOAD_LESSONS_REQUEST:
      return {
        ...state,
        learned: action.ids,
        uploading: true,
      };
    case study.UPLOAD_LESSONS_SUCCESS:
      return {
        ...state,
        uploading: false,
        uploaded: true,
      };
    case study.UPLOAD_LESSONS_FAILURE:
      return {
        ...state,
        uploading: false,
        uploaded: false,
        error: {
          name: action.payload.name,
          code: action.payload.code,
          info: action.payload.message,
        },
      };
    case auth.LOGOUT:
      return initialState;
    default:
      return state;
  }
};

const getKanjiInfoMap = (kanji) => {
  return kanji.reduce((acc, k) => {
    acc[k.id] = k;
    return acc;
  }, {});
};

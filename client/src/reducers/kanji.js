import * as auth from '../actions/auth';
import * as kanji from '../actions/kanji';
import * as review from '../actions/review';
import * as study from '../actions/study';

const initialState = {
  items: {}, // kanji_id -> kanji
  loading: false,
  isUnlocking: false,
  fetchedShort: false,
  error: null,
};

const upsertKanji = (newKanji, items) => {
  const newKanjiMap = newKanji.reduce((acc, k) => {
    const old = items[k.id];
    const updated = { ...old, ...k };
    acc[k.id] = updated;
    return acc;
  }, {});
  const updatedMap = { ...items, ...newKanjiMap };
  return updatedMap;
};

export default (state = initialState, action) => {
  switch (action.type) {
    case kanji.FETCH_KANJI_FULL_REQUEST:
    case kanji.FETCH_KANJI_LIST_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case kanji.UNLOCK_KANJI_SUCCESS:
    case kanji.FETCH_KANJI_FULL_SUCCESS:
    case kanji.FETCH_KANJI_LIST_SUCCESS:
    case review.UPLOAD_ANSWERS_SUCCESS:
    case study.UPLOAD_LESSONS_SUCCESS: {
      const updated = upsertKanji(action.payload.kanji, state.items);
      const isFetchedShort =
        state.fetchedShort || action.type === kanji.FETCH_KANJI_LIST_SUCCESS;
      return {
        items: updated,
        loading: false,
        isUnlocking: false,
        fetchedShort: isFetchedShort,
        error: null,
      };
    }
    case kanji.FETCH_KANJI_FULL_FAILURE:
    case kanji.FETCH_KANJI_LIST_FAILURE:
      return {
        ...state,
        loading: true,
        error: {
          name: action.payload.name,
          code: action.payload.code,
          info: action.payload.message,
        },
      };
    case kanji.UNLOCK_KANJI_REQUEST:
      return {
        ...state,
        isUnlocking: true,
      };
    case kanji.UNLOCK_KANJI_FAILURE:
      return {
        ...state,
        isUnlocking: false,
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

export const getFullByLiteral = (literal, state) => {
  return Object.values(state.kanji.items).find(
    (i) => i.literal === literal && i.full,
  );
};

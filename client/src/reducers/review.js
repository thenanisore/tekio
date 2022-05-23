import * as auth from '../actions/auth';
import * as review from '../actions/review';

const initialState = {
  answers: [],
  itemInfo: {},
  finished: false,
  uploading: false,
  uploaded: false,
  loading: false,
  rehydrated: false,
  error: null,
};

export default (state = initialState, action) => {
  switch (action.type) {
    case review.START_REVIEW:
      return {
        ...initialState,
        itemInfo: state.itemInfo,
      };
    case review.PREPARE_REVIEW_REQUEST:
      return {
        ...state,
        loading: true,
      };
    case review.PREPARE_REVIEW_SUCCESS:
      return {
        ...state,
        loading: false,
        itemInfo: getItemInfoMap(action.payload.kanji),
      };
    case review.SUBMIT_ANSWER:
      const updated = state.answers.concat([action.answer]);
      return {
        ...state,
        answers: updated,
      };
    case review.UNDO_ANSWER:
      return {
        ...state,
        answers: state.answers.slice(0, state.answers.length - 1),
      };
    case review.FINISH_REVIEW:
      return {
        ...state,
        finished: true,
      };
    case review.UPLOAD_ANSWERS_REQUEST:
      return {
        ...state,
        uploading: true,
      };
    case review.UPLOAD_ANSWERS_SUCCESS:
      return {
        ...state,
        uploading: false,
        uploaded: true,
      };
    case review.UPLOAD_ANSWERS_FAILURE:
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
    case review.STOP_LOADING:
      return {
        ...state,
        loading: false,
      };
    case review.REHYDRATE_REVIEWS_REQUEST:
      return {
        ...state,
      };
    case review.REHYDRATE_REVIEWS_SUCCESS:
      const reviewState = validatePersistedReviews(action.state, reviewTTL);
      return {
        ...state,
        ...reviewState,
        rehydrated: true,
      };
    case review.REHYDRATE_REVIEWS_FAILURE:
      return {
        ...state,
        rehydrated: true,
      };
    case auth.LOGOUT:
      return initialState;
    default:
      return state;
  }
};

const getItemInfoMap = (kanji) => {
  return kanji
    .flatMap((k) => k.items.map((i) => [i.id, k]))
    .reduce((acc, [itemId, kanji]) => {
      const item = kanji.items.find((i) => i.id === itemId);
      const updated = { ...kanji, item, items: undefined };
      acc[itemId] = updated;
      return acc;
    }, {});
};

const reviewTTL = 3600000;

const validatePersistedReviews = (rawState, reviewTTL) => {
  try {
    const persistedState = JSON.parse(rawState);
    const now = new Date().getTime();
    const delta = now - persistedState.persistedAt;
    if (delta > 0 && delta < reviewTTL && persistedState.state) {
      console.log('Successfully restored persisted reviews state.');
      return persistedState.state;
    } else {
      return {};
    }
  } catch (e) {
    console.log("Couldn't parse the persisted state: " + rawState);
    return {};
  }
};

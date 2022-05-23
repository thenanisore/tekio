export const FETCH_KANJI_LIST_REQUEST = '@@kanji/FETCH_KANJI_LIST_REQUEST';
export const FETCH_KANJI_LIST_SUCCESS = '@@kanji/FETCH_KANJI_LIST_SUCCESS';
export const FETCH_KANJI_LIST_FAILURE = '@@kanji/FETCH_KANJI_LIST_FAILURE';

export const fetchKanjiListRequest = () => ({
  type: FETCH_KANJI_LIST_REQUEST,
});

export const fetchKanjiListSuccess = (payload) => ({
  type: FETCH_KANJI_LIST_SUCCESS,
  payload,
});

export const fetchKanjiListFailure = (payload) => ({
  type: FETCH_KANJI_LIST_FAILURE,
  payload,
});

export const FETCH_KANJI_FULL_REQUEST = '@@kanji/FETCH_KANJI_FULL_REQUEST';
export const FETCH_KANJI_FULL_SUCCESS = '@@kanji/FETCH_KANJI_FULL_SUCCESS';
export const FETCH_KANJI_FULL_FAILURE = '@@kanji/FETCH_KANJI_FULL_FAILURE';

export const fetchKanjiFullByIdsRequest = (ids, token) => ({
  type: FETCH_KANJI_FULL_REQUEST,
  ids,
  token,
});

export const fetchKanjiFullByLiteralRequest = (literal, token) => ({
  type: FETCH_KANJI_FULL_REQUEST,
  literal,
  token,
});

export const fetchKanjiFullSuccess = (payload) => ({
  type: FETCH_KANJI_FULL_SUCCESS,
  payload,
});

export const fetchKanjiFullFailure = (payload) => ({
  type: FETCH_KANJI_FULL_FAILURE,
  payload,
});

export const UNLOCK_KANJI_REQUEST = '@@kanji/UNLOCK_KANJI_REQUEST';
export const UNLOCK_KANJI_SUCCESS = '@@kanji/UNLOCK_KANJI_SUCCESS';
export const UNLOCK_KANJI_FAILURE = '@@kanji/UNLOCK_KANJI_FAILURE';

export const unlockKanjiRequest = (id, token) => ({
  type: UNLOCK_KANJI_REQUEST,
  id,
  token,
});

export const unlockKanjiSuccess = (payload) => ({
  type: UNLOCK_KANJI_SUCCESS,
  payload,
});

export const unlockKanjiFailure = (payload) => ({
  type: UNLOCK_KANJI_FAILURE,
  payload,
});

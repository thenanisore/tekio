export const LOGIN_REQUEST = '@@auth/LOGIN_REQUEST';
export const LOGIN_SUCCESS = '@@auth/LOGIN_SUCCESS';
export const LOGIN_FAILURE = '@@auth/LOGIN_FAILURE';

export const TOKEN_RECEIVED = '@@auth/TOKEN_RECEIVED';

export const LOGOUT = '@@auth/LOGOUT';

export const loginRequest = (creds) => ({
  type: LOGIN_REQUEST,
  payload: creds,
});

export const loginSuccess = (payload) => ({
  type: LOGIN_SUCCESS,
  payload,
});

export const loginFailure = (payload) => ({
  type: LOGIN_FAILURE,
  payload,
});

export const tokenReceived = (token) => ({
  type: TOKEN_RECEIVED,
  payload: token,
});

export const logout = (toLogin, from) => ({
  type: LOGOUT,
  toLogin,
  from,
});

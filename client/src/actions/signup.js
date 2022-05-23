export const SIGNUP_REQUEST = '@@signup/SIGNUP_REQUEST';
export const SIGNUP_SUCCESS = '@@signup/SIGNUP_SUCCESS';
export const SIGNUP_FAILURE = '@@signup/SIGNUP_FAILURE';

export const signupRequest = (creds) => ({
  type: SIGNUP_REQUEST,
  payload: creds,
});

export const signupSuccess = (payload) => ({
  type: SIGNUP_SUCCESS,
  payload,
});

export const signupFailure = (error) => ({
  type: SIGNUP_FAILURE,
  payload: error,
});

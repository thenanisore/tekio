export const FETCH_USER_REQUEST = '@@user/FETCH_USER_REQUEST';
export const FETCH_USER_SUCCESS = '@@user/FETCH_USER_SUCCESS';
export const FETCH_USER_FAILURE = '@@user/FETCH_USER_FAILURE';

export const fetchUserRequest = (token) => ({
  type: FETCH_USER_REQUEST,
  token,
});

export const fetchUserSuccess = (payload) => ({
  type: FETCH_USER_SUCCESS,
  payload,
});

export const fetchUserFailure = (payload) => ({
  type: FETCH_USER_FAILURE,
  payload,
});

export const PURGE_USER = '@@user/PURGE_USER';

export const purgeUser = () => ({
  type: PURGE_USER,
});

export const PUT_SETTINGS_REQUEST = '@@user/PUT_SETTINGS_REQUEST';
export const PUT_SETTINGS_SUCCESS = '@@user/PUT_SETTINGS_SUCCESS';
export const PUT_SETTINGS_FAILURE = '@@user/PUT_SETTINGS_FAILURE';

export const putSettingsRequest = (settings, token) => ({
  type: PUT_SETTINGS_REQUEST,
  settings,
  token,
});

export const putSettingsSuccess = (payload) => ({
  type: PUT_SETTINGS_SUCCESS,
  payload,
});

export const putSettingsFailure = (payload) => ({
  type: PUT_SETTINGS_FAILURE,
  payload,
});

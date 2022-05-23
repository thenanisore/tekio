export const FETCH_PROFILE_REQUEST = '@@profile/FETCH_PROFILE_REQUEST';
export const FETCH_PROFILE_SUCCESS = '@@profile/FETCH_PROFILE_SUCCESS';
export const FETCH_PROFILE_FAILURE = '@@profile/FETCH_PROFILE_FAILURE';

export const fetchProfileRequest = (id) => ({
  type: FETCH_PROFILE_REQUEST,
  id,
});

export const fetchProfileSuccess = (payload) => ({
  type: FETCH_PROFILE_SUCCESS,
  payload,
});

export const fetchProfileFailure = (payload) => ({
  type: FETCH_PROFILE_FAILURE,
  payload,
});

export const PUT_PROFILE_REQUEST = '@@profile/PUT_PROFILE_REQUEST';
export const PUT_PROFILE_SUCCESS = '@@profile/PUT_PROFILE_SUCCESS';
export const PUT_PROFILE_FAILURE = '@@profile/PUT_PROFILE_FAILURE';

export const putProfileRequest = (profile, token) => ({
  type: PUT_PROFILE_REQUEST,
  profile,
  token,
});

export const putProfileSuccess = (payload) => ({
  type: PUT_PROFILE_SUCCESS,
  payload,
});

export const putProfileFailure = (payload) => ({
  type: PUT_PROFILE_FAILURE,
  payload,
});

export const UPLOAD_PICTURE_REQUEST = '@@profile/UPLOAD_PICTURE_REQUEST';
export const UPLOAD_PICTURE_SUCCESS = '@@profile/UPLOAD_PICTURE_SUCCESS';
export const UPLOAD_PICTURE_FAILURE = '@@profile/UPLOAD_PICTURE_FAILURE';

export const uploadPictureRequest = (pic, token) => ({
  type: UPLOAD_PICTURE_REQUEST,
  pic,
  token,
});

export const uploadPictureSuccess = (payload) => ({
  type: UPLOAD_PICTURE_SUCCESS,
  payload,
});

export const uploadPictureFailure = (payload) => ({
  type: UPLOAD_PICTURE_FAILURE,
  payload,
});

import * as auth from '../actions/auth';
import * as profile from '../actions/profile';

const initialState = {
  profile: null,
  loading: false,
  uploading: false,
  uploaded: false,
  error: null,
};

export default (state = initialState, action) => {
  switch (action.type) {
    case profile.FETCH_PROFILE_REQUEST:
      return {
        ...state,
        loading: true,
      };
    case profile.FETCH_PROFILE_SUCCESS:
      return {
        profile: action.payload,
        loading: false,
        error: null,
      };
    case profile.FETCH_PROFILE_FAILURE:
      return {
        ...state,
        loading: false,
        error: {
          name: action.payload.name,
          code: action.payload.code,
          info: action.payload.message,
        },
      };
    case profile.PUT_PROFILE_REQUEST:
      return {
        ...state,
        uploading: true,
        uploaded: false,
      };
    case profile.PUT_PROFILE_SUCCESS:
      return {
        ...state,
        profile: action.payload,
        uploading: false,
        uploaded: true,
      };
    case profile.PUT_PROFILE_FAILURE:
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

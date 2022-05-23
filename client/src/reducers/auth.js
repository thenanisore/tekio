import * as auth from '../actions/auth';

const initialState = {
  token: null,
  id: null,
  expires: null,
  issues: null,
  jwtId: null,
  loading: false,
  error: null,
};

export default (state = initialState, action) => {
  switch (action.type) {
    case auth.LOGIN_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case auth.LOGIN_SUCCESS:
      return {
        ...action.payload,
        loading: false,
        error: null,
      };
    case auth.LOGIN_FAILURE:
      return {
        token: null,
        loading: false,
        error: {
          name: action.payload.name,
          code: action.payload.code,
          info: action.payload.message,
        },
      };
    case auth.TOKEN_RECEIVED:
      return {
        ...state,
        loading: true,
      };
    case auth.LOGOUT:
      return initialState;
    default:
      return state;
  }
};

export const isAuthenticated = (state) => {
  return state.auth.token != null && state.auth.expires * 1000 > Date.now();
};

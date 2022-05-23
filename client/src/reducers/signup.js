import * as auth from '../actions/auth';
import * as signup from '../actions/signup';

const initialState = {
  successful: false,
  loading: false,
  error: null,
};

export default (state = initialState, action) => {
  switch (action.type) {
    case signup.SIGNUP_REQUEST:
      return {
        ...state,
        loading: true,
        error: null,
      };
    case signup.SIGNUP_SUCCESS:
      return {
        successful: true,
        loading: false,
        error: null,
      };
    case signup.SIGNUP_FAILURE:
      return {
        successful: false,
        loading: false,
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

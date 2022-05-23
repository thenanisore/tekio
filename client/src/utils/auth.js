import jwtDecode from 'jwt-decode';

export const getToken = () => localStorage.getItem('jwt');

export const setToken = (token) => localStorage.setItem('jwt', token);

export const clearToken = () => localStorage.removeItem('jwt');

export const hasToken = () => {
  let token;
  try {
    token = getToken();
  } catch (error) {
    window.alert('Tekio cannot function properly without local storage.');
  }
  return token != null;
};

// the received id looks like "\"3\"", we need to parse it
export const parseId = (id) => {
  const parsed = parseInt(id.replace(/"/g, ''), 10);
  if (isNaN(parsed)) {
    throw new Error(`Can't parse a user id from the token: ${id}`);
  }
  return parsed;
};

export const getTokenData = (jwt) => {
  const decoded = jwtDecode(jwt);
  return {
    token: jwt,
    id: parseId(decoded.sub),
    expires: decoded.exp,
    issued: decoded.iat,
    jwtId: decoded.jti,
  };
};

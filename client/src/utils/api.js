const API_ROOT = `${process.env.REACT_APP_API_HOST}:${
  process.env.REACT_APP_API_PORT
}/`;
console.log('API:' + API_ROOT);

class ServerError {
  constructor(message, code = 500) {
    this.message = message;
    this.name = 'ServerError';
    this.code = code;
  }
}

const request = async (url, requestOptions, token = undefined) => {
  let headers = new Headers();
  headers.append('Accept', 'application/json');
  headers.append('Content-Type', 'application/json');
  if (token) {
    headers.append('Authorization', `Bearer ${token}`);
  }
  const options = {
    mode: 'cors',
    headers: headers,
    ...requestOptions,
  };
  try {
    const response = await fetch(`${API_ROOT}${url}`, options);
    const parsed = await response.json();
    console.log(
      `HTTP ${options.method} RESP ${response.status}: ${
        response.statusText
      } with ${JSON.stringify(parsed)}`,
    );
    if (response.ok) {
      return parsed;
    } else {
      throw new ServerError(parsed.info, response.status);
    }
  } catch (error) {
    console.log(
      'There has been a problem with a fetch operation:',
      error.message,
    );
    if (error.name === 'ServerError') {
      throw error;
    } else {
      throw new Error('Could not connect to the server');
    }
  }
};

const get = async (url, params = {}, token = undefined) => {
  let query = Object.keys(params)
    .map((k) => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
    .join('&');
  if (query) query = '?' + query;
  console.log(`HTTP GET REQ to ${url} with ${JSON.stringify(params)}`);
  return request(
    url + query,
    {
      method: 'GET',
    },
    token,
  );
};

const post = async (url, body, token = undefined) => {
  console.log(`HTTP POST REQ to ${url} with ${JSON.stringify(body)}`);
  return request(
    url,
    {
      method: 'POST',
      body: JSON.stringify(body),
    },
    token,
  );
};

const put = async (url, body, token = undefined) => {
  console.log(`HTTP PUT REQ to ${url} with ${JSON.stringify(body)}`);
  return request(
    url,
    {
      method: 'PUT',
      body: JSON.stringify(body),
    },
    token,
  );
};

const api = {
  util: {
    getInfo: () => get('util/info'),
    echo: (data) => post('util/echo', data),
  },
  user: {
    // req:  { username, password }
    // resp: { token }
    login: (credentials) => post('user/sign_in', credentials),

    // req:  { username, password }
    // resp: { id }
    signup: (credentials) => post('user/sign_up', credentials),

    // req: none
    // resp: { user_info }
    getUser: (token) => get(`user`, {}, token),

    // req: user id
    // resp: { profile }
    getProfile: (id) => get(`user/profile/${id}`, {}),

    // req: array of ids
    // resp: { kanji[] }
    getUserKanji: (ids, token) => {
      const params = { ids: ids.join(',') };
      return get('user/kanji', params, token);
    },

    // req: kanji literal
    // resp: { kanji[] }
    getUserKanjiByLiteral: (literal, token) =>
      get('user/kanji/' + literal, {}, token),

    // req: { unlocked[] }
    // resp: { none }
    unlockKanji: (id, token) => {
      const body = { unlocked: [id] };
      return post('user/unlock', body, token);
    },

    // req: lessons[]
    // resp: { none }
    postLessons: (lessons, spent, token) => {
      const body = { unlocked: lessons, spent };
      return post('user/lessons', body, token);
    },

    // req: from (ms), to (ms) (TODO: pagination)
    // resp: { answers[] }
    getAnswers: (from, to, token) => {
      const params = { from, to };
      return get('user/answers', params, token);
    },

    // req: { answers[] }
    // resp: none
    postAnswers: (answers, token) => {
      const body = { answers };
      return post('user/answers', body, token);
    },

    // req: { settings }
    // resp: updated
    putSettings: (settings, token) => put('user/settings', settings, token),

    // req: { profile }
    // resp: updated
    putProfile: (profile, token) => put('user/profile', profile, token),
  },
  kanji: {
    // req: none (TODO: pagination)
    // resp: { kanji[] }
    getShortList: () => get('kanji'),

    // req: ids separated by comma (TODO: pagination)
    // resp: { kanji[] }
    getFullList: (...ids) => {
      const params = { ids: ids.join(',') };
      return get('kanji', params);
    },

    // req: kanji id
    // resp: { kanji info }
    getKanji: (id) => get('kanji/' + id),

    // req: (x, y)[][]
    // resp: scores[]
    recognize: (strokes, count) => {
      const body = { strokes, count };
      return post('kanji/recognize', body);
    },
  },
};

export const getStaticFile = (name) => {
  return API_ROOT + 'static/' + name;
};

export default api;

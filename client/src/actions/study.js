export const START_LESSONS = '@@study/START_LESSONS';
export const PREPARE_LESSONS_REQUEST = '@@study/PREPARE_LESSONS_REQUEST';
export const PREPARE_LESSONS_SUCCESS = '@@study/PREPARE_LESSONS_SUCCESS';
export const UPLOAD_LESSONS_REQUEST = '@@study/UPLOAD_LESSONS_REQUEST';
export const UPLOAD_LESSONS_SUCCESS = '@@study/UPLOAD_LESSONS_SUCCESS';
export const UPLOAD_LESSONS_FAILURE = '@@study/UPLOAD_LESSONS_FAILURE';
export const FINISH_LESSONS = '@@study/FINISH_LESSONS';

export const startLessons = (queue) => ({
  type: START_LESSONS,
  queue,
});

export const prepareLessonsRequest = (ids, token) => ({
  type: PREPARE_LESSONS_REQUEST,
  ids,
  token,
});

export const prepareLessonsSuccess = (payload) => ({
  type: PREPARE_LESSONS_SUCCESS,
  payload,
});

export const uploadLessonsRequest = (ids, spent, token) => ({
  type: UPLOAD_LESSONS_REQUEST,
  ids,
  spent,
  token,
});

export const uploadLessonsSuccess = (payload) => ({
  type: UPLOAD_LESSONS_SUCCESS,
  payload,
});

export const uploadLessonsFailure = (payload) => ({
  type: UPLOAD_LESSONS_FAILURE,
  payload,
});

export const finishLessons = () => ({
  type: FINISH_LESSONS,
});

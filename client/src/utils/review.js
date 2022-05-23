export const getPersistedReviews = () => localStorage.getItem('reviewSession');

export const persistReviews = (reviewState) => {
  localStorage.setItem(
    'reviewSession',
    JSON.stringify({
      persistedAt: new Date().getTime(),
      state: { ...reviewState, itemInfo: undefined },
    }),
  );
};

export const clearPersistedReviews = () =>
  localStorage.removeItem('reviewSession');

export const hasPersistedReviews = () => {
  let reviewState;
  try {
    reviewState = getPersistedReviews();
  } catch (error) {
    window.alert('Tekio cannot function properly without local storage.');
  }
  return reviewState != null;
};

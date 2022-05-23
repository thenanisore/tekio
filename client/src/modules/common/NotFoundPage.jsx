import { withLayout } from '.';
import NotFoundComponent from './NotFoundComponent';

const NotFoundPage = withLayout(NotFoundComponent, true, {
  upperText: '(╯°□°）╯︵ ┻━┻',
  text: 'Page not found!',
});

export default NotFoundPage;

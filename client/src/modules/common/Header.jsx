import PropTypes from 'prop-types';
import React from 'react';
import { Link } from 'react-router-dom';
import {
  Button,
  Container,
  Dropdown,
  Icon,
  Image,
  Label,
  Menu,
} from 'semantic-ui-react';

const UserMenu = ({ username, picture, id }) => {
  const trigger = (
    <span>
      <Image avatar src={picture} /> <strong>{username}</strong>
    </span>
  );
  return (
    <Dropdown trigger={trigger} pointing="top right">
      <Dropdown.Menu>
        <Link to={'/profile/' + id}>
          <Dropdown.Item>
            <Icon name="user" />
            Profile
          </Dropdown.Item>
        </Link>
        <Link to="/settings">
          <Dropdown.Item>
            <Icon name="settings" />
            Settings
          </Dropdown.Item>
        </Link>
        <Dropdown.Divider />
        <Link to="/logout">
          <Dropdown.Item>
            <Icon name="sign out" />
            Sign Out
          </Dropdown.Item>
        </Link>
      </Dropdown.Menu>
    </Dropdown>
  );
};

const Header = ({
  isAuthenticated,
  username,
  picture,
  userId,
  reviewCount,
  lessonCount,
  location,
}) => {
  const rightMenu = isAuthenticated ? (
    <Menu.Menu position="right">
      <Menu.Item
        name="kanji"
        active={location && location.pathname.startsWith('/kanji')}>
        <Link to="/kanji">
          <Icon name="book" />
          Dictionary
        </Link>
      </Menu.Item>
      <Menu.Item name="logout">
        <UserMenu username={username} picture={picture} id={userId} />
      </Menu.Item>
    </Menu.Menu>
  ) : (
    <Menu.Menu position="right">
      <Menu.Item name="login">
        <Link to="/login">Sign In</Link>
      </Menu.Item>
      <Menu.Item name="signup">
        <Link to="/signup">
          <Button color="purple">Sign Up</Button>
        </Link>
      </Menu.Item>
    </Menu.Menu>
  );

  const queueLabel = (n) =>
    n && n !== 0 ? (
      <Label circular color="blue">
        {n}
      </Label>
    ) : (
      ''
    );

  return (
    <Menu borderless size="large">
      <Container>
        <Menu.Item>
          <Link to="/">
            <h1>Tekiō 適</h1>
          </Link>
        </Menu.Item>
        {isAuthenticated ? (
          <Menu.Item
            name="study"
            active={location && location.pathname.startsWith('/study')}>
            <Link to="/study">
              Lessons
              {'  '}
              {queueLabel(lessonCount)}
            </Link>
          </Menu.Item>
        ) : (
          ''
        )}
        {isAuthenticated ? (
          <Menu.Item
            name="review"
            active={location && location.pathname.startsWith('/review')}>
            <Link to="/review">
              Reviews
              {'  '}
              {queueLabel(reviewCount)}
            </Link>
          </Menu.Item>
        ) : (
          ''
        )}
        {rightMenu}
      </Container>
    </Menu>
  );
};

Header.propTypes = {
  isAuthenticated: PropTypes.bool.isRequired,
};

export default Header;

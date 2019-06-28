import React from 'react';

import { NavigationContext } from './context';
import { Platform } from 'react-native';

const globalEventListeners = new Map();

const triggerEvent = (event) =>
  globalEventListeners.forEach((eventListener) => eventListener(event));

class NavigationEventsHandler extends React.Component {
  constructor(props) {
    super(props);
    Navigation.events().bindComponent(this);

    this.globalEventId =
      '_' +
      Math.random()
        .toString(36)
        .substr(2, 9);

    if (props.onNavigationCommand) {
      this.unregisterCommands = Navigation.events().registerCommandListener(
        (name, params) =>
          props.onNavigationCommand(name, params, props.componentId),
      );
    }
  }

  componentDidMount() {
    if (this.props.onGlobalEvent) {
      globalEventListeners.set(this.globalEventId, this.props.onGlobalEvent);
    }
  }

  componentWillUnmount() {
    globalEventListeners.delete(this.globalEventId);

    if (this.unregisterCommands) {
      this.unregisterCommands();
    }
  }

  navigationButtonPressed(event) {
    this.props.onNavigationButtonPressed(event, this.props.componentId);
  }

  render() {
    return typeof this.props.children === 'function'
      ? this.props.children(triggerEvent)
      : this.props.children || null;
  }
}

export const NavigationEvents = (props) =>
  Platform.OS === 'ios' ? (
    <NavigationContext.Consumer>
      {({ componentId }) => (
        <NavigationEventsHandler {...props} componentId={componentId} />
      )}
    </NavigationContext.Consumer>
  ) : null;

NavigationEvents.defaultProps = {
  onNavigationButtonPressed: () => {},
};

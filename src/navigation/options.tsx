import React from 'react';

import { NavigationContext } from './context';
import { Mount, Update } from 'react-lifecycle-components';
import { Platform } from 'react-primitives';

interface NavigationOptionsProps {
  options: any;
}

export const NavigationOptions: React.SFC<NavigationOptionsProps> = ({
  children = null,
  options,
}) =>
  Platform.OS === 'android' ? (
    <>{children}</>
  ) : (
    <NavigationContext.Consumer>
      {({ componentId = '' }) => {
        return (
          <Mount on={() => Navigation.mergeOptions(componentId, options)}>
            {Platform.OS === 'ios' && (
              <Update
                was={() => Navigation.mergeOptions(componentId, options)}
                watched={options}
              >
                {children}
              </Update>
            )}
          </Mount>
        );
      }}
    </NavigationContext.Consumer>
  );

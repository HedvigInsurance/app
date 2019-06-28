import * as React from 'react';
import Config from '@hedviginsurance/react-native-config';
import { TouchableWithoutFeedback, Platform } from 'react-native';
import {
  getDebugLayout,
  openDebugLayout,
} from 'src/navigation/screens/debug/layout';

interface DebugShortcutProps {
  componentId: string;
  children: React.ReactElement<any>;
}

export const DebugShortcut: React.StatelessComponent<DebugShortcutProps> = ({
  children,
  componentId,
}) => {
  if (__DEV__ || Config.APP_ID !== 'com.hedvig.app') {
    return (
      <TouchableWithoutFeedback
        onLongPress={() => {
          switch (Platform.OS) {
            case 'ios': {
              openDebugLayout();
              return;
            }
            case 'android': {
              // Navigation.push(componentId, getDebugLayout().root);
            }
            default:
              return;
          }
        }}
      >
        {children}
      </TouchableWithoutFeedback>
    );
  }
  return children;
};

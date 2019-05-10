import * as React from 'react';

import { ImageLibrary } from 'src/components/icons/ImageLibrary';

import { UploadMutation } from '../upload-mutation';
import { UploadingAnimation } from '../uploading-animation';

import { PickerButton } from './picker-button';
import { Navigation } from 'react-native-navigation';
import { FILE_PICKER_COMPONENT } from 'src/navigation/components/file-picker';
import { Platform, NativeModules } from 'react-native';

interface FileProps {
  onUpload: (url: string) => void;
}

export const File: React.SFC<FileProps> = ({ onUpload }) => (
  <UploadMutation>
    {(upload, isUploading) => (
      <>
        <PickerButton
          onPress={() => {
            if (Platform.OS === 'ios') {
              Navigation.showOverlay({
                component: {
                  name: FILE_PICKER_COMPONENT.name,
                  options: {
                    layout: {
                      backgroundColor: 'transparent',
                    },
                  },
                  passProps: {
                    upload,
                    onUpload,
                  },
                },
              });
            } else {
              NativeModules.ActivityStarter
                .showFileUploadOverlay()
                .then((key: string) => {
                  onUpload(key)
                })
            }
          }}
        >
          <UploadingAnimation darkMode isUploading={isUploading}>
            <ImageLibrary width={18} height={18} />
          </UploadingAnimation>
        </PickerButton>
      </>
    )}
  </UploadMutation>
);

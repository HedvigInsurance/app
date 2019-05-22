import * as React from 'react';

import { ImageLibrary } from 'src/components/icons/ImageLibrary';

import { UploadMutation } from '../upload-mutation';
import { UploadingAnimation } from '../uploading-animation';

import { PickerButton } from './picker-button';
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
              NativeModules.NativeRouting.showFileUploadOverlay(true).then(
                (urls: [string]) => {
                  urls.forEach((url: any) => {
                    upload(url).then((uploadResponse) => {
                      if (uploadResponse instanceof Error) {
                      } else {
                        onUpload(uploadResponse.key);
                      }
                    });
                  });
                },
              );
            } else {
              NativeModules.ActivityStarter.showFileUploadOverlay().then(
                (key: string) => {
                  onUpload(key);
                },
              );
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

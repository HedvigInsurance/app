import * as React from 'react';
import { DraggableOverlay } from 'src/components/draggable-overlay';
import { isIphoneX } from 'react-native-iphone-x-helper';
import {
  Dimensions,
  View,
  TouchableOpacity,
  Text,
  Platform,
} from 'react-native';
import styled from '@sampettersson/primitives';
import { colors, fonts } from '@hedviginsurance/brand';
import { Spacing } from 'src/components/Spacing';
import ImagePicker from 'react-native-image-picker';
import {
  DocumentPicker,
  DocumentPickerUtil,
} from '@hedviginsurance/react-native-document-picker';
import { BackButton } from 'src/components/BackButton';

const OverlayContent = styled(View)({
  paddingLeft: 30,
  paddingRight: 30,
});

const Row = styled(View)({
  flexDirection: 'row',
});

const OpenPickerButton = styled(TouchableOpacity)({
  borderWidth: 1,
  borderColor: colors.PURPLE,
  height: 40,
  borderRadius: 20,
  flex: 1,
  alignItems: 'center',
  justifyContent: 'center',
});

const PickerButtonText = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.PURPLE,
  fontSize: 14,
});

const Heading = styled(Text)({
  fontFamily: fonts.CIRCULAR,
  color: colors.BLACK,
  fontSize: 15,
  fontWeight: '600',
});

interface PickerOverlayProps {
  componentId: string;
  upload: (uri: string) => Promise<Error | { key: string }>;
  onUpload: (key: string) => void;
}

export const PickerOverlay: React.SFC<PickerOverlayProps> = ({
  componentId,
  upload,
  onUpload,
}) => (
  <DraggableOverlay
    heightPercentage={
      ((isIphoneX() ? 125 : 100) / Dimensions.get('window').height) * 100
    }
    onClose={() => Navigation.dismissOverlay(componentId)}
  >
    {(handleClose) => (
      <>
        <BackButton onPress={() => handleClose()} />
        <OverlayContent>
          <Spacing height={20} />
          <Heading>Vad vill du skicka?</Heading>
          <Spacing height={10} />
          <Row>
            <OpenPickerButton
              onPress={() => {
                ImagePicker.launchImageLibrary({}, (response) => {
                  if (response.origURL) {
                    handleClose();
                    upload(response.origURL).then((uploadResponse) => {
                      if (uploadResponse instanceof Error) {
                      } else {
                        onUpload(uploadResponse.key);
                      }
                    });
                  } else if (Platform.OS === 'android' && response.uri) {
                    handleClose();
                    upload(response.uri).then((uploadResponse) => {
                      if (!(uploadResponse instanceof Error)) {
                        onUpload(uploadResponse.key);
                      }
                    });
                  }
                });
              }}
            >
              <PickerButtonText>Bild eller film</PickerButtonText>
            </OpenPickerButton>
            <Spacing width={15} />
            <OpenPickerButton
              onPress={() => {
                DocumentPicker.show(
                  {
                    filetype: [DocumentPickerUtil.allFiles()],
                  },
                  (_: any, res: any) => {
                    setTimeout(() => {
                      handleClose();
                      upload(res.uri).then((uploadResponse) => {
                        if (!(uploadResponse instanceof Error)) {
                          onUpload(uploadResponse.key);
                        }
                      });
                    }, 50);
                  },
                );
              }}
            >
              <PickerButtonText>Fil</PickerButtonText>
            </OpenPickerButton>
          </Row>
        </OverlayContent>
      </>
    )}
  </DraggableOverlay>
);

import * as React from 'react';
import styled from '@sampettersson/primitives';
import { View, TouchableOpacity } from 'react-native';
import { colors } from '@hedviginsurance/brand';
import FastImage from 'react-native-fast-image';

import { createImageProgress } from 'react-native-image-progress';
import * as Progress from 'react-native-progress';

const Image = createImageProgress(FastImage);

const Box = styled(View)({
  paddingLeft: 10,
  paddingBottom: 10,
  width: 250,
  height: 190,
  overflow: 'hidden',
});

const BorderRadius = styled(View)({
  borderRadius: 20,
  overflow: 'hidden',
});

const ImageContainer = styled(Image)({
  height: '100%',
  width: '100%',
});

interface GifImageProps {
  url: string;
  onPress: () => void;
}

export const GifImage: React.SFC<GifImageProps> = ({ url, onPress }) => (
  <TouchableOpacity onPress={onPress}>
    <Box>
      <BorderRadius>
        <ImageContainer
          indicator={Progress.CircleSnail}
          indicatorProps={{
            size: 40,
            thickness: 5,
            color: colors.PINK,
          }}
          source={{ uri: url }}
        />
      </BorderRadius>
    </Box>
  </TouchableOpacity>
);

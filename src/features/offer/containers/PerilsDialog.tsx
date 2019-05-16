import React from 'react';
import { View, Text, StyleSheet, Image } from 'react-native';
import { DraggableOverlay } from 'src/components/draggable-overlay';

import { PERIL_IMAGE_MAP } from '../../../features/dashboard/components/Peril';

import { Description } from '../components/Description';
import { Heading } from '../components/Heading';

import { colors } from '@hedviginsurance/brand';
import styled from '@sampettersson/primitives';
import { BackButton } from 'src/components/BackButton';

const styles = StyleSheet.create({
  dialogContent: {
    height: '100%',
    backgroundColor: colors.OFF_WHITE,
    borderRadius: 3,
    overflow: 'hidden',
  },
  dialogHeading: {
    fontFamily: 'CircularStd-Book',
    color: colors.OFF_BLACK,
    fontSize: 23,
    lineHeight: 32,
    marginTop: 15,
  },
  contentWrapper: {
    backgroundColor: colors.WHITE,
    flex: 1,
  },
  dialogSubHeading: {
    fontFamily: 'CircularStd-Book',
    color: colors.DARK_GRAY,
    fontSize: 17,
    marginTop: 5,
  },
  perilsContent: {
    padding: 25,
  },
  perilsHeader: {
    padding: 25,
    justifyContent: 'space-between',
    alignItems: 'center',
    flexDirection: 'row',
  },
});

const PerilImage = styled(Image)({
  marginTop: 18,
  marginBottom: 10,
  height: 60,
  width: 60,
});

const cleanTitle = (title: string) => (title || '').replace(/[\n-]/g, '');

const getHeightPercentage = (length: number) => {
  if (length > 300) {
    return 70;
  }
  if (length > 200) {
    return 60;
  }
  return 50;
};

export interface Peril {
  description: string;
  title: string;
  id: keyof typeof PERIL_IMAGE_MAP;
}

export const PerilsDialog: React.SFC<{
  peril: Peril;
  categoryTitle: string;
  componentId: string;
}> = ({ componentId, categoryTitle, peril }) =>
  peril ? (
    <DraggableOverlay
      onClose={() => {
        Navigation.dismissOverlay(componentId);
      }}
      heightPercentage={getHeightPercentage(peril.description.length)}
    >
      {(handleClose) => (
        <>
          <BackButton onPress={() => handleClose()} />
          <View style={styles.dialogContent}>
            <View style={styles.perilsHeader}>
              <View>
                <Text style={styles.dialogHeading}>{categoryTitle}</Text>
                <Text style={styles.dialogSubHeading}>Försäkras mot</Text>
              </View>
              <PerilImage
                source={PERIL_IMAGE_MAP[peril.id]}
                resizeMode="contain"
              />
            </View>
            <View style={styles.contentWrapper}>
              <View style={styles.perilsContent}>
                <Heading>{cleanTitle(peril.title)}</Heading>
                <Description>{peril.description}</Description>
              </View>
            </View>
          </View>
        </>
      )}
    </DraggableOverlay>
  ) : null;

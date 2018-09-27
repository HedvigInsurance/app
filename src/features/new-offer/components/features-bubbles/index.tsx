import * as React from 'react';
import styled from '@sampettersson/primitives';
import { View } from 'react-native';
import { Parallel } from 'animated-react-native-components';

import { Insured } from 'src/features/new-offer/components/features-bubbles/bubbles/insured';
import { StartDate } from 'src/features/new-offer/components/features-bubbles/bubbles/start-date';
import { Spacing } from 'src/components/Spacing';
import { TravelProtection } from 'src/features/new-offer/components/features-bubbles/bubbles/travel-protection';
import { Deductible } from 'src/features/new-offer/components/features-bubbles/bubbles/deductible';
import { BindingPeriod } from 'src/features/new-offer/components/features-bubbles/bubbles/binding-period';
import { InsuranceType } from 'src/graphql/types';
import { OwnedAddon } from 'src/features/new-offer/components/features-bubbles/bubbles/owned-addon';

import { Position } from './position';

const Container = styled(View)({
  height: 325,
  width: 350,
  marginLeft: 35,
  position: 'relative',
});

interface FeaturesBubblesProps {
  personsInHousehold: number;
  insuredAtOtherCompany: boolean;
  type: InsuranceType;
}

export const FeaturesBubbles: React.SFC<FeaturesBubblesProps> = ({
  personsInHousehold,
  insuredAtOtherCompany,
  type,
}) => (
  <>
    <Spacing height={35} />
    <Parallel>
      <Container>
        <Position top={190} left="23%">
          <Deductible />
        </Position>
        <Position top={80} left="0%">
          <BindingPeriod />
        </Position>
        <Position top={140} left="46%">
          {type === InsuranceType.BRF ? <OwnedAddon /> : <TravelProtection />}
        </Position>
        <Position top={25} left="47%">
          <StartDate insuredAtOtherCompany={insuredAtOtherCompany} />
        </Position>
        <Position top={0} left="20%">
          <Insured personsInHousehold={personsInHousehold} />
        </Position>
      </Container>
    </Parallel>
  </>
);

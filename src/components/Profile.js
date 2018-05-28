import React from 'react';
import { View, Share, Image, StyleSheet, Linking } from 'react-native';
import { HeaderRightChat } from './NavBar';
import {
  StyledProfileContainer,
  StyledCharityParagraph,
  StyledCharitySignature,
} from './styles/profile';
import {
  StyledListHeader,
  StyledList,
  StyledListElement,
  StyledListElementTexts,
  StyledListElementHeading,
  StyledListElementText,
  StyledRowButton,
  TouchableStyledListElement,
} from './styles/list';
import {
  ProfileHeartIcon,
  ProfileFamilyIcon,
  ProfileLockIcon,
  ProfileBankAccountIcon,
  ProfileShareIcon,
} from './Icon';
import { DisabledListNextButton, RoundedButton } from './Button';
import { Loader } from './Loader';
import 'moment/locale/sv';
import * as R from 'ramda';

const styles = StyleSheet.create({
  cashbackImage: {
    width: 300,
    marginTop: 8,
    marginBottom: 16,
  },
});
export default class Profile extends React.Component {
  static navigationOptions = ({ navigation }) => ({
    title: 'Min Profil',
    headerRight: <HeaderRightChat navigation={navigation} />,
  });

  componentDidMount() {
    this.props.getUser();
    this.props.getCashbackAlternatives();
    this.props.getInsurance();
  }

  _userRow({ title, icon, text, secondText = null, onPress = null }) {
    let ListElementComponent = onPress
      ? TouchableStyledListElement
      : StyledListElement;
    let maybeButton = onPress ? (
      <StyledRowButton>
        <DisabledListNextButton />
      </StyledRowButton>
    ) : null;
    return (
      <ListElementComponent onPress={onPress}>
        {icon}
        <StyledListElementTexts>
          <StyledListElementHeading>{title}</StyledListElementHeading>
          <StyledListElementText>{text}</StyledListElementText>
          {secondText ? (
            <StyledListElementText>{secondText}</StyledListElementText>
          ) : null}
        </StyledListElementTexts>
        {maybeButton}
      </ListElementComponent>
    );
  }

  _maybeFamilyMembers() {
    if (this.props.user.familyMembers) {
      return this._userRow({
        title: 'Familjemedlemmar',
        text: R.join(', ', this.props.user.familyMembers),
      });
    }
  }

  _maybePersonalInfo() {
    if (this.props.user.age) {
      return this._userRow({
        title: 'Personlig info',
        icon: <ProfileFamilyIcon />,
        text: `${this.props.user.age} år | ${this.props.user.address}`,
        secondText: R.join(', ', this.props.user.familyMembers),
      });
    }
  }

  _safetyIncreasers() {
    const ucFirst = (string) =>
      string.charAt(0).toUpperCase() + string.slice(1);
    const safetyIncreasers = R.join(
      ', ',
      this.props.user.safetyIncreasers || [],
    );

    if (safetyIncreasers) {
      return this._userRow({
        title: 'Mina trygghetshöjare',
        icon: <ProfileLockIcon />,
        text: ucFirst(safetyIncreasers),
      });
    }
  }

  _maybeBankAccount() {
    if (this.props.user.maskedBankAccountNumber) {
      return this._userRow({
        title: 'Min betalning',
        icon: <ProfileBankAccountIcon />,
        text: `${this.props.insurance.currentTotalPrice} kr/månad. ${
          this.props.user.maskedBankAccountNumber
        }`,
        secondText: '',
        // onPress: () => this.props.navigation.navigate("Payment")
      });
    }
  }

  _maybeSelectedCashback() {
    if (this.props.user.selectedCashback) {
      return this._userRow({
        title: 'Min välgörenhet',
        icon: <ProfileHeartIcon />,
        text: this.props.user.selectedCashback,
      });
    }
  }

  _maybeCertificate() {
    const { certificateUrl } = this.props.insurance;
    if (certificateUrl) {
      return this._userRow({
        title: 'Mitt försäkringsbrev',
        icon: (
          <Image
            style={{
              width: 40,
              height: 40,
            }}
            source={require('../../assets/icons/profil/insurance-certificate.png')}
          />
        ),
        text: 'Tryck för att läsa',
        onPress: () => Linking.openURL(certificateUrl),
      });
    }
  }

  _shareRow() {
    return this._userRow({
      title: 'Rekommendera en vän',
      icon: <ProfileShareIcon />,
      text: 'Låt Hedvig förenkla vardagen för någon du bryr dig om',
      onPress: () => this._sharePressed(),
    });
  }

  _sharePressed() {
    Share.share(
      {
        message:
          'Livet är enklare med Hedvig. Ladda ner appen på AppStore eller Google Play. Läs mer på https://www.hedvig.com',
        title: 'Hedvig är försäkring som du aldrig tidigare har upplevt det',
      },
      {
        // Android only:
        dialogTitle: 'Dela Hedvig',
        // iOS only:
        excludedActivityTypes: ['com.apple.UIKit.activity.PostToTwitter'],
      },
    );
  }

  _maybeUserInfo() {
    if (this.props.user) {
      return (
        <View>
          {this._maybeSelectedCashback()}
          {this._maybePersonalInfo()}
          {this._safetyIncreasers()}
          {this._maybeBankAccount()}
          {this._maybeCertificate()}
        </View>
      );
    }
  }

  render() {
    // WARNING: Change this to loading state based on the request or something
    // more robust
    if (!this.props.user || !this.props.user.address) {
      return <Loader />;
    }

    // Barncancerfonden logo is almost a square so we need to adjust height
    // compared to SOS barnbyar which is more widescreen (should be default)
    let cashbackImageHeight =
      {
        Barncancerfonden: 100,
      }[this.props.user.selectedCashback] || 50;

    return (
      <StyledProfileContainer>
        <StyledList>
          <StyledListHeader>
            <Image
              source={{ uri: this.props.user.selectedCashbackImageUrl }}
              resizeMode="contain"
              style={[
                styles.cashbackImage,
                {
                  height: cashbackImageHeight,
                },
              ]}
            />
            <StyledCharityParagraph>
              {this.props.user.selectedCashbackParagraph}
            </StyledCharityParagraph>
            <StyledCharitySignature>
              {this.props.user.selectedCashbackSignature}
            </StyledCharitySignature>
          </StyledListHeader>
          {this._maybeUserInfo()}
          <RoundedButton
            title="Logga ut"
            onPress={() => this.props.logout()}
            style={{ marginTop: 8, marginBottom: 24 }}
          />
        </StyledList>
      </StyledProfileContainer>
    );
  }
}

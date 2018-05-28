import { connect } from 'react-redux';
import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  BackHandler,
  Dimensions,
  ScrollView,
  Keyboard,
} from 'react-native';
import * as R from 'ramda';
import PopupDialog from 'react-native-popup-dialog';

import { XNavigateBackButton } from '../../components/Button';
import { PERIL_IMAGE_MAP } from '../../components/dashboard/Peril';

import {
  horizontalSizeClass,
  H_SPACIOUS,
  H_REGULAR,
  H_COMPACT,
} from '../../services/DimensionSizes';
import {
  PERILS_DIALOG_SHOWN,
  PERILS_DIALOG_DISMISSED,
  PERILS_UNSET_ACTIVE,
} from './actions';
import { Description } from './Description';
import { Heading } from './Heading';

const { width: viewportWidth } = Dimensions.get('window');

const styles = StyleSheet.create({
  backdrop: { height: 0 },
  dialogClose: {
    position: 'absolute',
    left: 15,
    top: 15,
    zIndex: 2,
  },
  dialog: {
    backgroundColor: 'transparent',
    justifyContent: 'center',
    zIndex: 102,
    elevation: 1,
    paddingTop: 20,
    paddingBottom: 20,
    paddingHorizontal: {
      [H_SPACIOUS]: 15,
      [H_REGULAR]: 5,
      [H_COMPACT]: 5,
    }[horizontalSizeClass],
  },
  dialogContent: {
    position: 'relative',
    backgroundColor: '#F9FAFC',
    borderRadius: 3,
    overflow: 'hidden',
  },
  dialogHeading: {
    fontFamily: 'circular',
    color: '#414150',
    fontSize: 23,
    lineHeight: 32,
    marginTop: 15,
  },
  contentWrapper: {
    backgroundColor: 'white',
    flex: 1,
  },
  dialogSubHeading: {
    fontFamily: 'circular',
    color: '#9B9BAA',
    fontSize: 17,
    marginTop: 5,
  },
  dialogImage: {
    marginTop: 18,
    marginBottom: 10,
    height: 120,
    width: 120,
  },
  perilsContent: {
    padding: 25,
  },
  perilsHeader: {
    padding: 25,
    justifyContent: 'center',
    alignItems: 'center',
  },
});

class PerilsDialog extends React.Component {
  componentDidMount() {
    BackHandler.addEventListener('hardwareBackPress', this.onBackPress);
  }

  componentWillUnmount() {
    BackHandler.removeEventListener('hardwareBackPress', this.onBackPress);
  }

  componentDidUpdate() {
    const { activePeril, isPerilsDialogOpen } = this.props;
    if (activePeril && !isPerilsDialogOpen) {
      Keyboard.dismiss();
      this.popupDialog.show();
    } else if (!activePeril && isPerilsDialogOpen) {
      this.popupDialog.dismiss();
    }
  }

  onBackPress = () => {
    this.props.unsetActivePeril();
  };

  isPerilActive(peril) {
    const { activePeril } = this.props;
    return peril && R.path(['id'], activePeril) === R.path(['id'], peril);
  }

  cleanTitle(title) {
    // Backend hyphenates some titles so until we do this on the front end
    // entirely we need to strip those hyphens for the main title
    return (title || '').replace(/[\n-]/g, '');
  }

  render() {
    return (
      <PopupDialog
        ref={(popupDialog) => {
          this.popupDialog = popupDialog;
        }}
        dismissOnTouchOutside={false}
        dismissOnHardwareBackPress={false}
        width={viewportWidth - 20}
        height={0.99}
        style={styles.backdrop}
        dialogStyle={styles.dialog}
        onShown={this.props.dialogShown}
        onDismissed={this.props.dialogDismissed}
      >
        {this.props.isPerilsDialogOpen &&
          this.props.activePeril && (
            <View style={styles.dialogContent}>
              <View style={styles.dialogClose}>
                <XNavigateBackButton
                  onPress={() => this.props.unsetActivePeril()}
                />
              </View>
              <ScrollView>
                <View style={styles.perilsHeader}>
                  <Text style={styles.dialogHeading}>
                    {this.props.activePerilCategoryTitle}
                  </Text>
                  <Text style={styles.dialogSubHeading}>Försäkras mot</Text>
                  <Image
                    style={styles.dialogImage}
                    source={PERIL_IMAGE_MAP[this.props.activePeril.id]}
                    resizeMode="contain"
                  />
                </View>
                <View style={styles.contentWrapper}>
                  <View style={styles.perilsContent}>
                    <Heading>
                      {this.cleanTitle(this.props.activePeril.title)}
                    </Heading>
                    <Description>
                      {this.props.activePeril.description}
                    </Description>
                  </View>
                </View>
              </ScrollView>
            </View>
          )}
      </PopupDialog>
    );
  }
}

const mapStateToProps = (state) => {
  const {
    activePeril,
    isPerilsDialogOpen,
    activePerilCategoryTitle,
  } = state.offer;
  return {
    activePeril,
    isPerilsDialogOpen,
    activePerilCategoryTitle,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    unsetActivePeril: () =>
      dispatch({
        type: PERILS_UNSET_ACTIVE,
      }),
    dialogShown: () => {
      dispatch({
        type: PERILS_DIALOG_SHOWN,
      });
    },
    dialogDismissed: () => {
      dispatch({
        type: PERILS_DIALOG_DISMISSED,
      });
    },
  };
};

const PerilsDialogContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(PerilsDialog);

export { PerilsDialogContainer as PerilsDialog };

import React from 'react';
import { View, StyleSheet, Text } from 'react-native';
import { connect } from 'react-redux';
import { AudioRecorder, AudioUtils } from 'react-native-audio';
import Sound from 'react-native-sound';
import styled from '@sampettersson/primitives';

import {
  chatActions,
  uploadActions,
  dialogActions,
} from '../../../../hedvig-redux';
import {
  RecordButton,
  StopRecordingButton,
  StopRecordingAnimationButton,
} from '../../../components/Button';
import { AnimatedSingleSelectOptionButton } from '../components/Button';
import { UploadingAnimation } from '../../../components/Animation';
import {
  StyledMarginContainer,
  StyledRightAlignedOptions,
} from '../styles/chat';
import { StyledPassiveText } from '../../../components/styles/text';
import { Spacing } from '../../../components/Spacing';

import { colors } from '@hedviginsurance/brand';

const audioPath = `${AudioUtils.DocumentDirectoryPath}/claim.aac`;

const styles = StyleSheet.create({
  preRecordingContainer: {
    flexDirection: 'row',
  },
  preRecordingText: {
    fontFamily: 'CircularStd-Book',
    color: colors.OFF_BLACK,
    fontSize: 12,
    alignSelf: 'center',
    paddingRight: 12,
    paddingBottom: 8,
  },
  playbackStatusText: {
    fontFamily: 'CircularStd-Book',
    color: colors.DARK_GRAY,
    fontSize: 14,
    marginRight: 16,
  },
});

const RecordingTimeContainer = styled(View)({
  flexDirection: 'column',
  alignItems: 'flex-end',
  width: '50%',
});

class AudioInput extends React.Component {
  state = {
    isRecording: false,
    recordingTime: 0.0,
    isFinished: false,
  };

  componentDidMount() {
    AudioRecorder.onProgress = this.onProgress;
    AudioRecorder.onFinished = this.onFinished;
  }

  onProgress = (data) => {
    this.setState({ recordingTime: Math.floor(data.currentTime) });
  };

  onFinished = (data) => {
    this.finishRecording(
      data.status === 'OK',
      data.audioFileURL,
      data.audioFileSize,
    );
  };

  finishRecording = (success, url) => {
    if (!success) {
      this.onError('failed to record');
      return;
    }

    this.setState({ recordingUrl: url });
  };

  onError = (err) => {
    // TODO Handle error
    console.error('Error!', err); // eslint-disable-line no-console
  };

  requestPermissions = async () => {
    // TODO
    const status = await Permissions.check('microphone');
    if (status !== 'authorized') {
      const requestStatus = await Permissions.request('microphone', {
        type: 'always',
      });
      return requestStatus === 'authorized';
    }
    return true;
  };

  upload = () => {
    const { recordingUrl } = this.state;
    this.props.upload(this.props.message, {
      uri: recordingUrl,
      type: `audio/x-aac`,
      fileExtension: 'aac',
    });
    this.setState({ hasSentUpload: true });
  };

  startRecording = async () => {
    const hasPermission = await this.requestPermissions();
    if (!hasPermission) {
      return this.props.showPermissionDialog();
    }
    this.setState({ isRecording: true }, async () => {
      await AudioRecorder.prepareRecordingAtPath(audioPath, {
        SampleRate: 22050,
        Channels: 1,
        AudioQuality: 'Low',
        AudioEncoding: 'aac',
        AudioEncodingBitRate: 32000,
      });

      AudioRecorder.startRecording();
    });
  };

  stopRecording = async () => {
    await AudioRecorder.stopRecording();
    this.setState({ isRecording: false, isFinished: true });
  };

  restartRecording = () => {
    this.setState({ isFinished: false });
  };

  startPlayback = () => {
    const sound = new Sound(audioPath, '', () => {
      this.setState({ isPlayingBack: true, sound });
      this._playBackStatusUpdater = setInterval(this.updatePlaybackStatus, 100);
      sound.play(this.stopPlayback);
    });
  };

  stopPlayback = () => {
    const { sound } = this.state;
    if (sound) {
      sound.stop();
    }
    if (this._playBackStatusUpdater) {
      clearInterval(this._playBackStatusUpdater);
    }
    this.setState({ isPlayingBack: false, playbackStatus: undefined });
  };

  updatePlaybackStatus = () => {
    if (this.state.sound) {
      this.state.sound.getCurrentTime((seconds) => {
        this.setState({ playbackStatus: `${Math.floor(seconds)}s` });
      });
    }
  };

  render() {
    const { message, isUploading } = this.props;
    const {
      isRecording,
      isPlayingBack,
      isFinished,
      recordingTime,
      playbackStatus,
      hasSentUpload,
    } = this.state;
    if (isPlayingBack) {
      return (
        <StyledMarginContainer>
          <StyledRightAlignedOptions>
            <StopRecordingButton onPress={this.stopPlayback} />
            <Text style={styles.playbackStatusText}>{playbackStatus}</Text>
          </StyledRightAlignedOptions>
        </StyledMarginContainer>
      );
    }

    if (isUploading || hasSentUpload) {
      return (
        <StyledMarginContainer>
          <StyledRightAlignedOptions>
            <UploadingAnimation />
          </StyledRightAlignedOptions>
        </StyledMarginContainer>
      );
    }

    if (isRecording) {
      return (
        <StyledMarginContainer>
          <StyledRightAlignedOptions>
            <RecordingTimeContainer>
              <StopRecordingAnimationButton onPress={this.stopRecording} />
              <Spacing height={15} />
              <StyledPassiveText>Spelar in: {recordingTime}</StyledPassiveText>
            </RecordingTimeContainer>
          </StyledRightAlignedOptions>
        </StyledMarginContainer>
      );
    }

    if (isFinished) {
      return (
        <StyledMarginContainer>
          <StyledRightAlignedOptions>
            <AnimatedSingleSelectOptionButton
              title="Gör om"
              onPress={this.restartRecording}
            />
          </StyledRightAlignedOptions>
          <StyledRightAlignedOptions>
            <AnimatedSingleSelectOptionButton
              title="Spela upp"
              onPress={this.startPlayback}
            />
          </StyledRightAlignedOptions>
          <StyledRightAlignedOptions>
            <AnimatedSingleSelectOptionButton
              title="Skicka in anmälan"
              onPress={this.upload}
            />
          </StyledRightAlignedOptions>
        </StyledMarginContainer>
      );
    }

    return (
      <StyledMarginContainer>
        <StyledRightAlignedOptions>
          <View style={styles.preRecordingContainer}>
            <Text style={styles.preRecordingText}>{message.body.text}</Text>
            <RecordButton onPress={this.startRecording} />
          </View>
        </StyledRightAlignedOptions>
      </StyledMarginContainer>
    );
  }
}

const mapStateToProps = (state) => {
  let message = state.chat.messages[0];
  return {
    message,
    currentlyUploading: state.upload.currentlyUploading,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    showPermissionDialog: () =>
      dispatch(
        dialogActions.showDialog({
          title: 'Inspelning',
          paragraph:
            'Vänligen aktivera ljudinspelning för Hedvig i dina systeminställningar.',
        }),
      ),
    upload: (message, info) =>
      dispatch(
        uploadActions.upload({
          body: info,
          successActionCreator: (url) =>
            chatActions.sendChatResponse(message, {
              type: 'audio',
              url,
            }),
        }),
      ),
  };
};

const AudioInputContainer = connect(
  mapStateToProps,
  mapDispatchToProps,
)(AudioInput);

export default AudioInputContainer;

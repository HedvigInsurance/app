import React from "react"
import { connect } from "react-redux"

import MessageList from "../containers/chat/MessageList"
import ChatNumberInput from "../containers/chat/ChatNumberInput"
import ChatTextInput from "../containers/chat/ChatTextInput"
import DateInput from "../containers/chat/DateInput"
import MultipleSelectInput from "../containers/chat/MultipleSelectInput"
import SingleSelectInput from "../containers/chat/SingleSelectInput"
import VideoInput from "../containers/chat/VideoInput"
import PhotoInput from "../containers/chat/PhotoInput"
import BankIdCollectInput from "../containers/chat/BankIdCollectInput"
import AudioInput from "../containers/chat/AudioInput"
import {
  StyledMessageArea,
  StyledResponseArea,
  StyledMessageAndResponseArea,
  StyledChatContainer,
} from "./styles/chat"
import ParagraphInput from "../containers/chat/ParagraphInput"
import { NavBar } from "./NavBar"
import { ChatNavRestartButton, NavigateBackButton } from "./Button"
import { types }  from "hedvig-redux"

const inputComponentMap = (lastIndex, navigation) => ({
    multiple_select: <MultipleSelectInput messageIndex={lastIndex} />,
    text: <ChatTextInput messageIndex={lastIndex} />,
    number: <ChatNumberInput messageIndex={lastIndex} />,
    single_select: (
      <SingleSelectInput
        messageIndex={lastIndex}
        launchModal={choice =>
          navigation.navigate("ChatModal", { link: choice })}
      />
    ),
    date_picker: <DateInput messageIndex={lastIndex} />,
    video: (
      <VideoInput
        messageIndex={lastIndex}
        launchVideoRecorder={() => {
          navigation.navigate("ChatModal", { link: { view: "VideoExample" } })
        }}
      />
    ),
    photo_upload: <PhotoInput messageIndex={lastIndex} />,
    bankid_collect: <BankIdCollectInput messageIndex={lastIndex} />,
    paragraph: <ParagraphInput messageIndex={lastIndex} />,
    audio: <AudioInput messageIndex={lastIndex} />,
  })

class UnconnectedPollingMessage extends React.Component {
  componentDidMount() {
    this.props.startPolling()
  }

  componentWillUnmount() {
    this.props.stopPolling()
  }

  render() {
    return (
      <React.Fragment>
        {this.props.children}
      </React.Fragment>
    )
  }
}

const PollingMessage = connect(
  undefined,
  dispatch => ({
    startPolling: () => dispatch({type: types.START_POLLING_MESSAGES}),
    stopPolling: () => dispatch({type: types.STOP_POLLING_MESSAGES}),
  })
)(UnconnectedPollingMessage)

const getInputComponent = function(messages, navigation) {
  if (messages.length === 0) {
    return null
  }
  let lastIndex = messages.length - 1
  let lastMessage = messages[lastIndex]
  let lastMessageType = lastMessage.body.type
  if (lastMessageType === "polling") {
    lastMessage = messages[lastIndex - 2]
    lastMessageType = lastMessage.body.type
    return (
      <PollingMessage>
        {inputComponentMap(lastIndex - 1, navigation)[lastMessageType]}
      </PollingMessage>
    )
  }
  return inputComponentMap(lastIndex, navigation)[lastMessageType]
}

export default class Chat extends React.Component {
  componentDidMount() {
    this.props.getMessages()
  }

  render() {
    let headerLeft
    if (
      this.props.insurance.status === "INACTIVE" ||
      this.props.insurance.status === "ACTIVE"
    ) {
      headerLeft = (
        <NavigateBackButton onPress={() => this.props.showDashboard()} />
      )
    }
    return (
      <StyledChatContainer>
        <NavBar
          title="Hedvig"
          headerLeft={
            headerLeft
          }
          headerRight={
            <ChatNavRestartButton
              onPress={() => this.props.resetConversation()}
            />
          }
        />
        <StyledMessageAndResponseArea behaviour="padding">
          <StyledMessageArea>
            <MessageList />
          </StyledMessageArea>
          <StyledResponseArea>
            {getInputComponent(this.props.messages, this.props.navigation)}
          </StyledResponseArea>
        </StyledMessageAndResponseArea>
      </StyledChatContainer>
    )
  }
}

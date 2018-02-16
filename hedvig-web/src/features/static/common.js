import React from "react"
import { connect } from "react-redux"
import { Link } from "react-router-dom";
import { SlideDownCtaButton } from "../../components/styles/button"


export const SayHi = connect(
  undefined,
  dispatch => ({
    registerCtaClick: () => dispatch({type: "ANALYTICS/CTA_CLICK", payload: {location: "header"}})
  })
)(({registerCtaClick}) => (
  <Link to="/chat" onClick={registerCtaClick}>
    <SlideDownCtaButton>
      Sätt upp mig på väntelistan
    </SlideDownCtaButton>
  </Link>
))

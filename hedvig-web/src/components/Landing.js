import React from "react"
import { keyframes } from "styled-components"
import { connect } from "react-redux"
import { Link } from "react-router-dom"

import Collaboration from "./landing/Collaboration"
import Splash from "./landing/Splash"
import ClaimDemo from "./landing/ClaimDemo"
import AssetTrackerDemo from "./landing/AssetTrackerDemo"
import Footer from "./Footer"
import { Header } from "../components/Header"
import ModelExplanation from "./landing/ModelExplanation"
import WaterDamagePage from "./landing/WaterDamagePage";
import CallToAction from "./landing/CallToAction"
import { TurquoiseRoundedButtonStyled } from "./styles/button";
require("purecss/build/base.css")
require("purecss/build/grids.css")
require("purecss/build/grids-responsive.css")

const SlideDownAnimation = keyframes`
  from {
    margin-bottom: 20px;
  }

  to {
    margin-bottom: 0px;
  }
`

const AnimatedCtaButton = TurquoiseRoundedButtonStyled.extend`
  overflow-y: hidden;
  animation: ${SlideDownAnimation} 0.1s linear;
  margin-left: auto;
  white-space: nowrap;
  font-size: 16px;
`

const HidingCtaButton = ({isVisible}) => (
  !isVisible ? (
    <Link to="/chat"><AnimatedCtaButton>Säg hej till Hedvig</AnimatedCtaButton></Link>
  ) : (<div></div>)
)

const ConnectedHidingCtaButton = connect(
  state => ({
    isVisible: state.landing.visible
  })
)(HidingCtaButton)

const Landing = () => { // Fragment is used as a quick hack to render nothing in place of a better solution right now
  return (
    <div>
      <Header headerRight={<ConnectedHidingCtaButton />}/>
      <Splash />
      <WaterDamagePage />
      <ClaimDemo />
      <AssetTrackerDemo />
      <ModelExplanation />
      <CallToAction />
      <Collaboration />
      <Footer />
    </div>
  )
}

export default Landing

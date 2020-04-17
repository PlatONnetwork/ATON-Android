/*
 *  Copyright (c) 2013, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 *
 */

package com.platon.aton.component.widget.togglebutton;

/**
 * Data structure for storing spring configuration.
 */
public class SpringConfig {
  private double friction;
  private double tension;

  public static SpringConfig defaultConfig = SpringConfig.fromOrigamiTensionAndFriction(40, 7);

  /**
   * constructor for the SpringConfig
   * @param tension tension value for the SpringConfig
   * @param friction friction value for the SpringConfig
   */
  public SpringConfig(double tension, double friction) {
    this.tension = tension;
    this.friction = friction;
  }

  /**
   * A helper to make creating a SpringConfig easier with values-en mapping to the Origami values-en.
   * @param qcTension tension as defined in the Quartz Composition
   * @param qcFriction friction as defined in the Quartz Composition
   * @return a SpringConfig that maps to these values-en
   */
  public static SpringConfig fromOrigamiTensionAndFriction(double qcTension, double qcFriction) {
    return new SpringConfig(
        OrigamiValueConverter.tensionFromOrigamiValue(qcTension),
        OrigamiValueConverter.frictionFromOrigamiValue(qcFriction)
    );
  }

  public double getFriction() {
    return friction;
  }

  public void setFriction(double friction) {
    this.friction = friction;
  }

  public double getTension() {
    return tension;
  }

  public void setTension(double tension) {
    this.tension = tension;
  }
}

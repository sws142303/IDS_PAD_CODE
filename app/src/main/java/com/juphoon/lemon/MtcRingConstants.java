/**
 * @file MtcRingConstants.java
 * @brief MtcRingConstants constants
 */
/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.juphoon.lemon;

/**
 * @brief MtcRingConstants constants
 */
public interface MtcRingConstants {
  public final static int INVALIDID = -1; /**< @brief invalid id */
  public final static int ZOK = 0; /**< @brief ok */
  public final static int ZFAILED = 1; /**< @brief failed */
  public final static int MTC_RING_FOREVER = 0;
  public final static int MTC_RING_DTMF_LEN = 200;
  public final static int MTC_RING_ALERT_LEN = 500;
  public final static int MTC_RING_TERM_LEN = 2000;
  public final static int MTC_RING_ASSET_MASK = 0x80000000;
  // EN_MTC_RING_TYPE 
  public final static int EN_MTC_RING_TONE_0 = 0; /**< @brief tone 0 */
  public final static int EN_MTC_RING_TONE_1 = EN_MTC_RING_TONE_0 + 1; /**< @brief tone 1 */
  public final static int EN_MTC_RING_TONE_2 = EN_MTC_RING_TONE_1 + 1; /**< @brief tone 2 */
  public final static int EN_MTC_RING_TONE_3 = EN_MTC_RING_TONE_2 + 1; /**< @brief tone 3 */
  public final static int EN_MTC_RING_TONE_4 = EN_MTC_RING_TONE_3 + 1; /**< @brief tone 4 */
  public final static int EN_MTC_RING_TONE_5 = EN_MTC_RING_TONE_4 + 1; /**< @brief tone 5 */
  public final static int EN_MTC_RING_TONE_6 = EN_MTC_RING_TONE_5 + 1; /**< @brief tone 6 */
  public final static int EN_MTC_RING_TONE_7 = EN_MTC_RING_TONE_6 + 1; /**< @brief tone 7 */
  public final static int EN_MTC_RING_TONE_8 = EN_MTC_RING_TONE_7 + 1; /**< @brief tone 8 */
  public final static int EN_MTC_RING_TONE_9 = EN_MTC_RING_TONE_8 + 1; /**< @brief tone 9 */
  public final static int EN_MTC_RING_TONE_STAR = EN_MTC_RING_TONE_9 + 1; /**< @brief tone * */
  public final static int EN_MTC_RING_TONE_POUND = EN_MTC_RING_TONE_STAR + 1; /**< @brief tone # */
  public final static int EN_MTC_RING_RING = EN_MTC_RING_TONE_POUND + 1; /**< @brief ring */
  public final static int EN_MTC_RING_RING_BACK = EN_MTC_RING_RING + 1; /**< @brief ring back */
  public final static int EN_MTC_RING_CALL_FAILED = EN_MTC_RING_RING_BACK + 1; /**< @brief call failed */
  public final static int EN_MTC_RING_BUSY = EN_MTC_RING_CALL_FAILED + 1; /**< @brief busy */
  public final static int EN_MTC_RING_CALL_WAIT = EN_MTC_RING_BUSY + 1; /**< @brief call waiting */
  public final static int EN_MTC_RING_FORWARD = EN_MTC_RING_CALL_WAIT + 1; /**< @brief forwarding */
  public final static int EN_MTC_RING_TERM = EN_MTC_RING_FORWARD + 1; /**< @brief call terminated */
  public final static int EN_MTC_RING_HELD = EN_MTC_RING_TERM + 1; /**< @brief call held */
  public final static int EN_MTC_RING_MSG_RECV = EN_MTC_RING_HELD + 1; /**< @brief message received */
  public final static int EN_MTC_RING_SIZE = EN_MTC_RING_MSG_RECV + 1; /**< @brief ring size */

}

/**
 * @file MtcCallDbConstants.java
 * @brief MtcCallDbConstants constants
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
 * @brief MtcCallDbConstants constants
 */
public interface MtcCallDbConstants {
  public final static int INVALIDID = -1; /**< @brief invalid id */
  public final static int ZOK = 0; /**< @brief ok */
  public final static int ZFAILED = 1; /**< @brief failed */
  public final static int MTC_PRIVACY_NONE = 0x01; /**< @brief no privacy support */
  public final static int MTC_PRIVACY_ID = 0x02; /**< @brief using privacy id */
  public final static int MTC_PRIVACY_HEADER = 0x03; /**< @brief using privacy header */
  public final static int MTC_PRIVACY_SESSION = 0x04; /**< @brief using privacy session */
  public final static int MTC_PRIVACY_CRITICAL = 0x05; /**< @brief using privacy critical */
  // EN_MTC_DB_DTMF_TYPE 
  public final static int EN_MTC_DB_DTMF_AUTO = 0; /**< @brief auto select inband and outband */
  public final static int EN_MTC_DB_DTMF_INBAND = EN_MTC_DB_DTMF_AUTO + 1; /**< @brief inband dtmf */
  public final static int EN_MTC_DB_DTMF_OUTBAND = EN_MTC_DB_DTMF_INBAND + 1; /**< @brief rfc2833 */
  public final static int EN_MTC_DB_DTMF_INFO = EN_MTC_DB_DTMF_OUTBAND + 1; /**< @brief INFO message(CISCO format) */
  public final static int EN_MTC_DB_DTMF_INFO_HW = EN_MTC_DB_DTMF_INFO + 1; /**< @brief INFO message(Huawei format) */

  // EN_MTC_ENCODING_TYPE 
  public final static int EN_MTC_ENCODING_H264 = 0; /**< @brief H.264 */
  public final static int EN_MTC_ENCODING_VP8 = EN_MTC_ENCODING_H264 + 1; /**< @brief VP8 */

  // EN_MTC_DB_ARS_TYPE 
  public final static int EN_MTC_DB_ARS_LD = 0; /**< @brief Low Difinition */
  public final static int EN_MTC_DB_ARS_SD = EN_MTC_DB_ARS_LD + 1; /**< @brief Standard Difinition */
  public final static int EN_MTC_DB_ARS_HD = EN_MTC_DB_ARS_SD + 1; /**< @brief High Difinition */

  // EN_MTC_DB_NET_TYPE 
  public final static int EN_MTC_DB_NET_3G = 0; /**< @brief 3G */
  public final static int EN_MTC_DB_NET_LAN = EN_MTC_DB_NET_3G + 1; /**< @brief LAN */
  public final static int EN_MTC_DB_NET_WIFI = EN_MTC_DB_NET_LAN + 1; /**< @brief WIFI */

  // EN_MTC_DB_H264_PROFILE_TYPE 
  public final static int EN_MTC_DB_H264_PROFILE_BASELINE = 0x42; /**< @brief H264 baseline profile */
  public final static int EN_MTC_DB_H264_PROFILE_MAIN = 0x4d; /**< @brief H264 main profile */
  public final static int EN_MTC_DB_H264_PROFILE_HIGH = 0x64; /**< @brief H264 high profile */

  // EN_MTC_DB_SESSION_TIME_TYPE 
  public final static int EN_MTC_DB_SESSION_TIME_OFF = 0; /**< @brief Session timer off*/
  public final static int EN_MTC_DB_SESSION_TIME_NEGO = EN_MTC_DB_SESSION_TIME_OFF + 1; /**< @brief Session timer negotiation */
  public final static int EN_MTC_DB_SESSION_TIME_FORCE = EN_MTC_DB_SESSION_TIME_NEGO + 1; /**< @brief Session timer force on */

  // EN_MTC_DB_SRTP_CRYPTO_TYPE 
  public final static int EN_MTC_DB_SRTP_CRYPTO_OFF = 0; /**< @brief SRTP off */
  public final static int EN_MTC_DB_SRTP_CRYPTO_AES128_HMAC80 = EN_MTC_DB_SRTP_CRYPTO_OFF + 1; /**< @brief SRTP AES-128 HMAC-80 */
  public final static int EN_MTC_DB_SRTP_CRYPTO_AES128_HMAC32 = EN_MTC_DB_SRTP_CRYPTO_AES128_HMAC80 + 1; /**< @brief SRTP AES-128 HMAC-32 */

  // EN_MTC_DB_PREFIX_OPT_TYPE 
  public final static int EN_MTC_DB_PREFIX_OPT_NO_USE = 0; /* do not use call prefix */
  public final static int EN_MTC_DB_PREFIX_OPT_CONFIRM = EN_MTC_DB_PREFIX_OPT_NO_USE + 1; /* need user to confirm use or not */
  public final static int EN_MTC_DB_PREFIX_OPT_FORCE = EN_MTC_DB_PREFIX_OPT_CONFIRM + 1; /* force to use call prefix */

}
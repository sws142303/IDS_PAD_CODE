/**
 * @file MtcMedia.java
 * @brief MtcMedia interface
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
 * @brief MtcMedia interface
 */
public class MtcMedia implements MtcMediaConstants {
/**
 * @brief Get audio input device.
 *
 * @return The audio input device name when open successfully, 
 *              otherwise return empty string.
 * The caller must copy it, then use.
 *
 * @see @ref MtcMedia::Mtc_AudioSetInputDev
 */
  public static String Mtc_AudioGetInputDev() {
    return MtcMediaJNI.Mtc_AudioGetInputDev();
  }

/**
 * @brief Set audio input device.
 *
 * @param [in] *pcName audio input device codec name.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 *
 * @see @ref MtcMedia::Mtc_AudioGetInputDev
 */
  public static int Mtc_AudioSetInputDev(String pcName) {
    return MtcMediaJNI.Mtc_AudioSetInputDev(pcName);
  }

/**
 * @brief Get the count of audio input device.
 *
 * @return The audio input device count on successfully, otherwise return 0
 *
 * @see @ref MtcMedia::Mtc_AudioEnumInputDev
 */
  public static int Mtc_AudioGetInputDevCnt() {
    return MtcMediaJNI.Mtc_AudioGetInputDevCnt();
  }

/**
 * @brief Enumerate audio input device.
 *
 * @param [in] iIndex Index of audio input device enumeration.
 *
 * @return The audio input device name when open successfully,
 *              otherwise return empty string.
 * The caller must copy it, then use.
 *
 * @see @ref MtcMedia::Mtc_AudioGetInputDevCnt
 */
  public static String Mtc_AudioEnumInputDev(int iIndex) {
    return MtcMediaJNI.Mtc_AudioEnumInputDev(iIndex);
  }

/**
 * @brief Get audio input stream type @ref EN_MTC_AUIDO_STREAM_TYPE.
 *
 * @return The audio input stream type.
 *
 * @see @ref MtcMedia::Mtc_AudioGetOutputStreamType
 */
  public static int Mtc_AudioGetInputStreamType() {
    return MtcMediaJNI.Mtc_AudioGetInputStreamType();
  }

/**
 * @brief Set input volume.
 *
 * @param [in] dwLevel Volume value between 0 and 100.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 *
 * @see MtcMedia::Mtc_AudioGetInputVol
 */
  public static int Mtc_AudioSetInputVol(int dwLevel) {
    return MtcMediaJNI.Mtc_AudioSetInputVol(dwLevel);
  }

/**
 * @brief Get input volume.
 *
 * @return Current input volume value.
 *
 * @see MtcMedia::Mtc_AudioSetInputVol
 */
  public static int Mtc_AudioGetInputVol() {
    return MtcMediaJNI.Mtc_AudioGetInputVol();
  }

/**
 * @brief Mute or unmute input.
 *
 * @param [in] bMute true to mute input, false to unmute input.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 */
  public static int Mtc_AudioMuteInput(boolean bMute) {
    return MtcMediaJNI.Mtc_AudioMuteInput(bMute);
  }

/**
 * @brief Mute or unmute input.
 *
 * @param [in] bMute true to mute input, false to unmute input.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 */
  public static int Mtc_AudioMuteInputX(boolean bMute) {
    return MtcMediaJNI.Mtc_AudioMuteInputX(bMute);
  }

/**
 * @brief Get input mute state.
 *
 * @return true input is muted, false input is not muted
 */
  public static boolean Mtc_AudioIsMuteInputX() {
    return MtcMediaJNI.Mtc_AudioIsMuteInputX();
  }

/**
 * @brief Get audio output device.
 *
 * @return The audio output device name when open successfully,
 *              otherwise return empty string.
 * The caller must copy it, then use.
 *
 * @see @ref MtcMedia::Mtc_AudioSetOutputDev
 */
  public static String Mtc_AudioGetOutputDev() {
    return MtcMediaJNI.Mtc_AudioGetOutputDev();
  }

/**
 * @brief Set audio output device.
 *
 * @param [in] pcName audio input device enumeration codec name.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 *
 * @see @ref MtcMedia::Mtc_AudioGetOutputDev
 */
  public static int Mtc_AudioSetOutputDev(String pcName) {
    return MtcMediaJNI.Mtc_AudioSetOutputDev(pcName);
  }

/**
 * @brief Get the count of audio output device.
 *
 * @return Count of audio output device on successfully, 
 *              otherwise return 0.
 *
 * @see @ref MtcMedia::Mtc_AudioEnumOutputDev
 */
  public static int Mtc_AudioGetOutputDevCnt() {
    return MtcMediaJNI.Mtc_AudioGetOutputDevCnt();
  }

/**
 * @brief Enumerate of audio output device.
 *
 * @param [in] iIndex Index of audio output device enumeration.
 *
 * @return The audio output device name on successfully, 
 *              otherwise return empty string.
 * The caller must copy it, then use.
 *
 * @see @ref MtcMedia::Mtc_AudioGetOutputDevCnt
 */
  public static String Mtc_AudioEnumOutputDev(int iIndex) {
    return MtcMediaJNI.Mtc_AudioEnumOutputDev(iIndex);
  }

/**
 * @brief Get audio output stream type @ref EN_MTC_AUIDO_STREAM_TYPE.
 *
 * @return The audio output stream type.
 *
 * @see @ref MtcMedia::Mtc_AudioGetInputStreamType
 */
  public static int Mtc_AudioGetOutputStreamType() {
    return MtcMediaJNI.Mtc_AudioGetOutputStreamType();
  }

/**
 * @brief Set output volume.
 *
 * @param [in] dwLevel Volume value between 0 and 100.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 *
 * @see MtcMedia::Mtc_AudioGetInputVol
 */
  public static int Mtc_AudioSetOutputVol(int dwLevel) {
    return MtcMediaJNI.Mtc_AudioSetOutputVol(dwLevel);
  }

/**
 * @brief Get output volume.
 *
 * @return Current output volume value.
 *
 * @see MtcMedia::Mtc_AudioSetInputVol
 */
  public static int Mtc_AudioGetOutputVol() {
    return MtcMediaJNI.Mtc_AudioGetOutputVol();
  }

/**
 * @brief Mute or unmute output.
 *
 * @param [in] bMute true to mute output, false to unmute output.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 */
  public static int Mtc_AudioMuteOutput(boolean bMute) {
    return MtcMediaJNI.Mtc_AudioMuteOutput(bMute);
  }

/**
 * @brief Get video input device.
 *
 * @return The video output device name, device when open successfully, 
 *              otherwise return empty string.
 * The caller must copy it, then use.
 *
 * @see @ref MtcMedia::Mtc_VideoSetInputDev
 */
  public static String Mtc_VideoGetInputDev() {
    return MtcMediaJNI.Mtc_VideoGetInputDev();
  }

/**
 * @brief Set video input device.
 *
 * @param [in] *pcName video input device codec name.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 *
 * @see @ref MtcMedia::Mtc_VideoGetInputDev
 */
  public static int Mtc_VideoSetInputDev(String pcName) {
    return MtcMediaJNI.Mtc_VideoSetInputDev(pcName);
  }

/**
 * @brief Get the count of video input device.
 *
 * @return video input device count on successfully, otherwise return 0.
 *
 * @see @ref MtcMedia::Mtc_VideoEnumInputDev
 */
  public static int Mtc_VideoGetInputDevCnt() {
    return MtcMediaJNI.Mtc_VideoGetInputDevCnt();
  }

/**
 * @brief Enumerate video input device.
 *
 * @param [in] iIndex Index of video input device enumeration.
 *
 * @return The video input device name on successfully, 
 *              otherwise return empty string.
 * The caller must copy it, then use.
 *
 * @see @ref MtcMedia::Mtc_VideoGetInputDevCnt
 */
  public static String Mtc_VideoEnumInputDev(int iIndex) {
    return MtcMediaJNI.Mtc_VideoEnumInputDev(iIndex);
  }

/**
 * @brief Set the clockwise rotation angle of the camera image.
 *
 * @param [in] iDegree Camera rotation angle, must be one of 0, 90, 180 or 270
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 */
  public static int Mtc_VideoRotateCamera(int iDegree) {
    return MtcMediaJNI.Mtc_VideoRotateCamera(iDegree);
  }

/**
 * @brief Get the count of camera capability.
 *
 * @return The camera capability count on successfully, otherwise return 0
 *
 * @see @ref MtcMedia::Mtc_VideoGetCamCap
 */
  public static int Mtc_VideoGetCamCapCnt() {
    return MtcMediaJNI.Mtc_VideoGetCamCapCnt();
  }

/** 
 * @brief Get current camera capability.
 *
 * @param [in] iIndex Index of camera capability.
 * @param [in,out] piWidth the Capture width of specific camera capability.
 * @param [in,out] piHeight the Capture height of specific camera capability.
 * @param [in,out] piMaxFps the Max frame rate of specific camera capability.
 * @retval ZOK on succeed.
 * @retval ZFAILED on failure.
 *
 * @see MtcMedia::Mtc_VideoGetCamCapCnt
 */
  public static int Mtc_VideoGetCamCap(int iIndex, MtcNumber piWidth, MtcNumber piHeight, MtcNumber piMaxFps) {
    return MtcMediaJNI.Mtc_VideoGetCamCap(iIndex, piWidth, piHeight, piMaxFps);
  }

/**
 * @brief Calculate rectangle of remote image.
 *
 * @param [in] iMode Display mode @ref MtcMediaConstants::EN_MTC_DISPLAY_FULL_CONTENT.
 * @param [in] pstScreen Screen rectangle.
 * @param [in,out] pstRemoteRect Rectangle of remote image.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 */
  public static int Mtc_VideoCalcRect(int iMode, ST_MTC_RECT pstScreen, ST_MTC_RECT pstRemoteRect) {
    return MtcMediaJNI.Mtc_VideoCalcRect(iMode, ST_MTC_RECT.getCPtr(pstScreen), pstScreen, ST_MTC_RECT.getCPtr(pstRemoteRect), pstRemoteRect);
  }

/**
 * @brief Calculate rectangle of remote and local image.
 *
 * @param [in] iMode Display mode @ref MtcMediaConstants::EN_MTC_DISPLAY_FULL_CONTENT.
 * @param [in] pstScreen Rectangle of remote image.
 * @param [in,out] pstRemoteRect Rectangle of remote image.
 * @param [in,out] pstLocalRect Rectangle of local image.
 *
 * @retval ZOK on successfully.
 * @retval ZFAILED on failed.
 */
  public static int Mtc_VideoCalcRectX(int iMode, ST_MTC_RECT pstScreen, ST_MTC_RECT pstRemoteRect, ST_MTC_RECT pstLocalRect) {
    return MtcMediaJNI.Mtc_VideoCalcRectX(iMode, ST_MTC_RECT.getCPtr(pstScreen), pstScreen, ST_MTC_RECT.getCPtr(pstRemoteRect), pstRemoteRect, ST_MTC_RECT.getCPtr(pstLocalRect), pstLocalRect);
  }

/**
 * @brief Check the video file format.
 *
 * @param [in] pcFileName video file name.
 *
 * @retval true Video file format is support.
 * @retval false Video fil format is not support.
 */
  public static boolean Mtc_VideoCheckFormat(String pcFileName) {
    return MtcMediaJNI.Mtc_VideoCheckFormat(pcFileName);
  }

}
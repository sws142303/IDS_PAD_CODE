package cloud.Monitor.VideoSender;

public interface IVideoSenderCallback {
	
	
	/********************************
	 * ������:	OnSessionConnectStatus
	 * ˵��:		�����������״̬�ص�
	 * @param:	status	����״̬
	 * @return:	void
	 */
	public void OnSessionConnectStatus(int status);
	
	/********************************
	 * ������:	OnDispatchCmd
	 * ˵��:		���������͸��ֻ��˵�����ص�
	 * @param:	cmd				����id
	 * @param:	nSourceId		����Դid
	 * @param:	SourceContent	������Ϣ����
	 * @return:	void
	 */
	public void OnDispatchCmd(int cmd, int nSourceId, String SourceContent);
	
	/*********************************
	 * ������:	OnUpLoadFile
	 * ˵��:		�ļ��ϴ����Ȼص�
	 * @param:	strFileName		�ļ���
	 * @param:	nStatus			����״̬
	 * @param:	nStatusReport	����
	 * @return:	void
	 */
	public void OnUpLoadFile(String strFileName, int nStatus, double nStatusReport);
	
	/**********************************
	 * ������:	OnDownLoadFile
	 * ˵��:		�����ļ����Ȼص�
	 * @param:	strFileName		�ļ���
	 * @param:	nStatus			����״̬
	 * @param:	nStatusReport	����
	 * @return:	void
	 */
	public void OnDownLoadFile(String strFileName, int nStatus, double nStatusReport);
	
}

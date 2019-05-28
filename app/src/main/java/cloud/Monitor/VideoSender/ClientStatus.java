package cloud.Monitor.VideoSender;

public class ClientStatus {
	 
	public static final   int		CS_CONNECTING=0;		//��������
	public static final   int		CS_FAILED = 1;				//�޷�����
	public static final   int		CS_CONNECTED = 2;		//�Ѿ�����
	public static final   int		CS_DISCONNECTED=3;		//�Ͽ�����
	public static final   int		CS_BUSY = 4;				//����æ(�ѶϿ�������)
	public static final   int		CS_RECONNECTED = 5;		//�����ɹ�
	public static final   int		CS_IDLE = 6;				//����
	public static final   int		CS_RESTARTED = 7;			//���������ˡ����ӶϿ��ˣ������������������ˣ����ǻ���һ�������ӡ�
	
	public static final   int		kTextMessage  = 1;
	public static final   int		kImageMessage = 2;
	public static final   int       kAudioMessage = 3;
	
	public static final   int       kBroadcastMsg = 1;
	public static final   int       kExchangeMsg  = 2;
	public static final   int       kToCenterMsg  = 3;
}

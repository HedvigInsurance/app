import Config from '@hedviginsurance/react-native-config';
import DeviceInfo from 'react-native-device-info';

const getDeviceInfo = () => ({
  linkingUri: `${Config.APP_SCHEME}://`,
  model: DeviceInfo.getModel(),
  deviceId: DeviceInfo.getDeviceId(),
  uniqueId: DeviceInfo.getUniqueID(),
  version: DeviceInfo.getVersion(),
});

export { getDeviceInfo };
